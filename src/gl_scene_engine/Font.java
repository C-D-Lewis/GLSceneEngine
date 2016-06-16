package gl_scene_engine;


public abstract class Font implements FontSheetMap {
	
	private TileSheetParser sheet;
	
	public Font(String resourcePath, int glyphSize) {
		sheet = new TileSheetParser(resourcePath, glyphSize, false);
	}
	
	public TileSheetParser getSheet() {
		return sheet;
	}

}
