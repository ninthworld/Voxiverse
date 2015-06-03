package org.ninthworld.voxiverse.state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.jnbt.CompoundTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;
import org.ninthworld.voxiverse.entity.*;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.world.*;

public class LoadWorldState implements State {

	private String worldFileName;
	private World world;
	
	public LoadWorldState(String worldFileName){
		this.worldFileName = worldFileName;
	}
	
	@Override
	public void init(Game game) {
	}

	@Override
	public void update(Game game, int delta) {
		
		long seed = 0;
		
		try {
			NBTInputStream in = new NBTInputStream(new FileInputStream(new File("worlds\\"+worldFileName+"\\world.dat")));
			
			Tag tag = in.readTag();
			if(tag instanceof CompoundTag && tag.getName().equals("World")){
				CompoundTag root = (CompoundTag) tag;
				Map<String, Tag> map = root.getValue();
				for(Entry<String, Tag> entry : map.entrySet()){
					if(entry.getKey().equals("seed")){
						seed = ((LongTag) entry.getValue()).getValue();
					}
				}
			}
			in.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		world = new World();
		world.initialize(seed, worldFileName, new EntityPlayer(), game.settingsManager, game.assetManager.modelManager);
		
		world.loadChunks();
		game.changeState(new WorldState(world));
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
