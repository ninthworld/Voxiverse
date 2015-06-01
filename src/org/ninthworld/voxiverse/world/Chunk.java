package org.ninthworld.voxiverse.world;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.util.*;

public class Chunk extends Model {
	public static final int CHUNK_SIZE = 16;
	public static final float VOXEL_SIZE = 16f;
	
	private ChunkVector3i pos;
	private World parent;

	public boolean debug_OnlyAir = false;
	
	public Chunk(ChunkVector3i pos, World parent){
		super(new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE]);
		this.pos = pos;
		this.parent = parent;
	}
	
	public Chunk(ChunkVector3i pos, int[][][] data){
		super(data);
		this.pos = pos;
	}
	             
	public void createMeshes(){
		if(!debug_OnlyAir){
			super.createMeshes();
		}else{
			createFloatBuffer(0);
			createFloatBuffer(1);
		}
	}

	public void render(int p){
		GL11.glPushMatrix();
			GL11.glScalef(VOXEL_SIZE, VOXEL_SIZE, VOXEL_SIZE);
			drawFloatBuffer(p);
		GL11.glPopMatrix();
	}
	
	@Override
	public int getVoxelAt(VoxelVector3i voxelPos){
		Chunk chunk;
		if(inBounds(voxelPos) || (chunk = parent.getChunkAt(ChunkVector3i.add(pos, VoxelVector3i.toChunkVector(voxelPos), null))) == null){
			return getDataAt(voxelPos);
		}else{
			return chunk.getVoxelAt(VoxelVector3i.toRelativeVoxelVector(voxelPos));
		}
	}
	
	public ChunkVector3i getPos(){
		return pos;
	}
	
	public static Chunk load(ChunkVector3i chunkPos, String url){
		return new Chunk(chunkPos, load(url).getRawData());
	}

	public void unload(){
		clearBuffers();
	}
	
	public List<Chunk> getNeighbors(){
		List<Chunk> neighbors = new ArrayList<Chunk>();

		neighbors.add(parent.getChunkAt(new ChunkVector3i(pos.x+1, pos.y, pos.z)));
		neighbors.add(parent.getChunkAt(new ChunkVector3i(pos.x-1, pos.y, pos.z)));
		neighbors.add(parent.getChunkAt(new ChunkVector3i(pos.x, pos.y+1, pos.z)));
		neighbors.add(parent.getChunkAt(new ChunkVector3i(pos.x, pos.y-1, pos.z)));
		neighbors.add(parent.getChunkAt(new ChunkVector3i(pos.x, pos.y, pos.z+1)));
		neighbors.add(parent.getChunkAt(new ChunkVector3i(pos.x, pos.y, pos.z-1)));
		
		return neighbors;
	}
}
