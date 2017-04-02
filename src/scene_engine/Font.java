package scene_engine;

import java.awt.Point;


public abstract class Font {
	
	private TileSheetParser sheet;
	
	public Font(String resourcePath, int glyphSize) {
		sheet = new TileSheetParser(resourcePath, glyphSize, false);
	}
	
	public TileSheetParser getSheet() {
		return sheet;
	}
	
	public abstract Point characterToGlyphSheetPoint(char c);

}
