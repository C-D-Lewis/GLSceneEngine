package data;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;

import core.Logger;

/**
 * Class that maintains all resources from disk
 */
public class Resources {
	
	public static class Images {
		
		private static HashMap<String, Dimension> imageSizes = new HashMap<String, Dimension>();
		
		public static final String
			STUDIO_LOGO = "./res/images/studio_logo_src.png",
			CONTROLLER_PLUGGED = "./res/images/controller_plugged.png",
			CONTROLLER_UNPLUGGED = "./res/images/controller_unplugged.png";
		
		public static Dimension getImageSize(String path) {
			if(!imageSizes.containsKey(path)) {
				// Add new
				try {
					BufferedImage img = ImageIO.read(new File(path));
					Dimension size = new Dimension(img.getWidth(), img.getHeight());
					imageSizes.put(path, size);
					Logger.log(Images.class, "Cached image dimensions for " + path + ": " + size.toString(), Logger.INFO, true);
				} catch (Exception e) {
					String reason = "Unable to load image for dimension caching: " + path;
					Logger.log(Images.class, reason, Logger.ERROR, true);
					Logger.logStackTrace(e);
					Logger.assertOrCrash(false, reason);
				}
			}
			return imageSizes.get(path);
		}
		
	}
	
	public static class InteriorTileSheetPoints {
		
		public static final Point
			NULL = new Point(0, 0),
			FLOOR = new Point(1, 0),
			FLOOR_LIGHT = new Point(2, 0),
			WALL_LIGHT_UP = new Point(3, 0),
			WALL_LIGHT_RIGHT = new Point(4, 0),
			WALL_LIGHT_DOWN = new Point(5, 0),
			WALL_LIGHT_LEFT = new Point(6, 0),
			DOOR_CLOSED_LEFT = new Point(7, 0),
			DOOR_CLOSED_RIGHT = new Point(8, 0),
			DOOR_CLOSED_TOP = new Point(9, 0),
			DOOR_CLOSED_BOTTOM = new Point(10, 0),
			DOOR_OPEN_LEFT = new Point(11, 0),
			DOOR_OPEN_RIGHT = new Point(12, 0),
			DOOR_OPEN_TOP = new Point(13, 0),
			DOOR_OPEN_BOTTOM = new Point(14, 0),
			FLOOR_BUTTON_FALSE = new Point(15, 0),
			FLOOR_BUTTON_TRUE = new Point(16, 0),
			WALL_TOP = new Point(0, 1),
			WALL_BOTTOM = new Point(1, 1),
			WALL_LEFT = new Point(2, 1),
			WALL_RIGHT = new Point(3, 1),
			WALL_CORNER_TOP_LEFT = new Point(4, 1),
			WALL_CORNER_TOP_RIGHT = new Point(5, 1),
			WALL_CORNER_BOTTOM_LEFT = new Point(6, 1),
			WALL_CORNER_BOTTOM_RIGHT = new Point(7, 1),
			WALL_TOP_BOTTOM = new Point(8, 1),
			WALL_LEFT_RIGHT = new Point(9, 1),
			WALL_C_LEFT = new Point(10, 1),
			WALL_C_RIGHT = new Point(11, 1),
			WALL_C_BOTTOM = new Point(12, 1),
			WALL_C_TOP = new Point(13, 1);

	}
	
	public static class TileSheets {
		
		public static final String
			INTERIOR = "./res/tiles/interior.png";
		
		public static TileSheetParser interior;
	}
	
	public static class FontSheets {
		
		public static final String
			BLOCKY = "./res/fonts/blocky.png";
		
	}
	
	public static class ButtonIcons {
		
		public static final String
			ICON_PATH = "./res/images/buttons.png";
		
		public static final int
			ICON_SIZE = 16;
		
		public static final Point
			A = new Point(0, 0),
			B = new Point(1, 0),
			X = new Point(2, 0),
			Y = new Point(3, 0),
			KEYBOARD_GENERIC = new Point(4, 0),
			START = new Point(5, 0),
			KEYBOARD_RETURN = new Point(6, 0),
			KEYBOARD_ESCAPE = new Point(7, 0);
		
		public static TileSheetParser icons;
		
	}
	
	public static final int TILE_SRC_SIZE = 16;
	
	public static class SpritesSheets {
		
		public static final String
			PLAYER = "./res/sprites/player.png";
		
		public static TileSheetParser player;
		
	}
	
	public static class FilePaths {
		
		public static final String 
			LOG = "./debug.log",
			CONFIG = "./config.ini";
		
	}
	
	public static void initWithGL() {
		FontRenderer.loadFontFile(Resources.FontSheets.BLOCKY);
	}
	
}
