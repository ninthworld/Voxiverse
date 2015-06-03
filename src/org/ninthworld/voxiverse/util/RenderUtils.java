package org.ninthworld.voxiverse.util;

import org.lwjgl.opengl.GL11;

public class RenderUtils {
	
	public static void drawCubeWireframe(WorldVector3f pos, WorldVector3f half){
		GL11.glPushMatrix();
		
		GL11.glTranslatef(pos.x, pos.y, pos.z);
		GL11.glScalef(half.x, half.y, half.z);
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex3f(-1, 1, -1);
			GL11.glVertex3f(1, 1, -1);
			GL11.glVertex3f(1, 1, 1);
			GL11.glVertex3f(-1, 1, 1);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex3f(-1, -1, -1);
			GL11.glVertex3f(1, -1, -1);
			GL11.glVertex3f(1, -1, 1);
			GL11.glVertex3f(-1, -1, 1);
		GL11.glEnd();
		
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3f(-1, 1, -1);
			GL11.glVertex3f(-1, -1, -1);
	
			GL11.glVertex3f(1, 1, -1);
			GL11.glVertex3f(1, -1, -1);
	
			GL11.glVertex3f(1, 1, 1);
			GL11.glVertex3f(1, -1, 1);
	
			GL11.glVertex3f(-1, 1, 1);
			GL11.glVertex3f(-1, -1, 1);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}
	
}
