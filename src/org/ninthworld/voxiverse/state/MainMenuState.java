package org.ninthworld.voxiverse.state;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.gui.GUIButton;
import org.ninthworld.voxiverse.gui.GUIDisplay;
import org.ninthworld.voxiverse.util.Color;
import org.ninthworld.voxiverse.util.NewFont;
import org.ninthworld.voxiverse.util.WorldVector3f;

public class MainMenuState implements State {
	
	private GUIDisplay guiDisplay;
	
	@Override
	public void init(Game game) {
		guiDisplay = new GUIDisplay();
		
		guiDisplay.addGUIObject("New World", new GUIButton("New World", 0.5f, 0.2f, 0.2f, 0.1f));
		guiDisplay.addGUIObject("Load World", new GUIButton("Load World", 0.5f, 0.4f, 0.2f, 0.1f));
		guiDisplay.addGUIObject("Options", new GUIButton("Options", 0.5f, 0.6f, 0.2f, 0.1f));
		guiDisplay.addGUIObject("Exit", new GUIButton("Exit", 0.5f, 0.8f, 0.2f, 0.1f));
		
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
					if(guiDisplay.getGUIButton("New World").isMouseInBounds()){
						game.changeState(new NewWorldState());
					}
					if(guiDisplay.getGUIButton("Load World").isMouseInBounds()){
						game.changeState(new ChooseWorldState());
					}
					if(guiDisplay.getGUIButton("Options").isMouseInBounds()){
					}
					if(guiDisplay.getGUIButton("Exit").isMouseInBounds()){
						game.changeState(new QuitState());
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
