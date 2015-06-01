package org.ninthworld.voxiverse.game;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.ninthworld.voxiverse.manager.*;
import org.ninthworld.voxiverse.state.*;

public class Game {
	
	private long lastFrame;
	private long lastFPS;
	private int fps;

	private int color_texID, depth_texID;
	
	private State gameState;
	public SettingsManager settingsManager;
	public AssetManager assetManager;
	private DisplayManager displayManager;
		
	public Game(){
		settingsManager = new SettingsManager();
		displayManager = new DisplayManager();
		assetManager = new AssetManager();
	}
	
	public void init(){
		settingsManager.loadSettings();
		displayManager.init(settingsManager);
		assetManager.loadBaseAssets();
		
		gameState = new LoadAssetsState();
		gameState.init(this);

		color_texID = makeTexture(null, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, Display.getWidth(), Display.getHeight());
		depth_texID = makeTexture(null, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, Display.getWidth(), Display.getHeight());
		
		loop();
	}
	
	private void loop(){
		
		getDelta();
		lastFPS = getTime();
		
		while( !(gameState instanceof QuitState) ){
			int delta = getDelta();
			
			//gameState.input(this, 40);
			gameState.input(this, delta);
			gameState.update(this, delta);
			render();
			
			updateFPS();
			Display.update();
			if(settingsManager.useDisplaySync()){
				Display.sync(60);
			}
			if(Display.isCloseRequested()){
				gameState = new QuitState();
			}
		}
		
		gameState.cleanUp(this);
		assetManager.cleanUp();
	}
	
	public void render(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);

		render3D();
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		render2D();
	}
	
	private void render3D(){
		displayManager.display3D();

		GL11.glPushMatrix();
			assetManager.diffuseShader.useProgram();
			gameState.render3D(this);
			assetManager.diffuseShader.stopProgram();
		GL11.glPopMatrix();
		
		frameSave(color_texID);
		frameSave(depth_texID);
	}
	
	private void render2D(){
		displayManager.display2D();

		assetManager.SSAOShader.useProgram();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depth_texID);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, color_texID);
		GL20.glUniform1i(assetManager.SSAOShader.shaderUniform("texture0"), 0);
		GL20.glUniform1i(assetManager.SSAOShader.shaderUniform("texture1"), 1);
		GL20.glUniform2f(assetManager.SSAOShader.shaderUniform("camerarange"), 10, 1000);
		GL20.glUniform2f(assetManager.SSAOShader.shaderUniform("screensize"), Display.getWidth(), Display.getHeight());
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(1, 0);		
			GL11.glVertex2f(Display.getWidth(), Display.getHeight());
			GL11.glTexCoord2f(1, 1);			
			GL11.glVertex2f(Display.getWidth(), 0);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, Display.getHeight());
		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		assetManager.SSAOShader.stopProgram();

		GL11.glPushMatrix();
			gameState.render2D(this);
		GL11.glPopMatrix();
	}
	
	public void changeState(State state){
		gameState.cleanUp(this);
		gameState = state;
		gameState.init(this);
	}
	
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		
		return delta;
	}
	
	public void updateFPS() {
		if(getTime() - lastFPS > 1000){
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	private static int allocateTexture(){
		IntBuffer textureHandle = BufferUtils.createIntBuffer(1);
		GL11.glGenTextures(textureHandle);
		return textureHandle.get(0);
	}
	
	private static int makeTexture(ByteBuffer pixels, int type, int type2, int w, int h){
		int textureHandle = allocateTexture();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, type, w, h, 0, type, type2, pixels);
		return textureHandle;
	}
	
	private void frameSave(int textureHandle){
		GL11.glColor4f(1,  1, 1, 1);
		GL11.glReadBuffer(GL11.GL_BACK);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Display.getWidth(), Display.getHeight());
	}	
}
