package org.ninthworld.voxiverse.manager;

public class AssetManager {

	public ShaderManager diffuseShader;
	public ShaderManager deferredAOShader;
	public ShaderManager SSAOShader;
	
	public AssetManager(){
	}
	
	public void loadBaseAssets(){
		
		diffuseShader = new ShaderManager();
		diffuseShader.loadShader("res/shaders/shader.vert", "res/shaders/shader.frag");
		
		deferredAOShader = new ShaderManager();
		deferredAOShader.loadShader("res/shaders/deferredAO.vert", "res/shaders/deferredAO.frag");
		
		SSAOShader = new ShaderManager();
		SSAOShader.loadShader("res/shaders/ssao.vert", "res/shaders/ssao.frag");
	}
	
	public void loadAssets(){
		
	}
	
	public void cleanUp(){
		diffuseShader.cleanUp();
		deferredAOShader.cleanUp();
		SSAOShader.cleanUp();
	}
}
