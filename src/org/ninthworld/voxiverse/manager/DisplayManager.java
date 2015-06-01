package org.ninthworld.voxiverse.manager;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class DisplayManager {

	public void init(SettingsManager settings){
		try {
			
			Display.setDisplayMode( new DisplayMode(settings.getDisplayWidth(), settings.getDisplayHeight()) );
			Display.setVSyncEnabled( settings.useDisplayVSync() );
			Display.create( new PixelFormat(4, 24, 0, 4) );
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	private void displayGL(){
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
	}
	
	public void display3D(){
		displayGL();
		
		GLU.gluPerspective(45f, ((float)Display.getWidth()/(float)Display.getHeight()), 1f, 10000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
	}

	public void display2D(){
		displayGL();
		
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
}
