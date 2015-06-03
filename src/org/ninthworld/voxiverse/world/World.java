package org.ninthworld.voxiverse.world;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.entity.*;
import org.ninthworld.voxiverse.game.Game;
import org.ninthworld.voxiverse.manager.ModelManager;
import org.ninthworld.voxiverse.manager.SettingsManager;
import org.ninthworld.voxiverse.util.*;

public class World {
	private int renderDistance;
	private long seed;
	public String worldFileName;
	//public Camera camera;
	public NewCamera newCamera;
	public EntityPlayer player;
	private HashMap<ChunkVector3i, Chunk> loadedChunks;
	
	private SimplexNoise[] noise;
	private SimplexNoise modelGen;
	
	private List<ChunkVector3i> chunksToUpdate;
	
	private ChunkVector3i lastPlayerPos;
	
	public CursorRaytracer cursorRaytracer;
	
	private ModelManager modelManager;
	
	public World(){
		this.seed = 0;
		this.worldFileName = "";
		//this.camera = new Camera();
		this.newCamera = new NewCamera();
		this.player = new EntityPlayer();
		this.loadedChunks = new HashMap<ChunkVector3i, Chunk>();
		this.noise = new SimplexNoise[10];
		this.cursorRaytracer = new CursorRaytracer();
	}
	
	public void initialize(long seed, String worldFileName, EntityPlayer player, SettingsManager settings, ModelManager modelManager){
		this.seed = seed;
		this.worldFileName = worldFileName;
		this.player = player;
		this.renderDistance = settings.getRenderDistance();
		//this.camera.create();
		
		for(int i=0; i<this.noise.length; i++){
			this.noise[i] = new SimplexNoise(seed+i);
		}
		this.modelGen = new SimplexNoise(seed+this.noise.length);
		
		this.modelManager = modelManager;
		
		chunksToUpdate = new ArrayList<ChunkVector3i>();
		
		//lastPlayerPos = WorldVector3f.toChunkVector(player.getPos());
		lastPlayerPos = WorldVector3f.toChunkVector(newCamera.getFocusPos());
	}
	
	long lastTime = 0;
	int mouseDelay = 160;
	public void input(Game game){
		if(Mouse.isButtonDown(0) && cursorRaytracer.isSelected && cursorRaytracer.isAdjSelected){
			Chunk chunk = getChunkAt(cursorRaytracer.chunkAdjPos);
			if(chunk != null && game.getTime() > lastTime+mouseDelay){
				lastTime = game.getTime();
				chunk.putBlock(cursorRaytracer.relVoxelAdjPos, Material.BLOCK_STONE);
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_X) && cursorRaytracer.isSelected){
			Chunk chunk = getChunkAt(cursorRaytracer.chunkSelectPos);
			if(chunk != null && game.getTime() > lastTime+mouseDelay){
				lastTime = game.getTime();
				chunk.putBlock(cursorRaytracer.relVoxelSelectPos, Material.AIR);
			}
		}
	}
	
	public void update(Game game){
		this.renderDistance = game.settingsManager.getRenderDistance();

		newCamera.update();

		if(!WorldVector3f.toChunkVector(newCamera.getFocusPos()).equals(lastPlayerPos)){
			loadChunks();
		}
		lastPlayerPos = WorldVector3f.toChunkVector(newCamera.getFocusPos());
		
		ChunkVector3i playerPos = WorldVector3f.toChunkVector(newCamera.getFocusPos());
		ChunkVector3i closest = null;
		for(ChunkVector3i chunkPos : chunksToUpdate){
			if(closest == null || (chunkPos.getDistanceTo(playerPos) < closest.getDistanceTo(playerPos) /*&& isChunkInFrustrum(chunkPos)*/)){
				closest = chunkPos;
			}
		}
		Chunk chunk = getChunkAt(closest);
		if(chunk != null){
			chunk.createMeshes();
		}
		chunksToUpdate.remove(closest);
		
	}
	
	public void loadChunks(){
		int loadDist = renderDistance;

		HashMap<ChunkVector3i, Chunk> newLoaded = new HashMap<ChunkVector3i, Chunk>(); 
		
		for(int x=-loadDist; x<=loadDist; x++){
			for(int y=-loadDist; y<=loadDist; y++){
				for(int z=-loadDist; z<=loadDist; z++){
					//ChunkVector3i chunkPos = WorldVector3f.toChunkVector(player.getPos());
					ChunkVector3i chunkPos = WorldVector3f.toChunkVector(newCamera.getFocusPos());
					chunkPos = ChunkVector3i.add(chunkPos, new ChunkVector3i(x, y, z), null);
					Chunk chunk = getChunkAt(chunkPos);
					if(chunk == null){
						chunk = loadChunk(chunkPos);
					}
					newLoaded.put(chunkPos, chunk);
				}
			}
		}
		
		for(Entry<ChunkVector3i, Chunk> entry : loadedChunks.entrySet()){
			Chunk chunk = entry.getValue();
			boolean keepLoaded = false;
			loop:
			for(Entry<ChunkVector3i, Chunk> newEntry : newLoaded.entrySet()){
				Chunk newChunk = newEntry.getValue();
				if(newChunk.getPos().equals(chunk.getPos())){
					keepLoaded = true;
					break loop;
				}
			}
			if(!keepLoaded){
				chunk.unload();
			}
		}
		
		loadedChunks.clear();
		loadedChunks = newLoaded;

	}
	
	public Chunk getChunkAt(ChunkVector3i chunkPos){
		return loadedChunks.get(chunkPos); // NULL if the key doesn't exist
	}
	
	public List<Chunk> getLoadedChunks(){
		List<Chunk> loaded = new ArrayList<Chunk>();
		for(Entry<ChunkVector3i, Chunk> entry : loadedChunks.entrySet()){
			loaded.add(entry.getValue());
		}
		return loaded;
	}
	
	public boolean chunkFileExists(ChunkVector3i chunkPos){
		String path = "worlds/"+worldFileName+"/chunks/voxels_"+chunkPos.toString()+".chunk";
		File f = new File(path);
		return f.exists();
	}
	
	public Chunk loadChunk(ChunkVector3i chunkPos){
		Chunk chunk;
		if(chunkFileExists(chunkPos)){
			String path = "worlds/"+worldFileName+"/chunks/voxels_"+chunkPos.toString()+".chunk";
			chunk = Chunk.load(this, chunkPos, path);
		}else{
			chunk = generateChunk(chunkPos);
		}
		
		loadedChunks.put(chunkPos, chunk);

		if(!chunksToUpdate.contains(chunk.getPos())){
			chunksToUpdate.add(chunk.getPos());
			for(Chunk neighbor : chunk.getNeighbors()){
				if(neighbor != null && !neighbor.debug_OnlyAir){
					if(!chunksToUpdate.contains(neighbor.getPos())){
						chunksToUpdate.add(neighbor.getPos());
					}
				}
			}
		}
		
		return chunk;
	}
	
	public void unloadChunk(ChunkVector3i chunkPos){
		loadedChunks.get(chunkPos).unload();
		loadedChunks.remove(chunkPos);
	}
	
	public Chunk generateChunk(ChunkVector3i chunkPos){

		final int BIOME_OCEAN = 0;
		final int BIOME_DESERT = 1;
		final int BIOME_GRASS = 2;

		float biome0Freq = 0.003f;
		float biome1Freq = 0.008f;
		
		float redFlowerFreq = 0.02f;
		float yellowFlowerFreq = 0.02f;
		float tallGrassFreq = 0.2f;
		float cactusFreq = 0.02f;
		float rockFreq = 0.0108f;
		
		Chunk chunk = new Chunk(chunkPos, this);
		
		boolean debug_OnlyAir = true;
		
		for(int x=0; x<Chunk.CHUNK_SIZE; x++){
			for(int z=0; z<Chunk.CHUNK_SIZE; z++){				
				int adjX = chunkPos.x*Chunk.CHUNK_SIZE + x;
				int adjZ = chunkPos.z*Chunk.CHUNK_SIZE + z;
				
				float[] biome = new float[2];
				biome[0] = noise[0].noise(adjX * biome0Freq, adjZ * biome0Freq);
				biome[1] = noise[1].noise(adjX * biome1Freq, adjZ * biome1Freq);
				
				int biomeId = (biome[0] < 0f ? BIOME_OCEAN : (biome[1] < 0f ? BIOME_DESERT : BIOME_GRASS));
				
				float height0Freq = 0.0f;
				float height1Freq = 0.003f;
				float height2Freq = 0.008f;
				
				float height0Amp = 32f; // Ocean depth
				float height1Amp = 16f;
				float height2Amp = 48f;
				
				int[] height = new int[3];
				height[BIOME_OCEAN] = (int)( biome[0] * height0Amp );
				height[BIOME_DESERT] = (int)( biome[0] * Math.abs(biome[1]) *  Math.abs(noise[2].noise(adjX * height1Freq, adjZ * height1Freq)) * height1Amp );
				height[BIOME_GRASS] = (int)( biome[0] * Math.abs(biome[1]) *  Math.abs(noise[3].noise(adjX * height2Freq, adjZ * height2Freq)) * height2Amp );
								
				for(int y=0; y<Chunk.CHUNK_SIZE; y++){
					int adjY = chunkPos.y*Chunk.CHUNK_SIZE + y;
					int mat = Material.AIR;

					if(adjY < height[biomeId]){
						if(biomeId == BIOME_OCEAN){
							mat = Material.BLOCK_SAND;
						}else if(biomeId == BIOME_DESERT){
							mat = Material.BLOCK_SAND;
						}else if(biomeId == BIOME_GRASS){
							mat = Material.BLOCK_GRASS;
						}
						debug_OnlyAir = false;
					}else if(adjY == height[biomeId] && adjY >= 0){
						double r = (modelGen.noise((float)(chunkPos.x*Chunk.CHUNK_SIZE+x)*20, (float)(chunkPos.z*Chunk.CHUNK_SIZE+z)*20)+1f)/2f;
						
						if(biomeId == BIOME_GRASS){
							float lastFreq = 0f;
							if(r > 0 && r < redFlowerFreq){
								chunk.addEntityModel(generateEntityModel("redFlower", new VoxelVector3i(x, y, z)));
							}
							lastFreq+=redFlowerFreq;
							if(r >= lastFreq && r < lastFreq+yellowFlowerFreq){
								chunk.addEntityModel(generateEntityModel("yellowFlower", new VoxelVector3i(x, y, z)));
							}
							lastFreq+=yellowFlowerFreq;
							if(r >= lastFreq && r < lastFreq+tallGrassFreq){
								chunk.addEntityModel(generateEntityModel("tallGrass", new VoxelVector3i(x, y, z)));
							}
							lastFreq+=tallGrassFreq;
							if(r >= lastFreq && r < lastFreq+rockFreq){
								chunk.addEntityModel(generateEntityModel("rock", new VoxelVector3i(x, y, z)));
							}
						}else if(biomeId == BIOME_DESERT){
							float lastFreq = 0f;
							if(r > 0 && r < cactusFreq){
								chunk.addEntityModel(generateEntityModel("cactus", new VoxelVector3i(x, y, z)));
							}
							lastFreq+=cactusFreq;
							if(r >= lastFreq && r < lastFreq+rockFreq){
								chunk.addEntityModel(generateEntityModel("rock", new VoxelVector3i(x, y, z)));
							}
						}
					}else if(adjY < 0){
						if(biomeId == BIOME_OCEAN){
							mat = Material.BLOCK_WATER;
						}
						debug_OnlyAir = false;
					}
					
					chunk.setDataAt(new VoxelVector3i(x, y, z), mat);
				}
			}
		}
		
		if(debug_OnlyAir){
			chunk.debug_OnlyAir = true;
		}
		
		return chunk;
	}
	
	private EntityModel generateEntityModel(String modelName, VoxelVector3i pos){
		float scale = modelManager.getScale(modelName);
		Model model = modelManager.getModel(modelName);
		float xOffset = Chunk.VOXEL_SIZE/2f - (model.getRawData().length*scale)/2f;
		float zOffset = Chunk.VOXEL_SIZE/2f - (model.getRawData()[0][0].length*scale)/2f;
		
		return new EntityModel(
				(VoxelVector3i.toWorldVector(pos)).add(xOffset, 0f, zOffset),
				WorldVector3f.Zero(),
				model,
				scale
			);
	}
	
	private boolean isChunkInFrustrum(ChunkVector3i chunkPos){
		
		WorldVector3f pos = newCamera.getCameraPos();
		WorldVector3f rot = newCamera.getCameraRot();
		
		float playerAngle = (float)(rot.y - Math.PI/2f);
		float fovRadius = (float)Math.PI/3f;
		
		WorldVector3f chunkWorldPos = ChunkVector3i.toWorldVector(chunkPos);
		chunkWorldPos.x += (Chunk.VOXEL_SIZE*Chunk.CHUNK_SIZE)/2f;
		chunkWorldPos.y += (Chunk.VOXEL_SIZE*Chunk.CHUNK_SIZE)/2f;
		chunkWorldPos.z += (Chunk.VOXEL_SIZE*Chunk.CHUNK_SIZE)/2f;
		
		WorldVector3f playerWorldPos = new WorldVector3f(
				(float)(pos.x - Math.sin(-playerAngle + Math.PI/2f)*(Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE)),
				pos.y, 
				(float)(pos.z - Math.cos(-playerAngle + Math.PI/2f)*(Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE))
			);
		
		float chunkAngle = (float) Math.atan2(chunkWorldPos.z - playerWorldPos.z, chunkWorldPos.x - playerWorldPos.x);
		
		float lowAngle = WorldVector3f.wrapRadian(playerAngle - fovRadius);
		float adjustedAngle = WorldVector3f.wrapRadian(chunkAngle - lowAngle);
				
		if( adjustedAngle >= 0f && adjustedAngle <= fovRadius*2f){
			return true;
		}else{		
			return false;
		}
	}
	
	public void render(){
		
		renderSkybox();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		for(int pass=0; pass<2; pass++){
			for(Entry<ChunkVector3i, Chunk> set : loadedChunks.entrySet()){
				Chunk chunk = set.getValue();
				GL11.glPushMatrix();
				
				GL11.glTranslatef(
						chunk.getPos().x*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE,
						chunk.getPos().y*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE,
						chunk.getPos().z*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE
					);
					chunk.render(pass);
					
				GL11.glPopMatrix();
					
			}
		}
		
		cursorRaytracer.renderBoundingBox();
		
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	public void renderSkybox(){
		WorldVector3f pos = newCamera.getFocusPos();
		
		float renderDist = (renderDistance+1)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE;
		Color lowSky = new Color(214/255f, 239/255f, 244/255f);
		Color highSky = new Color(97/255f, 193/255f, 215/255f);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(pos.x, pos.y, pos.z);
		//GL11.glColor3f(0, 0, 1f);
		GL11.glBegin(GL11.GL_QUADS);
			highSky.glColor();
			GL11.glVertex3f(renderDist, renderDist, renderDist);
			GL11.glVertex3f(-renderDist, renderDist, renderDist);
			lowSky.glColor();
			GL11.glVertex3f(-renderDist, -renderDist, renderDist);
			GL11.glVertex3f(renderDist, -renderDist, renderDist);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
			highSky.glColor();
			GL11.glVertex3f(-renderDist, renderDist, -renderDist);
			GL11.glVertex3f(renderDist, renderDist, -renderDist);
			lowSky.glColor();
			GL11.glVertex3f(renderDist, -renderDist, -renderDist);
			GL11.glVertex3f(-renderDist, -renderDist, -renderDist);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
			highSky.glColor();
			GL11.glVertex3f(renderDist, renderDist, -renderDist);
			GL11.glVertex3f(renderDist, renderDist, renderDist);
			lowSky.glColor();
			GL11.glVertex3f(renderDist, -renderDist, renderDist);
			GL11.glVertex3f(renderDist, -renderDist, -renderDist);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
			highSky.glColor();
			GL11.glVertex3f(-renderDist, renderDist, renderDist);
			GL11.glVertex3f(-renderDist, renderDist, -renderDist);
			lowSky.glColor();
			GL11.glVertex3f(-renderDist, -renderDist, -renderDist);
			GL11.glVertex3f(-renderDist, -renderDist, renderDist);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS);
			highSky.glColor();
			GL11.glVertex3f(-renderDist, renderDist, renderDist);
			GL11.glVertex3f(renderDist, renderDist, renderDist);
			GL11.glVertex3f(renderDist, renderDist, -renderDist);
			GL11.glVertex3f(-renderDist, renderDist, -renderDist);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
}
