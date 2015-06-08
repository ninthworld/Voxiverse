package org.ninthworld.voxiverse.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

public class GUIButton extends GUIObject {
	private float x, y, w, h;
	private String label;
	private GUIButtonState state;
	
	public GUIButton(String label, float x, float y, float w, float h){
		this.label = label;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.state = GUIButtonState.NONE;
	}
	
	public String getLabel(){
		return label;
	}
	
	public float getOriginX(){
		return Display.getWidth()*x;
	}
	
	public float getOriginY(){
		return Display.getHeight()*y;
	}
	
	public boolean isMouseInBounds(){
		return inBounds(Mouse.getX(), Display.getHeight()-Mouse.getY());
	}
	
	public boolean inBounds(float mX, float mY){
		int displayW = Display.getWidth();
		int displayH = Display.getHeight();
		
		mX /= (float) displayW;
		mY /= (float) displayH;
		
		return (mX >= x-(w/2f) && mX <= x+(w/2f) && mY >= y-(h/2f) && mY <= y+(h/2f));
	}
	
	public void update(){
		if(isMouseInBounds()){
			state = GUIButtonState.HOVER;
		}else{
			state = GUIButtonState.NONE;
		}
	}
	
	public void render2D(){
		int displayW = Display.getWidth();
		int displayH = Display.getHeight();
		
		GL11.glPushMatrix();
		if(state == GUIButtonState.HOVER){
			GL11.glColor3f(1, 0, 0);
		}else{
			GL11.glColor3f(1, 1, 1);
		}
		GL11.glTranslatef(displayW*x, displayH*y, 0);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2f(-displayW*(w/2f), -displayH*(h/2f));
			GL11.glVertex2f(displayW*(w/2f), -displayH*(h/2f));
			GL11.glVertex2f(displayW*(w/2f), displayH*(h/2f));
			GL11.glVertex2f(-displayW*(w/2f), displayH*(h/2f));
		GL11.glEnd();
		GL11.glPopMatrix();
	}
}
