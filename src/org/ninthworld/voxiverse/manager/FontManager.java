package org.ninthworld.voxiverse.manager;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.ninthworld.voxiverse.util.NewFont;
import org.ninthworld.voxiverse.world.Model;

public class FontManager {
	private HashMap<String, NewFont> fonts;
	
	public FontManager(){
		fonts = new HashMap<String, NewFont>();
	}
	
	public void loadFont(String name, int size, String file){
		BufferedImage img = loadImage(file);
		int id = loadTexture(img);
		NewFont font = new NewFont(size, id);
		fonts.put(name, font);
	}
	
	public NewFont getFont(String name){
		return fonts.get(name);
	}

	public void cleanUp() {
		for(Entry<String, NewFont> set : fonts.entrySet()){
			set.getValue().cleanUp();
		}
	}
	 
	private static int loadTexture(BufferedImage image) {
    	int BYTES_PER_PIXEL = 4;
    	int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);

        for(int y = 0; y < image.getHeight(); y++){
        	for(int x = 0; x < image.getWidth(); x++){
        		int pixel = pixels[y * image.getWidth() + x];
        		buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
        		buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
        		buffer.put((byte) (pixel & 0xFF));               // Blue component
        		buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
        	}
        }
        buffer.flip();
    
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
    	return textureID;
	}

	public static BufferedImage loadImage(String loc) {
		try {
			return ImageIO.read(new File(loc));
		} catch (IOException e) {
		}
		return null;
	}
}
