package org.ninthworld.voxiverse.util;

import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.voxiverse.world.Chunk;

public class ChunkVector3i extends Vector3f {
	
	public int x, y, z;
	
	public ChunkVector3i(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static ChunkVector3i add(ChunkVector3i a, ChunkVector3i b, ChunkVector3i dest){
		ChunkVector3i temp = new ChunkVector3i(a.x + b.x, a.y + b.y, a.z + b.z);
		if(dest != null){
			dest.x = temp.x;
			dest.y = temp.y;
			dest.z = temp.z;
			return dest;
		}else{
			return temp;
		}
	}
	
	public static WorldVector3f toWorldVector(ChunkVector3i a){
		return new WorldVector3f(
			(float) a.x * Chunk.VOXEL_SIZE * Chunk.CHUNK_SIZE,
			(float) a.y * Chunk.VOXEL_SIZE * Chunk.CHUNK_SIZE,
			(float) a.z * Chunk.VOXEL_SIZE * Chunk.CHUNK_SIZE
		);
	}
	
	public static VoxelVector3i toVoxelVector(ChunkVector3i a){
		return new VoxelVector3i(
			(int) a.x * Chunk.CHUNK_SIZE,
			(int) a.y * Chunk.CHUNK_SIZE,
			(int) a.z * Chunk.CHUNK_SIZE
		);
	}
	
	public double getDistanceTo(ChunkVector3i a){
		return Math.sqrt(Math.pow(x - a.x, 2) + Math.pow(y - a.y, 2) + Math.pow(z - a.z, 2));
	}
	
	public int hashCode(){
		return (int)(x+y+z);
	}
	
	public boolean equals(Object o){
		if(o instanceof ChunkVector3i){
			ChunkVector3i v = (ChunkVector3i) o;
			return (v.x == x && v.y == y && v.z == z);
		}else{
			return false;
		}
	}
	
	public String toString(){
		return (int)x+"_"+(int)y+"_"+(int)z;
	}
}
