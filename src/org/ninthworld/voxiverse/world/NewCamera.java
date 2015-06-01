package org.ninthworld.voxiverse.world;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.util.*;

public class NewCamera {
	
	private WorldVector3f focusPos;
	private WorldVector3f cameraPos;
	private WorldVector3f cameraRot;
	private float focusDist;
	private float moveSpeed;
	private float mouseSensitivity;
	private float maxLook;
	
	public NewCamera(){
		this.focusPos = new WorldVector3f(0, 0, 0);
		this.cameraPos = new WorldVector3f(0, 0, 0);
		this.cameraRot = new WorldVector3f(0, 0, 0);
		this.focusDist = 5f*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE;
		this.moveSpeed = 0.8f;
		this.mouseSensitivity = 0.005f;
		this.maxLook = 85f;
	}
	
	public void acceptInput(float delta){
		acceptInputZoom();
		acceptInputRotate(2);
		acceptInputGrab();
		acceptInputMove(delta);
	}
	
	public void acceptInputZoom(){
		float dWheel = (float) Mouse.getDWheel();
		if(Math.abs(dWheel) > 0){
			focusDist -= dWheel * 0.5f;
			focusDist = (float) Math.max(focusDist, 0);
		}
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
            
            cameraRot.x = (float) Math.min(Math.max(cameraRot.x, 0.01f), Math.PI/2f);
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
	}
	
	public void apply(){
		GL11.glLoadIdentity();
		GL11.glRotatef((float) Math.toDegrees(cameraRot.z), 0, 0, 1);
		GL11.glRotatef((float) Math.toDegrees(cameraRot.x), 1, 0, 0);
		GL11.glRotatef((float) Math.toDegrees(cameraRot.y), 0, 1, 0);
		GL11.glTranslatef(-cameraPos.x, -cameraPos.y, -cameraPos.z);
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
