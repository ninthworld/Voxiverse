package org.ninthworld.voxiverse.state;

import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.world.*;

public class WorldState implements State {

	private World world;
	
	public WorldState(World world){
		this.world = world;
	}
	
	@Override
	public void init(Game game) {
		
	}

	@Override
	public void update(Game game, int delta) {
		world.update(game);
	}

	@Override
	public void input(Game game, int delta) {
		//world.camera.acceptInput(delta);
		world.newCamera.acceptInput(delta);
	}

	@Override
	public void cleanUp(Game game) {

	}

	@Override
	public void render3D(Game game) {
		//world.camera.apply();
		world.newCamera.apply();
		world.render();
	}

	@Override
	public void render2D(Game game) {
		
	}

}
