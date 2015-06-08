package org.ninthworld.voxiverse.state;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.gui.GUIButton;
import org.ninthworld.voxiverse.gui.GUIDisplay;
import org.ninthworld.voxiverse.world.*;

public class WorldState implements State {

	private World world;
	
	private boolean keyESCDown;
	private GUIDisplay guiDisplay;
	
	public WorldState(World world){
		this.world = world;
	}
	
	@Override
	public void init(Game game) {
		keyESCDown = false;
		guiDisplay = new GUIDisplay();
		guiDisplay.setVisible(false);
		
		guiDisplay.addGUIObject("minusChunkDist", new GUIButton("-", 0.05f, 0.1f, 0.025f, 0.025f));
		guiDisplay.addGUIObject("addChunkDist", new GUIButton("+", 0.15f, 0.1f, 0.025f, 0.025f));
	}

	@Override
	public void update(Game game, int delta) {
		world.update(game);
	}

	private boolean leftDown = false;
	
	@Override
	public void input(Game game, int delta) {
		world.newCamera.acceptInput(delta);
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			if(!keyESCDown){
				keyESCDown = true;
				guiDisplay.setVisible(!guiDisplay.isVisible());
			}
		}else{
			keyESCDown = false;
		}
		
		if(guiDisplay.isVisible()){
			guiDisplay.update();

			if(Mouse.isButtonDown(0)){ // Left is Down
				if(!leftDown){
					leftDown = true;
					
					if(guiDisplay.getGUIButton("minusChunkDist").isMouseInBounds()){
						game.settingsManager.setRenderDistance(game.settingsManager.getRenderDistance()-1);
					}
					if(guiDisplay.getGUIButton("addChunkDist").isMouseInBounds()){
						game.settingsManager.setRenderDistance(game.settingsManager.getRenderDistance()+1);
					}
				}
			}else{
				leftDown = false;
			}
		}else{
			world.input(game);
		}
	}

	@Override
	public void cleanUp(Game game) {

	}

	@Override
	public void render3D(Game game) {
		//world.camera.apply();
		world.newCamera.apply();
		world.render();
		world.newCamera.renderCamera();
		
		world.cursorRaytracer.raytrace(world.newCamera, world);
	}

	@Override
	public void render2D(Game game) {
		if(guiDisplay.isVisible()){
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(0, 0, 0, .5f);
			GL11.glRectf(0, 0, Display.getWidth()/3f, Display.getHeight());
			GL11.glDisable(GL11.GL_BLEND);
			
			guiDisplay.render(game.assetManager.fontManager, game.assetManager.fontShader);
		}
	}

}
