package org.ninthworld.voxiverse.util;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.voxiverse.world.Chunk;

public class VoxelVector3i {
	
	public int x, y, z;
	
	public VoxelVector3i(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static ChunkVector3i toChunkVector(VoxelVector3i a){
		return new ChunkVector3i(
			(int)Math.floor(a.x/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.y/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.z/(float)Chunk.CHUNK_SIZE)
		);
	}
	
	public static WorldVector3f toWorldVector(VoxelVector3i a){
		return new WorldVector3f(
			(float) a.x * Chunk.VOXEL_SIZE,
			(float) a.y * Chunk.VOXEL_SIZE,
			(float) a.z * Chunk.VOXEL_SIZE
		);
	}
	
	public static VoxelVector3i toRelativeVoxelVector(VoxelVector3i a){
		return new VoxelVector3i(
			(int)floorMod((int)a.x, Chunk.CHUNK_SIZE), 
			(int)floorMod((int)a.y, Chunk.CHUNK_SIZE), 
			(int)floorMod((int)a.z, Chunk.CHUNK_SIZE)
		);
	}
	
	public static int floorMod(int a, int b){
		return (((a % b) + b) % b);
	}
	
	public int hashCode(){
		return (int)(x+y+z);
	}
	
	public boolean equals(Object o){
		if(o instanceof VoxelVector3i){
			VoxelVector3i v = (VoxelVector3i) o;
			return (v.x == x && v.y == y && v.z == z);
		}else{
			return false;
		}
	}
}
