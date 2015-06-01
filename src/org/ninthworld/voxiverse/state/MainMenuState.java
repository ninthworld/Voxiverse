package org.ninthworld.voxiverse.state;

import org.lwjgl.input.Mouse;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.gui.GUIButton;

public class MainMenuState implements State {
	
	private GUIButton[] buttons;
	
	@Override
	public void init(Game game) {
		buttons = new GUIButton[4];
		buttons[0] = new GUIButton("New World", 0.5f, 0.2f, 0.2f, 0.1f);
		buttons[1] = new GUIButton("Load World", 0.5f, 0.4f, 0.2f, 0.1f);
		buttons[2] = new GUIButton("Options", 0.5f, 0.6f, 0.2f, 0.1f);
		buttons[3] = new GUIButton("Exit", 0.5f, 0.8f, 0.2f, 0.1f);
	}

	@Override
	public void update(Game game, int delta) {
		
	}

	@Override
	public void input(Game game, int delta) {
		for(GUIButton button : buttons){
			button.update();
		}
		
		if(Mouse.isButtonDown(0)){
			if(buttons[0].isMouseInBounds()){
				// New World Action
				game.changeState(new NewWorldState());
			}
			if(buttons[1].isMouseInBounds()){
				// Load World Action
				game.changeState(new LoadWorldState("world0"));
			}
			if(buttons[2].isMouseInBounds()){
				// Options Action
			}
			if(buttons[3].isMouseInBounds()){
				// Exit Action
				game.changeState(new QuitState());
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
		for(GUIButton button : buttons){
			button.render2D();
		}
	}

}
