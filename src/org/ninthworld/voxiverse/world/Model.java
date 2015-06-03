package org.ninthworld.voxiverse.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.ninthworld.voxiverse.util.*;

public class Model {
	private int[][][] data;

	public List<List<Mesh>> meshes;
	public FloatBuffer[] fbo_verts;
	public FloatBuffer[] fbo_norms;
	public FloatBuffer[] fbo_colors;
	public IntBuffer[] vbo_verts;
	public IntBuffer[] vbo_norms;
	public IntBuffer[] vbo_colors;
	public int passes;
	
	private boolean hasMeshes;
	
	public Model(int[][][] data){
		this.data = data;

		this.passes = 2;
		this.meshes = new ArrayList<List<Mesh>>();
		this.fbo_verts = new FloatBuffer[this.passes];
		this.fbo_norms = new FloatBuffer[this.passes];
		this.fbo_colors = new FloatBuffer[this.passes];
		this.vbo_verts = new IntBuffer[this.passes];
		this.vbo_norms = new IntBuffer[this.passes];
		this.vbo_colors = new IntBuffer[this.passes];
		for(int i=0; i<passes; i++){
			this.meshes.add(new ArrayList<Mesh>());
		}
		
		this.hasMeshes = false;
	}
	
	public boolean hasMeshes(){
		return hasMeshes;
	}
	
	public void createMeshes(){
		if(fbo_verts[0] != null)
			clearBuffers();
		for(List<Mesh> m : meshes){
			m.clear();
		}
		
		List<BlockData[][][]> dataPass = new ArrayList<BlockData[][][]>();
		
		for(int p=0; p<passes; p++){
			BlockData[][][] blockData = new BlockData[data.length][data[0].length][data[0][0].length];
			dataPass.add(blockData);
			for(int x=0; x<data.length; x++){
				for(int y=0; y<data[x].length; y++){
					for(int z=0; z<data[x][y].length; z++){
						BlockData block = blockData[x][y][z] = new BlockData(data[x][y][z]);
						if(block.id > 0 && ((p == 0 && !Material.hasAlpha(block.id)) || (p == 1 && Material.hasAlpha(block.id))) ){
							int b0 = getVoxelAt(new VoxelVector3i(x+1, y, z));
							int b1 = getVoxelAt(new VoxelVector3i(x-1, y, z));
							int b2 = getVoxelAt(new VoxelVector3i(x, y+1, z));
							int b3 = getVoxelAt(new VoxelVector3i(x, y-1, z));
							int b4 = getVoxelAt(new VoxelVector3i(x, y, z+1));
							int b5 = getVoxelAt(new VoxelVector3i(x, y, z-1));
							block.faces[0] = (block.id != b0 ? Material.isTransparent(b0) : false);
							block.faces[1] = (block.id != b1 ? Material.isTransparent(b1) : false);
							block.faces[2] = (block.id != b2 ? Material.isTransparent(b2) : false);
							block.faces[3] = (block.id != b3 ? Material.isTransparent(b3) : false);
							block.faces[4] = (block.id != b4 ? Material.isTransparent(b4) : false);
							block.faces[5] = (block.id != b5 ? Material.isTransparent(b5) : false);
						}
					}
				}	
			}
			
			greedyMesh(p, dataPass.get(p));
			createFloatBuffer(p);
		}
		
		hasMeshes = true;
	}
	
	public void greedyMesh(int p, BlockData[][][] blockData){
		boolean[][][][] mask = new boolean[blockData.length][blockData[0].length][blockData[0][0].length][6];
		
		for(int side=0; side<6; side++){
			for(int x=0; x<blockData.length; x++){
				for(int y=0; y<blockData[0].length; y++){
					for(int z=0; z<blockData[0][0].length; z++){
						if(data[x][y][z] > Material.AIR && !mask[x][y][z][side] && blockData[x][y][z].faces[side]){
							if(side == 0 || side == 1){
								int width = 0;
								int height = 0;
								loop:
								for(int i=y; i<blockData[0].length; i++){
									if(i == y){
										for(int j=z; j<blockData[0][0].length; j++){
											if(!mask[x][i][j][side] && blockData[x][i][j].id == blockData[x][y][z].id && blockData[x][i][j].faces[side]){
												width++;
											}else{
												break;
											}
										}
									}else{
										for(int j=0; j<width; j++){
											if(mask[x][i][z+j][side] || blockData[x][i][z+j].id != blockData[x][y][z].id || !blockData[x][i][z+j].faces[side]){
												break loop;
											}
										}
									}
									height++;
								}
								for(int i=0; i<height; i++){
									for(int j=0; j<width; j++){
										mask[x][y+i][z+j][side] = true;
									}
								}
								
								if(side == 0)
									meshes.get(p).add(new Mesh(new VoxelVector3i(x+1, y, z), new VoxelVector3i(x+1, y+height, z+width), new VoxelVector3i(1, 0, 0), Material.getColor(data[x][y][z])));
								else
									meshes.get(p).add(new Mesh(new VoxelVector3i(x, y, z+width), new VoxelVector3i(x, y+height, z), new VoxelVector3i(-1, 0, 0), Material.getColor(data[x][y][z])));
							}else if(side == 2 || side == 3){
								int width = 0;
								int height = 0;
								loop:
								for(int i=x; i<blockData.length; i++){
									if(i == x){
										for(int j=z; j<blockData[0][0].length; j++){
											if(!mask[i][y][j][side] && blockData[i][y][j].id == blockData[x][y][z].id && blockData[i][y][j].faces[side]){
												width++;
											}else{
												break;
											}
										}
									}else{
										for(int j=0; j<width; j++){
											if(mask[i][y][z+j][side] || blockData[i][y][z+j].id != blockData[x][y][z].id || !blockData[i][y][z+j].faces[side]){
												break loop;
											}
										}
									}
									height++;
								}
								for(int i=0; i<height; i++){
									for(int j=0; j<width; j++){
										mask[x+i][y][z+j][side] = true;
									}
								}
								
								if(side == 2)
									meshes.get(p).add(new Mesh(new VoxelVector3i(x, y+1, z+width), new VoxelVector3i(x+height, y+1, z), new VoxelVector3i(0, 1, 0), Material.getColor(data[x][y][z])));
								else
									meshes.get(p).add(new Mesh(new VoxelVector3i(x+height, y, z+width), new VoxelVector3i(x, y, z), new VoxelVector3i(0, -1, 0), Material.getColor(data[x][y][z])));
							}else if(side == 4 || side == 5){
								int width = 0;
								int height = 0;
								loop:
								for(int i=x; i<blockData.length; i++){
									if(i == x){
										for(int j=y; j<blockData[0].length; j++){
											if(!mask[i][j][z][side] && blockData[i][j][z].id == blockData[x][y][z].id && blockData[i][j][z].faces[side]){
												width++;
											}else{
												break;
											}
										}
									}else{
										for(int j=0; j<width; j++){
											if(mask[i][y+j][z][side] || blockData[i][y+j][z].id != blockData[x][y][z].id || !blockData[i][y+j][z].faces[side]){
												break loop;
											}
										}
									}
									height++;
								}
								for(int i=0; i<height; i++){
									for(int j=0; j<width; j++){
										mask[x+i][y+j][z][side] = true;
									}
								}
								
								if(side == 4)
									meshes.get(p).add(new Mesh(new VoxelVector3i(x+height, y, z+1), new VoxelVector3i(x, y+width, z+1), new VoxelVector3i(0, 0, 1), Material.getColor(data[x][y][z])));
								else
									meshes.get(p).add(new Mesh(new VoxelVector3i(x, y, z), new VoxelVector3i(x+height, y+width, z), new VoxelVector3i(0, 0, -1), Material.getColor(data[x][y][z])));
							}
						}
					}
				}
			}
		}
	}
	
	public void createFloatBuffer(int p){
		fbo_verts[p] = BufferUtils.createFloatBuffer(meshes.get(p).size()*4*3);
		fbo_norms[p] = BufferUtils.createFloatBuffer(meshes.get(p).size()*4*3);
		fbo_colors[p] = BufferUtils.createFloatBuffer(meshes.get(p).size()*4*4);
		vbo_verts[p] = BufferUtils.createIntBuffer(1); 
		vbo_norms[p] = BufferUtils.createIntBuffer(1); 
		vbo_colors[p] = BufferUtils.createIntBuffer(1); 
				
		for(Mesh mesh : meshes.get(p)){
			fbo_verts[p].put(mesh.pos1.x).put(mesh.pos1.y).put(mesh.pos1.z);
			fbo_verts[p].put(mesh.norm.z==0?mesh.pos1.x:mesh.pos2.x).put(mesh.pos1.y).put(mesh.norm.x!=0||mesh.norm.y!=0?mesh.pos2.z:mesh.pos1.z);
			fbo_verts[p].put(mesh.pos2.x).put(mesh.pos2.y).put(mesh.pos2.z);
			fbo_verts[p].put(mesh.norm.z==0?mesh.pos2.x:mesh.pos1.x).put(mesh.pos2.y).put(mesh.norm.x!=0||mesh.norm.y!=0?mesh.pos1.z:mesh.pos2.z);
		}
		fbo_verts[p].flip();
		for(Mesh mesh : meshes.get(p)){
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
		}
		fbo_norms[p].flip();
		for(Mesh mesh : meshes.get(p)){
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
		}
		fbo_colors[p].flip();
		
		GL15.glGenBuffers(vbo_verts[p]);
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_verts[p].get(0));
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (Float.SIZE/Byte.SIZE)*(fbo_verts[p].capacity()), GL15.GL_STATIC_DRAW);
	    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fbo_verts[p]);
	    
	    GL15.glGenBuffers(vbo_norms[p]);
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_norms[p].get(0));
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (Float.SIZE/Byte.SIZE)*(fbo_norms[p].capacity()), GL15.GL_STATIC_DRAW);
	    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fbo_norms[p]);

	    GL15.glGenBuffers(vbo_colors[p]);
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_colors[p].get(0));
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (Float.SIZE/Byte.SIZE)*(fbo_colors[p].capacity()), GL15.GL_STATIC_DRAW);
	    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fbo_colors[p]);
	}
	
	public void drawFloatBuffer(int p){
		if(vbo_verts[p] != null && vbo_norms[p] != null && vbo_colors[p] != null){
			GL11.glPushMatrix();
			
			GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
	        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
			GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
			
	        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_verts[p].get(0));
	        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);
	
	        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_norms[p].get(0));
	        GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
	        
	        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_colors[p].get(0));
	        GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
	        
	        GL11.glDrawArrays(GL11.GL_QUADS, 0, fbo_verts[p].capacity()/3);
	        
	        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
	        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
	        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	        
	        GL11.glPopMatrix();
		}
	}
	
	public void render(int p){
		drawFloatBuffer(p);
	}
	
	public void clearBuffers(){
		for(int p=0; p<passes; p++){
			if(fbo_verts[p] != null){
				fbo_verts[p].clear();
				fbo_norms[p].clear();
				fbo_colors[p].clear();
				vbo_verts[p].clear();
				vbo_norms[p].clear();
				vbo_colors[p].clear();
			}
		}
		hasMeshes = false;
	}
	
	public int getVoxelAt(VoxelVector3i voxelPos){
		return getDataAt(voxelPos);
	}
	
	public int[][][] getRawData(){
		return data;
	}
	
	public void setDataAt(VoxelVector3i voxelPos, int id){
		if(inBounds(voxelPos)){
			data[voxelPos.x][voxelPos.y][voxelPos.z] = id;
		}
	}
	
	public int getDataAt(VoxelVector3i voxelPos){
		if(inBounds(voxelPos)){
			return data[voxelPos.x][voxelPos.y][voxelPos.z];
		}else{
			return Material.AIR;
		}
	}
	
	public boolean inBounds(VoxelVector3i voxelPos){
		return (voxelPos.x >= 0 && voxelPos.x < data.length && voxelPos.y >= 0 && voxelPos.y < data[0].length && voxelPos.z >= 0 && voxelPos.z < data[0][0].length);
	}
	
	public static Model load(String path){
		Model model = null;
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(new File(path)));
			int x_size, y_size, z_size;
			
			x_size = in.read();
			y_size = in.read();
			z_size = in.read();
			
			int[] working = new int[x_size*y_size*z_size];
			int read = 0;
			while(read < working.length){
				working[read] = in.read();
				read++;
			}
			
			int[][][] voxels = new int[x_size][y_size][z_size];
			for(int x=0; x<x_size; x++){
				for(int y=0; y<y_size; y++){
					for(int z=0; z<z_size; z++){
						voxels[x][y][z] = working[z + (y*z_size) + (x*z_size*y_size)];
					}
				}
			}
			model = new Model(voxels);
		
			in.close();
		} catch(FileNotFoundException e) {			
		} catch(IOException e) {			
		}
		
		return model;
	}
	
	public void save(String path){
		int x_size = this.data.length;
		int y_size = this.data[0].length;
		int z_size = this.data[0][0].length;
		
		byte[] output = new byte[3 + x_size*y_size*z_size];
		output[0] = (byte) (x_size & 0xFF);
		output[1] = (byte) (y_size & 0xFF);
		output[2] = (byte) (z_size & 0xFF);
		
		for(int x=0; x<x_size; x++){
			for(int y=0; y<y_size; y++){
				for(int z=0; z<z_size; z++){
					output[3 + z + (y*z_size) + (x*z_size*y_size)] = (byte) (this.data[x][y][z] & 0xFF);
				}
			}
		}
		
		OutputStream out = null;
		try {
			File f = new File(path);
			f.getParentFile().mkdirs();
			out = new BufferedOutputStream(new FileOutputStream(path));
			out.write(output);
			out.close();
		} catch(FileNotFoundException e) {			
		} catch(IOException e) {			
		}
	}
	
	private class Mesh {
		public VoxelVector3i pos1;
		public VoxelVector3i pos2;
		public VoxelVector3i norm;
		public Color color;
		
		public Mesh(VoxelVector3i pos1, VoxelVector3i pos2, VoxelVector3i norm, Color c){
			this.pos1 = pos1;
			this.pos2 = pos2;
			this.norm = norm;
			this.color = c;
		}
	}
	
	private class BlockData {
		public int id;
		public boolean[] faces;
		
		public BlockData(int id){
			this.id = id;
			this.faces = new boolean[6]; 
			
			/*
			 *  0 = +X  1 = -X
			 *  2 = +Y  3 = -Y
			 *  4 = +Z  5 = -Z
			 *  
			 */
		}
	}
}
