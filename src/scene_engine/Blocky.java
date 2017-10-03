package scene_engine;

import java.awt.Dimension;
import java.awt.Point;

public class Blocky extends FontRenderer.Font {

    public static final int GLYPH_SIZE_PIXELS = 8;
    public static final Dimension GLYPH_GRID_SIZE = new Dimension(5, 9);

    public Blocky() {
        super("./res/fonts/blocky.png", GLYPH_SIZE_PIXELS);
    }

    @Override
    public Point characterToGlyphSheetPoint(char c) {
        int index = c - 'a';
        
        if(index >= 0 && index < 26) {         return Helpers.i2xy(index, GLYPH_GRID_SIZE.width); } // a - z
        else if(index >= -49 && index < -39) { return Helpers.i2xy(index + 49 + 26, GLYPH_GRID_SIZE.width); }  // 0 - 9
        else if(index >= -32 && index < -6) {  return Helpers.i2xy(index + 32, GLYPH_GRID_SIZE.width); }  // A - Z, shift to 0
        else {
            switch(c) {  // Special characters
                case ' ': return new Point(1, 7);
                case '.': return new Point(2, 7);
                case '?': return new Point(3, 7);
                case '!': return new Point(4, 7);
                case '/': return new Point(0, 8);
                case '-': return new Point(1, 8);
                case ':': return new Point(2, 8);
                case ',': return new Point(3, 8);
                default:
                    Logger.log(FontRenderer.class, "Invalid character: " + c, Logger.WARN, false);
                    return characterToGlyphSheetPoint('?');
            }
        }
    }

}
