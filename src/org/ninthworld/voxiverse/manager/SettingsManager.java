package org.ninthworld.voxiverse.manager;

public class SettingsManager {
	private int width, height, renderDistance;
	private boolean sync, vsync;
	
	public SettingsManager(){
		this.width = 1080;
		this.height = 720;
		this.renderDistance = 6;
		this.sync = false;
		this.vsync = false;
	}
	
	public void loadSettings(){
		// settings.properties
	}
	
	public int getDisplayWidth(){
		return width;
	}
	
	public int getDisplayHeight(){
		return height;
	}
	
	public void setRenderDistance(int dist){
		if(dist >= 0){
			renderDistance = dist;
		}
	}
	
	public int getRenderDistance(){
		return renderDistance;
	}
	
	public boolean useDisplaySync(){
		return sync;
	}
	
	public boolean useDisplayVSync(){
		return vsync;
	}
}
