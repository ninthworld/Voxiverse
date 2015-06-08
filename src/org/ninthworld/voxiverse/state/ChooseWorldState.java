package org.ninthworld.voxiverse.state;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.gui.GUIButton;
import org.ninthworld.voxiverse.util.Color;
import org.ninthworld.voxiverse.util.NewFont;
import org.ninthworld.voxiverse.util.WorldVector3f;

public class ChooseWorldState implements State {
	
	private List<GUIButton> buttons;
	
	@Override
	public void init(Game game) {
		buttons = new ArrayList<GUIButton>();
		buttons.add( new GUIButton("Back", 0.2f, 0.9f, 0.2f, 0.1f) );
		
		File dir = new File("worlds\\");
		String[] list = dir.list();
		if(list != null && list.length > 0){
			for(String str : list){
				File f = new File(dir.getAbsolutePath()+"\\"+str);
				if(f.isDirectory()){
					buttons.add(new GUIButton(str, 0.5f, buttons.size()*0.2f, 0.2f, 0.1f));
				}
			}
		}
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
					for(GUIButton button : buttons){
						if(button.isMouseInBounds()){
							if(button.getLabel().equals("Back")){
								game.changeState(new MainMenuState());
							}else{
								game.changeState(new LoadWorldState(button.getLabel()));
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
