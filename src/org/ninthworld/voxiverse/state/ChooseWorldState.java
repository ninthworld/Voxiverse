package org.ninthworld.voxiverse.state;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.gui.GUIButton;
import org.ninthworld.voxiverse.gui.GUIDisplay;
import org.ninthworld.voxiverse.gui.GUIObject;
import org.ninthworld.voxiverse.util.Color;
import org.ninthworld.voxiverse.util.NewFont;
import org.ninthworld.voxiverse.util.WorldVector3f;

public class ChooseWorldState implements State {
	
	private GUIDisplay guiDisplay;
	
	@Override
	public void init(Game game) {
		guiDisplay = new GUIDisplay();
		
		guiDisplay.addGUIObject("Back", new GUIButton("Back", 0.2f, 0.9f, 0.2f, 0.1f));
		
		File dir = new File("worlds\\");
		String[] list = dir.list();
		int size = 0;
		if(list != null && list.length > 0){
			for(String str : list){
				File f = new File(dir.getAbsolutePath()+"\\"+str);
				if(f.isDirectory()){
					guiDisplay.addGUIObject("option_"+str, new GUIButton(str, 0.5f, (++size)*0.2f, 0.2f, 0.1f));
				}
			}
		}
	}

	@Override
	public void update(Game game, int delta) {
		
	}

	@Override
	public void input(Game game, int delta) {
		
		guiDisplay.update();
		
		while(Mouse.next()){
			if(!Mouse.getEventButtonState()){
				if(Mouse.getEventButton() == 0){ // Left Button Released
					for(Entry<String, GUIObject> entry : guiDisplay.getGUIObjectMap().entrySet()){
						if(entry.getValue() instanceof GUIButton){
							GUIButton button = (GUIButton) entry.getValue();
							
							if(button.isMouseInBounds()){
								if(entry.getKey().equals("Back")){
									game.changeState(new MainMenuState());
								}
								if(entry.getKey().startsWith("option_")){
									game.changeState(new LoadWorldState(button.getLabel()));
								}
								
							}
						}
					}					
				}
			}
		}
	}

	@Override
	public void cleanUp(Game game) {

	}

	@Override
	public void render3D(Game game) {

	}

	@Override
	public void render2D(Game game) {
		guiDisplay.render(game.assetManager.fontManager, game.assetManager.fontShader);
	}

}
