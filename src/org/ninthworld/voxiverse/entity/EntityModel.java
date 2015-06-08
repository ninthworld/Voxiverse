package org.ninthworld.voxiverse.entity;

import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.util.Color;
import org.ninthworld.voxiverse.util.RenderUtils;
import org.ninthworld.voxiverse.util.WorldVector3f;
import org.ninthworld.voxiverse.world.Model;

public class EntityModel extends Entity {

	private Model model;
	private float scale;
	
	private boolean isSelected;
	private boolean isHovered;
	private int countDown;
	
	public EntityModel(WorldVector3f pos, WorldVector3f rot, Model m, float scale){
		super(pos, rot);
		this.model = m;
		this.scale = scale;
		this.isHovered = false;
		this.isSelected = false;
		this.countDown = 0;
	}
	
	public void update(){
		updateHovered();
	}
	
	public boolean isSelected(){
		return isSelected;
	}
	
	public boolean isHovered(){
		return isHovered;
	}
	
	public void toggleSelected(){
		isSelected = !isSelected;
	}
	
	public void setHovered(){
		isHovered = true;
		countDown = 40;
	}
	
	public void updateHovered(){
		if(countDown > 0){
			countDown--;
		}else{
			countDown = 0;
			isHovered = false;
		}
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
	
	public WorldVector3f getCenterPos(){
		return new WorldVector3f(getPos().x + getHalf().x, getPos().y + getHalf().y, getPos().z + getHalf().z);
	}
	
	public WorldVector3f getHalf(){
		return new WorldVector3f((model.getRawData().length*scale)/2f, (model.getRawData()[0].length*scale)/2f, (model.getRawData()[0][0].length*scale)/2f);
	}
	
	public boolean isInBoundingBox(WorldVector3f p){
		return (p.x >= (getCenterPos().x-getHalf().x) &&
				p.x <= (getCenterPos().x+getHalf().x) &&
				p.y >= (getCenterPos().y-getHalf().y) &&
				p.y <= (getCenterPos().y+getHalf().y) &&
				p.z >= (getCenterPos().z-getHalf().z) &&
				p.z <= (getCenterPos().z+getHalf().z) );
	}
	
	public void renderBoundingBox(Color color){
		GL11.glPushMatrix();
			color.glColor();
			RenderUtils.drawCubeWireframe(getCenterPos(), getHalf());
		GL11.glPopMatrix();
	}
}
