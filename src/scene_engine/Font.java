package scene_engine;

import java.awt.Point;

public abstract class Font {
    
    private TileSheetManager sheet;

    public abstract Point characterToGlyphSheetPoint(char c);

    public Font(String resourcePath, int glyphSize) {
        sheet = new TileSheetManager(resourcePath, glyphSize, false);
    }
    
    public TileSheetManager getSheet() { return sheet; }

}
