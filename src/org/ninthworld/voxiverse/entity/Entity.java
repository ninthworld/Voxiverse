package org.ninthworld.voxiverse.entity;

import org.ninthworld.voxiverse.util.*;

public class Entity {
	private WorldVector3f pos;
	private WorldVector3f rot;
	
	public Entity(){
		this.pos = WorldVector3f.Zero();
		this.rot = WorldVector3f.Zero();
	}
	
	public Entity(WorldVector3f pos, WorldVector3f rot){
		this.pos = pos;
		this.rot = rot;
	}
	
	public void setPos(WorldVector3f pos){
		this.pos = pos;
	}
	
	public WorldVector3f getPos(){
		return pos;
	}
	
	public void setRotation(WorldVector3f rot){
		this.rot.x = WorldVector3f.wrapRadian(rot.x);
		this.rot.y = WorldVector3f.wrapRadian(rot.y);
		this.rot.z = WorldVector3f.wrapRadian(rot.z);
	}
	
	public WorldVector3f getRotation(){
		return rot;
	}
}
