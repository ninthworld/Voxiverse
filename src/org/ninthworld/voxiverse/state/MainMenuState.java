package org.ninthworld.voxiverse.state;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.gui.GUIButton;
import org.ninthworld.voxiverse.util.Color;
import org.ninthworld.voxiverse.util.NewFont;
import org.ninthworld.voxiverse.util.WorldVector3f;

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
		
		while(Mouse.next()){
			if(!Mouse.getEventButtonState()){
				if(Mouse.getEventButton() == 0){
					// Left Button Released
					
					if(buttons[0].isMouseInBounds()){
						// New World Action
						game.changeState(new NewWorldState());
					}
					if(buttons[1].isMouseInBounds()){
						// Load World Action
						//game.changeState(new LoadWorldState("world0"));
						game.changeState(new ChooseWorldState());
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
		
		NewFont font1 = game.assetManager.fontManager.getFont("Font2");
		Color color = new Color(0f, 0f, 1f);
		font1.bindFont(game.assetManager.fontShader, color);
		
		for(GUIButton button : buttons){
			String str = button.getLabel();
			font1.drawString(button.getOriginX() - font1.getWidth(str)/2f, button.getOriginY() - font1.getHeight()/2f, str);
		}
		
		font1.unbindFont(game.assetManager.fontShader);
		
	}

}
