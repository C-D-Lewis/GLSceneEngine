package gl_scene_engine;

import java.awt.Point;


public abstract class Font {
	
	private TileSheetParser sheet;
	
	public Font(String resourcePath, int glyphSize) {
		sheet = new TileSheetParser(resourcePath, glyphSize, false);
	}
	
	public TileSheetParser getSheet() {
		return sheet;
	}
	
	/**
	 * @return a mapping of characters in a string to grid locations in the font's PNG
	 */
	public abstract Point characterToGlyphSheetPoint(char c);

}
