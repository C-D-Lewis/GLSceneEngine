package gl_scene_engine;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

/**
 * Tile sheet parser to get tiles from a single image
 */
public class TileSheetParser {

	private BufferedImage sprites[][];
	private BufferedImage sheet;
	private HashMap<Point, Integer> textureMap = new HashMap<Point, Integer>();
	private int tileSize;

	/**
	 * Constructor. Assumes square glyphs
	 * @param sheetPath	Path to tile sheet
	 */
	public TileSheetParser(String path, int tileSize, boolean discardBlank) {
		this.tileSize = tileSize;
		
		try {
			//Open sheet
			sheet = ImageIO.read(new File(path));
			Logger.log(TileSheetParser.class, "Path is " + path, Logger.INFO, true);
			
			boolean b = sheet.getType() == BufferedImage.TYPE_4BYTE_ABGR;
			Logger.assertOrCrash(b, "Sprite sheet should be BufferedImage.TYPE_4BYTE_ABGR");
			
			//Get parameters
			int gridWidth = sheet.getWidth() / tileSize;
			int gridHeight = sheet.getHeight() / tileSize;
			
			Logger.log(TileSheetParser.class, "Tile sheet of " + sheet.getWidth() + "x" + sheet.getHeight() + " is " + gridWidth + "x" + gridHeight + " tiles.", Logger.INFO, true);
			
			//Setup array
			sprites = new BufferedImage[gridWidth][gridHeight];

			//Parse sprites
			int count = 0;
			for(int y = 0; y < gridHeight; y++) {
				for(int x = 0; x < gridWidth; x++) {
					sprites[x][y] = sheet.getSubimage(x * tileSize, y * tileSize, tileSize, tileSize);
					if(discardBlank) {
						if(checkSpriteBlank(sprites[x][y])) {
							sprites[x][y] = null;	//Throw it away
						}
					} else {
						count++;
						
						// Make and save the texture
						textureMap.put(new Point(x, y), TextureManager.uploadTextureFromBufferedImage(sprites[x][y]));	// Slightly genius!
					}
				}
			}
			Logger.log(TileSheetParser.class, "Found " + count + " tiles.", Logger.INFO, true);
		} catch (IOException e) {
			Logger.logStackTrace(e);
			Logger.assertOrCrash(false, "Path is invalid: "  + path);
		}
	}
	
	/**
	 * Check to see if a tile is completely blank
	 * @param inSprite	Sprite to check for transparency
	 * @return	true if completely transparent
	 */
	private boolean checkSpriteBlank(BufferedImage inSprite) {
		for(int y = 0; y < tileSize; y++) {
			for(int x = 0; x < tileSize; x++) {
				if(inSprite.getRGB(x, y) >> 24 != 0x00) {	// TYPE_4BYTE_ABGR
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Getters and Setters
	 * ***************************************************************************************************
	 */

	public BufferedImage getSprite(int x, int y) {
		return sprites[x][y];
	}
	
	public int bindTileTexture(Point tilePoint) {
		if(textureMap.get(tilePoint) == null) {
			Logger.log(TileSheetParser.class, "Null texture: " + tilePoint.toString(), Logger.ERROR, false);
			return 0;
		} else {
			int name = textureMap.get(tilePoint);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
			return name;
		}
	}
	
}
