package org.ninthworld.voxiverse.state;

import org.ninthworld.voxiverse.game.*;

public interface State {
	
	void init(Game game);

	void update(Game game, int delta);
	
	void input(Game game, int delta);
	
	void cleanUp(Game game);

	void render3D(Game game);

	void render2D(Game game);
}
