package org.ninthworld.voxiverse.util;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.voxiverse.world.Chunk;

public class WorldVector3f {

	public float x, y, z;
	
	public WorldVector3f(){
		this.x = 0f;
		this.y = 0f;
		this.z = 0f;
	}
	
	public WorldVector3f(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static VoxelVector3i toVoxelVector(WorldVector3f a){
		return new VoxelVector3i(
			(int)Math.floor(a.x/Chunk.VOXEL_SIZE),
			(int)Math.floor(a.y/Chunk.VOXEL_SIZE),
			(int)Math.floor(a.z/Chunk.VOXEL_SIZE)
		);
	}
	
	public static ChunkVector3i toChunkVector(WorldVector3f a){
		return new ChunkVector3i(
			(int)Math.floor(a.x/Chunk.VOXEL_SIZE/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.y/Chunk.VOXEL_SIZE/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.z/Chunk.VOXEL_SIZE/(float)Chunk.CHUNK_SIZE)
		);
	}
	
	public int hashCode(){
		return (int)(x+y+z);
	}
	
	public boolean equals(Object o){
		if(o instanceof WorldVector3f){
			WorldVector3f v = (WorldVector3f) o;
			return (v.x == x && v.y == y && v.z == z);
		}else{
			return false;
		}
	}
	
	public static float wrapRadian(float rad){
		return (float)((((rad)%(Math.PI*2f))+(Math.PI*2f))%(Math.PI*2f));
	}
	
	public float distanceTo(WorldVector3f b){
		return (float) Math.sqrt(Math.pow(x - b.x, 2) + Math.pow(y - b.y, 2) + Math.pow(z - b.z, 2));
	}
	
	public static WorldVector3f Zero(){
		return new WorldVector3f(0, 0, 0);
	}
	
	public WorldVector3f add(float x, float y, float z){
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}
}
