package org.ninthworld.voxiverse.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.jnbt.*;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.entity.EntityModel;
import org.ninthworld.voxiverse.manager.ModelManager;
import org.ninthworld.voxiverse.util.*;

public class Chunk extends Model {
	public static final int CHUNK_SIZE = 16;
	public static final float VOXEL_SIZE = 16f;
	
	private List<EntityModel> entityModels;
	
	private ChunkVector3i pos;
	private World parent;

	public boolean debug_OnlyAir = false;
	
	public Chunk(ChunkVector3i pos, World parent){
		super(new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE]);
		this.pos = pos;
		this.parent = parent;
		this.entityModels = new ArrayList<EntityModel>();
	}
	
	public Chunk(ChunkVector3i pos, int[][][] data, World parent){
		super(data);
		this.pos = pos;
		this.parent = parent;
		this.entityModels = new ArrayList<EntityModel>();
	}
	
	public void createMeshes(){
		if(!debug_OnlyAir){
			super.createMeshes();
		}else{
			createFloatBuffer(0);
			createFloatBuffer(1);
		}
	}

	public void update(){
		for(EntityModel em : entityModels){
			em.update();
		}
	}
	
	public void render(int p){
		GL11.glPushMatrix();
			GL11.glScalef(VOXEL_SIZE, VOXEL_SIZE, VOXEL_SIZE);
			drawFloatBuffer(p);
		GL11.glPopMatrix();	

		for(EntityModel em : entityModels){
			GL11.glPushMatrix();
				GL11.glTranslatef(em.getPos().x, em.getPos().y, em.getPos().z);
				GL11.glScalef(em.getScale(), em.getScale(), em.getScale());
				em.getModel().drawFloatBuffer(p);
			GL11.glPopMatrix();	
			
			if(em.isHovered()){
				em.renderBoundingBox(new Color(0f, 1f, 0f));
			}
			if(em.isSelected()){
				em.renderBoundingBox(new Color(0f, 0f, 1f));
			}
		}
	}
	
	public void renderWireframe(){
		GL11.glColor3f(0, 0, 0);
		RenderUtils.drawCubeWireframe(
				new WorldVector3f(
						(pos.x+0.5f)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE, 
						(pos.y+0.5f)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE,
						(pos.z+0.5f)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE
					),
					new WorldVector3f(Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE/2f, Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE/2f, Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE/2f)
			);
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
	
	public static Chunk load(World world, ChunkVector3i chunkPos, String url){
		return new Chunk(chunkPos, load(url).getRawData(), world);
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
	
	public void addEntityModel(EntityModel entityModel){
		entityModels.add(entityModel);
	}
	
	public List<EntityModel> getEntityModels(){
		return entityModels;
	}
	
	public void putBlock(VoxelVector3i voxelPos, int material){
		this.setDataAt(voxelPos, material);
		saveChunk();
		createMeshes();
		//parent.loadChunk(pos);
		
		//this.createMeshes();
		//this.debug_OnlyAir = false;
	}
	
	public void saveChunk(){
		this.save(getChunkVoxelPath());
		
		// Save entities
		File path = new File(getChunkEntitiesPath());
		path.getParentFile().mkdirs();
		
		try {
			NBTOutputStream out = new NBTOutputStream(new FileOutputStream(path));

			Map<String, Tag> tags = new HashMap<String, Tag>();
			
			for(EntityModel entityModel : entityModels){
				Map<String, Tag> subtags = new HashMap<String, Tag>();
				subtags.put("name", new StringTag("name", entityModel.getModel().name));
				subtags.put("scale", new FloatTag("scale", entityModel.getScale()));
				subtags.put("pos", entityModel.getPos().encodeCompoundTag("pos"));
				subtags.put("rot", entityModel.getRotation().encodeCompoundTag("rot"));
				
				String cTagName = "entityModel"+entityModels.indexOf(entityModel);
				CompoundTag sub = new CompoundTag(cTagName, subtags);
				
				tags.put(cTagName, sub);
			}
			
			CompoundTag root = new CompoundTag("Entities", tags);
			out.writeTag(root);
			
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Chunk loadChunk(World world, ChunkVector3i chunkPos, ModelManager modelManager){
		Chunk chunk = Chunk.load(world, chunkPos, getChunkVoxelPath(world, chunkPos));
		
		try {
			NBTInputStream in = new NBTInputStream(new FileInputStream(new File(getChunkEntitiesPath(world, chunkPos))));
			
			Tag tag = in.readTag();
			if(tag instanceof CompoundTag && tag.getName().equals("Entities")){
				
				CompoundTag root = (CompoundTag) tag;
				
				Map<String, Tag> rootMap = root.getValue();
				for(Entry<String, Tag> entry : rootMap.entrySet()){
					
					if(entry.getValue() instanceof CompoundTag){
						CompoundTag sub = (CompoundTag) entry.getValue();

						String name = "";
						float scale = 0f;
						WorldVector3f pos = WorldVector3f.Zero();
						WorldVector3f rot = WorldVector3f.Zero();
						
						Map<String, Tag> subMap = sub.getValue();
						for(Entry<String, Tag> subEntry : subMap.entrySet()){
							Tag valueTag = subEntry.getValue();
							
							if(valueTag.getName().equals("name") && valueTag instanceof StringTag){
								name = ((StringTag) valueTag).getValue();
							}else if(valueTag.getName().equals("scale") && valueTag instanceof FloatTag){
								scale = ((FloatTag) valueTag).getValue();
							}else if(valueTag.getName().equals("pos") && valueTag instanceof CompoundTag){
								pos.decodeCompoundTag((CompoundTag) valueTag);
							}else if(valueTag.getName().equals("rot") && valueTag instanceof CompoundTag){
								rot.decodeCompoundTag((CompoundTag) valueTag);
							}							
						}
						
						chunk.addEntityModel(new EntityModel(pos, rot, modelManager.getModel(name), scale));
					}					
				}				
			}
			in.close();			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return chunk;
	}
	
	public static String getChunkVoxelPath(World w, ChunkVector3i chunkPos){
		return "worlds/"+w.worldFileName+"/chunks/voxels_"+chunkPos.toString()+".chunk";
	}
	
	public static String getChunkEntitiesPath(World w, ChunkVector3i chunkPos){
		return "worlds/"+w.worldFileName+"/chunks/entities_"+chunkPos.toString()+".chunk";
	}
	
	public String getChunkVoxelPath(){
		return getChunkVoxelPath(parent, pos);
	}
	public String getChunkEntitiesPath(){
		return getChunkEntitiesPath(parent, pos);
	}
}
