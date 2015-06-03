package org.ninthworld.voxiverse.manager;

import java.util.HashMap;

import org.ninthworld.voxiverse.world.Model;

public class ModelManager {
	private HashMap<String, Model> models;
	private HashMap<String, Float> scales;
	
	public ModelManager(){
		models = new HashMap<String, Model>();
		scales = new HashMap<String, Float>();
	}
	
	public void loadModel(String name, Model m, float scale){
		m.createMeshes();
		models.put(name, m);
		scales.put(name, scale);
	}
	
	public Model getModel(String name){
		return models.get(name);
	}
	
	public float getScale(String name){
		return scales.get(name);
	}
}
