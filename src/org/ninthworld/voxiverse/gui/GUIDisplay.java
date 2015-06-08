package org.ninthworld.voxiverse.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.ninthworld.voxiverse.manager.FontManager;
import org.ninthworld.voxiverse.manager.ShaderManager;
import org.ninthworld.voxiverse.util.Color;
import org.ninthworld.voxiverse.util.NewFont;

public class GUIDisplay {
	
	private boolean visible;
	private HashMap<String, GUIObject> guiObjects;
	
	public GUIDisplay(){
		this.visible = true;
		this.guiObjects = new HashMap<String, GUIObject>();
	}
	
	public void addGUIObject(String str, GUIObject obj){
		guiObjects.put(str, obj);
	}
	
	public GUIObject getGUIObject(String str){
		return guiObjects.get(str);
	}
	
	public GUIButton getGUIButton(String str){
		return (GUIButton) getGUIObject(str);
	}
	
	public void setVisible(boolean val){
		visible = val;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public HashMap<String, GUIObject> getGUIObjectMap(){
		return guiObjects;
	}
	
	public void update(){
		for(Entry<String, GUIObject> entry : guiObjects.entrySet()){
			if(entry.getValue() instanceof GUIButton){
				GUIButton button = (GUIButton) entry.getValue();
				button.update();
			}
		}
	}
	
	public void render(FontManager fontManager, ShaderManager fontShader){
		for(Entry<String, GUIObject> entry : guiObjects.entrySet()){
			if(entry.getValue() instanceof GUIButton){
				GUIButton button = (GUIButton) entry.getValue();
				button.render2D();
			}
		}

		for(Entry<String, GUIObject> entry : guiObjects.entrySet()){
			if(entry.getValue() instanceof GUIButton){
				GUIButton button = (GUIButton) entry.getValue();
				
				NewFont font1 = fontManager.getFont("Font2");
				Color color = new Color(0f, 0f, 1f);
				font1.bindFont(fontShader, color);
				
				String str = button.getLabel();
				font1.drawString(button.getOriginX() - font1.getWidth(str)/2f, button.getOriginY() - font1.getHeight()/2f, str);
				
				font1.unbindFont(fontShader);
			}
		}
	}
}
