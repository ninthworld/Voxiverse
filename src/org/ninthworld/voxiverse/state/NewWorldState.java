package org.ninthworld.voxiverse.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.jnbt.*;
import org.ninthworld.voxiverse.game.*;

public class NewWorldState implements State {

	@Override
	public void init(Game game) {
		
	}

	@Override
	public void update(Game game, int delta) {
		
		File worlds = new File("worlds\\");
		worlds.mkdirs();
		
		int lastIndex = 0;
		while( (new File("worlds\\world"+lastIndex+"\\")).exists() ){
			lastIndex++;
		}
		
		File world = new File("worlds\\world"+lastIndex+"\\");
		world.mkdirs();
		
		try {
			NBTOutputStream out = new NBTOutputStream(new FileOutputStream(new File(world.getPath()+"\\world.dat")));
			Random rand = new Random();
			Map<String, Tag> tags = new HashMap<String, Tag>();
			tags.put("seed", new LongTag("seed", rand.nextLong()));
			CompoundTag root = new CompoundTag("World", tags);
			out.writeTag(root);
			out.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		game.changeState(new LoadWorldState("world"+lastIndex));
	}

	@Override
	public void input(Game game, int delta) {

	}

	@Override
	public void cleanUp(Game game) {

	}

	@Override
	public void render3D(Game game) {

	}

	@Override
	public void render2D(Game game) {

	}

}
