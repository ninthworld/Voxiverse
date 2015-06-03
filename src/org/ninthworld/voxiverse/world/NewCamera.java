package org.ninthworld.voxiverse.world;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.ninthworld.voxiverse.util.*;

public class NewCamera {
	
	private WorldVector3f focusPos;
	private WorldVector3f cameraPos;
	private WorldVector3f cameraRot;
	private float focusDist;
	private float moveSpeed;
	private float mouseSensitivity;
	private float maxLook;
	
	public float debug_focusDist;
	public WorldVector3f debug_cameraPos;
	
	public NewCamera(){
		this.focusPos = new WorldVector3f(0, 0, 0);
		this.cameraPos = new WorldVector3f(0, 0, 0);
		this.cameraRot = new WorldVector3f((float)Math.PI/6f, 0, 0);
		this.focusDist = 5f*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE;
		this.moveSpeed = 0.8f;
		this.mouseSensitivity = 0.005f;
		this.maxLook = 85f;
		
		this.debug_focusDist = 4f*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE;
		this.debug_cameraPos = new WorldVector3f(0, 0, 0);
	}
	
	public void renderCamera(){
		GL11.glColor3f(1, 0, 0);
		RenderUtils.drawCubeWireframe(focusPos, new WorldVector3f(Chunk.VOXEL_SIZE/2f, Chunk.VOXEL_SIZE/2f, Chunk.VOXEL_SIZE/2f));
	}
	
	public void acceptInput(float delta){
		acceptInputZoom();
		acceptInputRotate(2);
		acceptInputGrab();
		acceptInputMove(delta);
		
		moveSpeed = focusDist/2048f;
	}
	
	public void acceptInputZoom(){
		float dWheel = (float) Mouse.getDWheel();
		if(Math.abs(dWheel) > 0){
			focusDist -= dWheel * 0.5f;
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			focusDist -= 8f;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			focusDist += 8f;
		}
		
		focusDist = (float) Math.max(focusDist, 0);
	}
	
	public void acceptInputGrab(){
		if(Mouse.isInsideWindow() && Mouse.isButtonDown(1)){
			Mouse.setGrabbed(true);
		}else{
			Mouse.setGrabbed(false);
		}
	}
	
	public void acceptInputRotate(float delta){
		if(Mouse.isGrabbed()) {
            float mouseDX = Mouse.getDX();
            float mouseDY = -Mouse.getDY();
            cameraRot.y += mouseDX * mouseSensitivity * delta;
            cameraRot.x += mouseDY * mouseSensitivity * delta;
            cameraRot.x = Math.max(-maxLook, Math.min(maxLook, cameraRot.x));
            
            cameraRot.x = (float) Math.min(Math.max(cameraRot.x, 0.01f), Math.PI/2f-0.001f);
        }
	}
	
	public void acceptInputMove(float delta){
		boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean keyFast = Keyboard.isKeyDown(Keyboard.KEY_Q);
        boolean keySlow = Keyboard.isKeyDown(Keyboard.KEY_E);
        boolean keyFlyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        boolean keyFlyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        
        float speed;

        if(keyFast) {
            speed = moveSpeed * 5;
        }
        else if(keySlow) {
            speed = moveSpeed / 2;
        }
        else {
            speed = moveSpeed;
        }

        speed *= delta;

        if(keyFlyUp) {
            focusPos.y += speed;
        }
        if(keyFlyDown) {
        	focusPos.y -= speed;
        }

        if(keyDown) {
        	focusPos.x -= Math.sin(cameraRot.y) * speed;
        	focusPos.z += Math.cos(cameraRot.y) * speed;
        }
        if(keyUp) {
        	focusPos.x += Math.sin(cameraRot.y) * speed;
        	focusPos.z -= Math.cos(cameraRot.y) * speed;
        }
        if(keyLeft) {
        	focusPos.x += Math.sin(cameraRot.y - Math.PI/2f) * speed;
        	focusPos.z -= Math.cos(cameraRot.y - Math.PI/2f) * speed;
        }
        if(keyRight) {
        	focusPos.x += Math.sin(cameraRot.y + Math.PI/2f) * speed;
        	focusPos.z -= Math.cos(cameraRot.y + Math.PI/2f) * speed;
        }
	}
	
	public void update(){
		cameraPos.x = focusPos.x - (float)( Math.sin( cameraRot.x + Math.PI/2f ) * Math.cos( cameraRot.y - Math.PI/2f ) * focusDist );
		cameraPos.y = focusPos.y - (float)( Math.cos( cameraRot.x - Math.PI/2f ) * -focusDist );
		cameraPos.z = focusPos.z - (float)( Math.sin( cameraRot.x + Math.PI/2f ) * Math.sin( cameraRot.y - Math.PI/2f ) * focusDist );
		
		debug_cameraPos.x = focusPos.x - (float)( Math.sin( cameraRot.x + Math.PI/2f ) * Math.cos( cameraRot.y - Math.PI/2f ) * debug_focusDist );
		debug_cameraPos.y = focusPos.y - (float)( Math.cos( cameraRot.x - Math.PI/2f ) * -debug_focusDist );
		debug_cameraPos.z = focusPos.z - (float)( Math.sin( cameraRot.x + Math.PI/2f ) * Math.sin( cameraRot.y - Math.PI/2f ) * debug_focusDist );
	}
	
	public void apply(){
		GL11.glMatrixMode(GL11.GL_MATRIX_MODE);
		GL11.glLoadIdentity();
	    GLU.gluLookAt(cameraPos.x, cameraPos.y, cameraPos.z, focusPos.x, focusPos.y, focusPos.z, 0, 1, 0);
	}
	
	public WorldVector3f getFocusPos(){
		return focusPos;
	}
	
	public WorldVector3f getCameraPos(){
		return cameraPos;
	}
	
	public WorldVector3f getCameraRot(){
		return cameraRot;
	}
	
	public float getFocusDist(){
		return focusDist;
	}
	
	public void setFocusPos(WorldVector3f pos){
		this.focusPos = pos;
	}
	
	/*public void setCameraPos(WorldVector3f pos){
		this.cameraPos = pos;
	}
	
	public void setCameraRot(WorldVector3f rot){
		this.cameraRot = rot;
	}*/
	
	public void setFocusDist(float d){
		this.focusDist = d;
	}
}
