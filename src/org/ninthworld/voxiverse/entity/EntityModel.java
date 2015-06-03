package org.ninthworld.voxiverse.entity;

import org.ninthworld.voxiverse.util.WorldVector3f;
import org.ninthworld.voxiverse.world.Model;

public class EntityModel extends Entity {

	private Model model;
	private float scale;
	
	public EntityModel(WorldVector3f pos, WorldVector3f rot, Model m, float scale){
		super(pos, rot);
		this.model = m;
		this.scale = scale;
	}
	
	public void setModel(Model m){
		model = m;
	}
	
	public Model getModel(){
		return model;
	}
	
	public void setScale(float s){
		scale = s;
	}
	
	public float getScale(){
		return scale;
	}
}
