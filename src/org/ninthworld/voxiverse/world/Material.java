package org.ninthworld.voxiverse.world;

import org.ninthworld.voxiverse.util.Color;

public class Material {
	public static final int MAX_ID = 40;
	
	public static final int NULL = -1;
	public static final int AIR = 0;
	public static final int BLOCK_STONE = 1;
	public static final int BLOCK_GRASS = 2;
	public static final int BLOCK_WATER = 3;
	public static final int BLOCK_SAND = 4;	
	
	public static final int FLOWER_STEM_1 = 10;	
	public static final int FLOWER_STEM_2 = 11;	
	public static final int FLOWER_PETAL_YELLOW_1 = 12;	
	public static final int FLOWER_PETAL_YELLOW_2 = 13;	
	public static final int FLOWER_PETAL_RED_1 = 14;	
	public static final int FLOWER_PETAL_RED_2 = 15;	
	public static final int FLOWER_PETAL_WHITE_1 = 16;	
	public static final int FLOWER_PETAL_PINK_1 = 17;
	public static final int FLOWER_PETAL_PINK_2 = 18;	
	public static final int FLOWER_HEAD_CENTER_1 = 19;	
	public static final int FLOWER_HEAD_CENTER_2 = 20;	
	public static final int FLOWER_HEAD_CENTER_3 = 21;
	
	public static final int CHARACTER_1_SHOE_1 = 22;
	public static final int CHARACTER_1_SHOE_2 = 23;
	public static final int CHARACTER_1_SHOE_3 = 24;
	public static final int CHARACTER_1_SHIRT_1 = 25;
	public static final int CHARACTER_1_SHIRT_2 = 26;
	public static final int CHARACTER_1_SHIRT_3 = 27;
	public static final int CHARACTER_1_SHIRT_4 = 28;
	public static final int CHARACTER_1_BELT_1 = 29;
	public static final int CHARACTER_1_BELT_2 = 30;
	public static final int CHARACTER_1_BELT_3 = 31;
	public static final int CHARACTER_1_BELT_4 = 32;
	public static final int CHARACTER_1_SKIN_1 = 33;
	public static final int CHARACTER_1_SKIN_2 = 34;
	public static final int CHARACTER_1_HAIR_1 = 35;
	public static final int CHARACTER_1_HAIR_2 = 36;
	public static final int CHARACTER_1_EYES_1 = 37;
	public static final int CHARACTER_1_EYES_2 = 38;
	public static final int CHARACTER_1_EYES_3 = 39;
	public static final int CHARACTER_1_EYES_4 = 40;
	
	public static boolean isTransparent(int mat){
		switch(mat){
		case AIR:
			return true;
		case BLOCK_WATER:
			return true;
		}
		return false;
	}
	
	public static boolean hasAlpha(int mat){
		switch(mat){
		case BLOCK_WATER:
			return true;
		}
		return false;
	}
	
	public static Color getColor(int mat){
		switch(mat){
		case AIR:
			return null;
		case BLOCK_STONE:
			return new Color(97f/255f, 102f/255f, 106f/255f);
		case BLOCK_GRASS:
			return new Color(49f/255f, 88f/255f, 55f/255f);
		case BLOCK_WATER:
			return new Color(64/255f, 164/255f, 223/255f, 0.4f);
		case BLOCK_SAND:
			//return new Color(160/255f, 150/255f, 40/255f);
			return new Color(255/255f, 204/255f, 149/255f);
		case FLOWER_STEM_1:
			return new Color(46/255f, 115/255f, 26/255f);
		case FLOWER_STEM_2:
			return new Color(32/255f, 82/255f, 22/255f);
		case FLOWER_PETAL_YELLOW_1:
			return new Color(194/255f, 182/255f, 44/255f);
		case FLOWER_PETAL_YELLOW_2:
			return new Color(186/255f, 144/255f, 32/255f);
		case FLOWER_PETAL_RED_1:
			return new Color(166/255f, 66/255f, 68/255f);
		case FLOWER_PETAL_RED_2:
			return new Color(143/255f, 37/255f, 37/255f);
		case FLOWER_PETAL_WHITE_1:
			return new Color(190/255f, 187/255f, 182/255f);
		case FLOWER_PETAL_PINK_1:
			return new Color(193/255f, 139/255f, 171/255f);
		case FLOWER_PETAL_PINK_2:
			return new Color(190/255f, 106/255f, 165/255f);
		case FLOWER_HEAD_CENTER_1:
			return new Color(75/255f, 59/255f, 36/255f);
		case FLOWER_HEAD_CENTER_2:
			return new Color(75/255f, 59/255f, 36/255f);
		case FLOWER_HEAD_CENTER_3:
			return new Color(198/255f, 192/255f, 44/255f);
			
		case CHARACTER_1_SHOE_1:
			return new Color(33/255f, 33/255f, 33/255f);
		case CHARACTER_1_SHOE_2:
			return new Color(50/255f, 50/255f, 50/255f);
		case CHARACTER_1_SHOE_3:
			return new Color(78/255f, 78/255f, 78/255f);
		case CHARACTER_1_SHIRT_1:
			return new Color(25/255f, 72/255f, 127/255f);
		case CHARACTER_1_SHIRT_2:
			return new Color(35/255f, 86/255f, 217/255f);
		case CHARACTER_1_SHIRT_3:
			return new Color(61/255f, 56/255f, 45/255f);
		case CHARACTER_1_SHIRT_4:
			return new Color(226/255f, 225/255f, 219/255f);
		case CHARACTER_1_BELT_1:
			return new Color(60/255f, 51/255f, 40/255f);
		case CHARACTER_1_BELT_2:
			return new Color(66/255f, 60/255f, 50/255f);
		case CHARACTER_1_BELT_3:
			return new Color(96/255f, 104/255f, 113/255f);
		case CHARACTER_1_BELT_4:
			return new Color(146/255f, 153/255f, 162/255f);
		case CHARACTER_1_SKIN_1:
			return new Color(249/255f, 219/255f, 166/255f);
		case CHARACTER_1_SKIN_2:
			return new Color(255/255f, 238/255f, 189/255f);
		case CHARACTER_1_HAIR_1:
			return new Color(64/255f, 44/255f, 17/255f);
		case CHARACTER_1_HAIR_2:
			return new Color(76/255f, 50/255f, 14/255f);
		case CHARACTER_1_EYES_1:
			return new Color(0/255f, 0/255f, 0/255f);
		case CHARACTER_1_EYES_2:
			return new Color(24/255f, 45/255f, 70/255f);
		case CHARACTER_1_EYES_3:
			return new Color(9/255f, 69/255f, 105/255f);
		case CHARACTER_1_EYES_4:
			return new Color(255/255f, 255/255f, 255/255f);
		}
		return new Color(0,0,0);
	}
	
	public static String getName(int mat){
		switch(mat){
		case AIR:
			return "Air";
		case BLOCK_STONE:
			return "Stone";
		case BLOCK_GRASS:
			return "Grass";
		case BLOCK_WATER:
			return "Water";
		case BLOCK_SAND:
			return "Sand";
			
		case FLOWER_STEM_1:
			return "Flower Stem 1";
		case FLOWER_STEM_2:
			return "Flower Stem 2";
		case FLOWER_PETAL_YELLOW_1:
			return "Flower Petal Yellow 1";
		case FLOWER_PETAL_YELLOW_2:
			return "Flower Petal Yellow 2";
		case FLOWER_PETAL_RED_1:
			return "Flower Petal Red 1";
		case FLOWER_PETAL_RED_2:
			return "Flower Petal Red 2";
		case FLOWER_PETAL_WHITE_1:
			return "Flower Petal White 1";
		case FLOWER_PETAL_PINK_1:
			return "Flower Petal Pink 1";
		case FLOWER_PETAL_PINK_2:
			return "Flower Petal Pink 2";
		case FLOWER_HEAD_CENTER_1:
			return "Flower Head Center 1";
		case FLOWER_HEAD_CENTER_2:
			return "Flower Head Center 2";
		case FLOWER_HEAD_CENTER_3:
			return "Flower Head Center 3";
			
		case CHARACTER_1_SHOE_1:
			return "Character 1 Shoe 1";
		case CHARACTER_1_SHOE_2:
			return "Character 1 Shoe 2";
		case CHARACTER_1_SHOE_3:
			return "Character 1 Shoe 3";
		case CHARACTER_1_SHIRT_1:
			return "Character 1 Shirt 1";
		case CHARACTER_1_SHIRT_2:
			return "Character 1 Shirt 2";
		case CHARACTER_1_SHIRT_3:
			return "Character 1 Shirt 3";
		case CHARACTER_1_SHIRT_4:
			return "Character 1 Shirt 4";
		case CHARACTER_1_BELT_1:
			return "Character 1 Belt 1";
		case CHARACTER_1_BELT_2:
			return "Character 1 Belt 2";
		case CHARACTER_1_BELT_3:
			return "Character 1 Belt 3";
		case CHARACTER_1_BELT_4:
			return "Character 1 Belt 4";
		case CHARACTER_1_SKIN_1:
			return "Character 1 Skin 1";
		case CHARACTER_1_SKIN_2:
			return "Character 1 Skin 2";
		case CHARACTER_1_HAIR_1:
			return "Character 1 Hair 1";
		case CHARACTER_1_HAIR_2:
			return "Character 1 Hair 2";
		case CHARACTER_1_EYES_1:
			return "Character 1 Eyes 1";
		case CHARACTER_1_EYES_2:
			return "Character 1 Eyes 2";
		case CHARACTER_1_EYES_3:
			return "Character 1 Eyes 3";
		case CHARACTER_1_EYES_4:
			return "Character 1 Eyes 4";
		}
		return "NULL";
	}
}
