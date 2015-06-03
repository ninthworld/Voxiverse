package org.ninthworld.voxiverse.manager;

import org.ninthworld.voxiverse.world.Model;

public class AssetManager {

	public ShaderManager diffuseShader;
	public ShaderManager deferredAOShader;
	public ShaderManager SSAOShader;
	
	public ModelManager modelManager;
	
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
		modelManager = new ModelManager();
		
		modelManager.loadModel("redFlower", Model.load("res/models/redFlower.model"), 2f);
		modelManager.loadModel("yellowFlower", Model.load("res/models/yellowFlower.model"), 2f);
		modelManager.loadModel("tallGrass", Model.load("res/models/tallGrass.model"), 2f);
		modelManager.loadModel("cactus", Model.load("res/models/cactus.model"), 2f);
		modelManager.loadModel("rock", Model.load("res/models/rock.model"), 2f);
	}
	
	public void cleanUp(){
		diffuseShader.cleanUp();
		deferredAOShader.cleanUp();
		SSAOShader.cleanUp();
	}
}
