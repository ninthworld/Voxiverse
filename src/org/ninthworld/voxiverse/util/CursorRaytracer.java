package org.ninthworld.voxiverse.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.ninthworld.voxiverse.world.Chunk;
import org.ninthworld.voxiverse.world.Material;
import org.ninthworld.voxiverse.world.NewCamera;
import org.ninthworld.voxiverse.world.World;

public class CursorRaytracer {
	
	public WorldVector3f selectPos;
	public ChunkVector3i chunkSelectPos;
	public VoxelVector3i relVoxelSelectPos;
	public boolean isSelected;
	
	public ChunkVector3i chunkAdjPos;
	public VoxelVector3i relVoxelAdjPos;
	public boolean isAdjSelected;
	
	public CursorRaytracer(){
		isSelected = false;
		selectPos = new WorldVector3f(0, 0, 0);
		relVoxelSelectPos = new VoxelVector3i(0, 0, 0);
		chunkSelectPos = new ChunkVector3i(0, 0, 0);
		
		relVoxelAdjPos = new VoxelVector3i(0, 0, 0);
		chunkAdjPos = new ChunkVector3i(0, 0, 0);
		isAdjSelected = false;
	}
	
    IntBuffer viewBuffer = BufferUtils.createIntBuffer(16);
    FloatBuffer modelBuffer = BufferUtils.createFloatBuffer(16);
    FloatBuffer projBuffer = BufferUtils.createFloatBuffer(16);
    FloatBuffer startBuffer = BufferUtils.createFloatBuffer(4);
    FloatBuffer posBuffer = BufferUtils.createFloatBuffer(4);
    FloatBuffer zBuffer = BufferUtils.createFloatBuffer(20);
    
	public void raytrace(NewCamera camera, World world){
		viewBuffer.clear();
		projBuffer.clear();
		modelBuffer.clear();
	    zBuffer.clear();
	    posBuffer.clear();
	
	    float mx = Mouse.getX();
	    float my = Mouse.getY();
	    
	    GL11.glPushMatrix();
	        camera.apply();
	    GL11.glPopMatrix();
	
	    GL11.glGetInteger(GL11.GL_VIEWPORT, viewBuffer);
	    GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelBuffer);
	    GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projBuffer);
	
	    GL11.glReadPixels((int)mx, (int)my, 1, 1, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, zBuffer);
	    GLU.gluUnProject(mx, my, zBuffer.get(0), modelBuffer, projBuffer, viewBuffer, posBuffer);
	
		selectPos = new WorldVector3f(posBuffer.get(0), posBuffer.get(1), posBuffer.get(2));
		
		isSelected = false;
		WorldVector3f adjSelectPos = selectPos;
		loop1:
		for(int i=0; i<Chunk.VOXEL_SIZE; i++){
			WorldVector3f[] select = new WorldVector3f[6];
			select[0] = new WorldVector3f(selectPos.x + i, selectPos.y, selectPos.z);
			select[1] = new WorldVector3f(selectPos.x - i, selectPos.y, selectPos.z);
			select[2] = new WorldVector3f(selectPos.x, selectPos.y + i, selectPos.z);
			select[3] = new WorldVector3f(selectPos.x, selectPos.y - i, selectPos.z);
			select[4] = new WorldVector3f(selectPos.x, selectPos.y, selectPos.z + i);
			select[5] = new WorldVector3f(selectPos.x, selectPos.y, selectPos.z - i);

			for(WorldVector3f pos : select){
				ChunkVector3i chunkPos = WorldVector3f.toChunkVector(pos);
				VoxelVector3i relativeVoxelPos = VoxelVector3i.toRelativeVoxelVector(WorldVector3f.toVoxelVector(pos));
		
				Chunk chunk = world.getChunkAt(chunkPos);
				int material = Material.NULL;
				if(chunk != null){
					material = chunk.getVoxelAt(relativeVoxelPos);
				}
		
				if(material > Material.AIR){
					chunkSelectPos = chunkPos;
					relVoxelSelectPos = relativeVoxelPos;
					isSelected = true;
					adjSelectPos = pos;
					break loop1;
				}
			}
		}
		
		if(isSelected){
			isAdjSelected = false;
			loop2:
			for(int i=0; i<Chunk.VOXEL_SIZE; i++){
				WorldVector3f[] adj = new WorldVector3f[6];
				adj[0] = new WorldVector3f(adjSelectPos.x + i, adjSelectPos.y, adjSelectPos.z);
				adj[1] = new WorldVector3f(adjSelectPos.x - i, adjSelectPos.y, adjSelectPos.z);
				adj[2] = new WorldVector3f(adjSelectPos.x, adjSelectPos.y + i, adjSelectPos.z);
				adj[3] = new WorldVector3f(adjSelectPos.x, adjSelectPos.y - i, adjSelectPos.z);
				adj[4] = new WorldVector3f(adjSelectPos.x, adjSelectPos.y, adjSelectPos.z + i);
				adj[5] = new WorldVector3f(adjSelectPos.x, adjSelectPos.y, adjSelectPos.z - i);
				
				for(WorldVector3f pos : adj){
					ChunkVector3i adjChunkPos = WorldVector3f.toChunkVector(pos);
					VoxelVector3i adjRelativeVoxelPos = VoxelVector3i.toRelativeVoxelVector(WorldVector3f.toVoxelVector(pos));
					Chunk adjChunk = world.getChunkAt(adjChunkPos);
					int adjMaterial = Material.NULL;
					if(adjChunk != null){
						adjMaterial = adjChunk.getVoxelAt(adjRelativeVoxelPos);
						if( adjMaterial == Material.AIR){
							relVoxelAdjPos = adjRelativeVoxelPos;
							chunkAdjPos = adjChunkPos;
							isAdjSelected = true;
							break loop2;
						}
					}
				}
			}			
		}
	}
	
	public void renderBoundingBox(){
		/*GL11.glColor3f(1, 0, 1);
		RenderUtils.drawCubeWireframe(
			selectPos,
			new WorldVector3f(
				Chunk.VOXEL_SIZE/2f+1,
				Chunk.VOXEL_SIZE/2f+1,
				Chunk.VOXEL_SIZE/2f+1
			)
		);*/
		
		if(isSelected){
			GL11.glColor3f(1, 0, 0);
			RenderUtils.drawCubeWireframe(
				new WorldVector3f(
					chunkSelectPos.x*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE + relVoxelSelectPos.x*Chunk.VOXEL_SIZE + Chunk.VOXEL_SIZE/2f,
					chunkSelectPos.y*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE + relVoxelSelectPos.y*Chunk.VOXEL_SIZE + Chunk.VOXEL_SIZE/2f,
					chunkSelectPos.z*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE + relVoxelSelectPos.z*Chunk.VOXEL_SIZE + Chunk.VOXEL_SIZE/2f
				),
				new WorldVector3f(
					Chunk.VOXEL_SIZE/2f+1,
					Chunk.VOXEL_SIZE/2f+1,
					Chunk.VOXEL_SIZE/2f+1
				)
			);	
			
			if(isAdjSelected){
				GL11.glColor3f(0, 1, 0);
				RenderUtils.drawCubeWireframe(
						new WorldVector3f(
							chunkAdjPos.x*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE + relVoxelAdjPos.x*Chunk.VOXEL_SIZE + Chunk.VOXEL_SIZE/2f,
							chunkAdjPos.y*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE + relVoxelAdjPos.y*Chunk.VOXEL_SIZE + Chunk.VOXEL_SIZE/2f,
							chunkAdjPos.z*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE + relVoxelAdjPos.z*Chunk.VOXEL_SIZE + Chunk.VOXEL_SIZE/2f
						),
						new WorldVector3f(
							Chunk.VOXEL_SIZE/2f+1,
							Chunk.VOXEL_SIZE/2f+1,
							Chunk.VOXEL_SIZE/2f+1
						)
					);
			}
		}	
	}
}
