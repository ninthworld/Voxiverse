package org.ninthworld.voxiverse.state;

import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.game.*;

public class LoadAssetsState implements State {

	@Override
	public void init(Game game){
		
	}

	@Override
	public void update(Game game, int delta){
		game.assetManager.loadAssets();
		game.changeState(new MainMenuState());
	}

	@Override
	public void input(Game game, int delta){
		
	}

	@Override
	public void render3D(Game game){
		
	}
	
	@Override
	public void render2D(Game game){
		
	}

	@Override
	public void cleanUp(Game game){	
		
	}
}
