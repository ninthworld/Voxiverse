package org.ninthworld.voxiverse.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.ninthworld.voxiverse.manager.ShaderManager;

public class NewFont {
	private static final int ROWS_COLS = 16;
	
	private int size;
	private int textureID;
	
	public NewFont(int size, int textureID){
		this.size = size;
		this.textureID = textureID;
	}
	
	public void bindFont(ShaderManager sm, Color color){
		sm.useProgram();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		GL20.glUniform1i(sm.shaderUniform("texture0"), 0);
		GL20.glUniform4f(sm.shaderUniform("inputColor"), color.r, color.g, color.b, color.a);
	}
	
	public void unbindFont(ShaderManager sm){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		sm.stopProgram();
	}
	
	public void cleanUp(){
		GL11.glDeleteTextures(textureID);
	}
	
	public int getWidth(String str){
		return size*str.length();
	}
	
	public int getHeight(){
		return size;
	}
	
	public void drawString(float x, float y, String str){
		boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		//color.glColor();
		for(int i=0; i<str.length(); i++){
			float row = (float)Math.floor((str.charAt(i))/(float)ROWS_COLS)/(float)ROWS_COLS;
			float col = ((str.charAt(i))%ROWS_COLS)/(float)ROWS_COLS;
			
			//System.out.println(row + ", " + col);
			//float size = 16f;
			GL11.glPushMatrix();
			GL11.glTranslatef(x+i*size, y, 0);
			//GL11.glScalef(currentSize/size, currentSize/size, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(col, row);
				GL11.glVertex2f(0, 0);
				
				GL11.glTexCoord2f(col + 1/(float)ROWS_COLS, row);
				GL11.glVertex2f(size, 0);
				
				GL11.glTexCoord2f(col + 1/(float)ROWS_COLS, row + 1/(float)ROWS_COLS);
				GL11.glVertex2f(size, size);
				
				GL11.glTexCoord2f(col, row + 1/(float)ROWS_COLS);
				GL11.glVertex2f(0, size);
			GL11.glEnd();
			GL11.glPopMatrix();
		}
		
		if(!blendEnabled)
			GL11.glDisable(GL11.GL_BLEND);
	}
}
