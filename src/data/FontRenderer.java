package data;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import util.GLHelpers;
import core.BuildConfig;
import core.Logger;

public class FontRenderer {
	
	public class Align {
		
		public static final int
			LEFT = 0,
			CENTER = 1,
			RIGHT = 2,
			TOP = 3,
			BOTTOM = 4;
		
	}
	
	private static final Dimension 
		FONT_GLYTH_SIZE = new Dimension(8, 8),
		GLYPH_GRID_SIZE = new Dimension(5, 8);
	
	private static TileSheetParser sheet;
	
	public static void loadFontFile(String sheetPath) {
		sheet = new TileSheetParser(sheetPath, FONT_GLYTH_SIZE.width, false);
	}
	
	private static Point characterToGlythPoint(char c) {
		int index = c - 'a';
//		System.out.println(c + " -> " + index);
		
		if(index >= 0 && index < 26) {
			// a - z
			return i2xy(index);
		} else if(index >= -49 && index < -39) {
			// 0 - 9
			return i2xy(index + 49 + 26);
		} else if(index >= -32 && index < -6) {
			// A - Z, shift to 0
			return i2xy(index + 32);
		} else {
			// Special characters
			switch(c) {
				case ' ':
					return new Point(1, 7);
				case '.':
					return new Point(2, 7);
				case '?':
					return new Point(3, 7);
				case '!':
					return new Point(4, 7);
				default:
					Logger.log(FontRenderer.class, "Invalid character: " + c, Logger.WARN, false);
					return new Point(0, 0);
			}
		}
	}
	
	private static Point i2xy(int i) {
		return new Point(i % GLYPH_GRID_SIZE.width, i / GLYPH_GRID_SIZE.width);
	}
	
	public static void testFont() {
		GLHelpers.setColorFromColor(Color.WHITE);
		String text = "abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ.0123456789 .!?";
		int fontSize = 16;
		int width = 800;
		int y = 10;

		Dimension textSize = getTextSize(text, width, fontSize);
		drawString(text, new Rectangle(10, y, width, 50), fontSize, Align.LEFT, Align.TOP);
		
		y += textSize.height;
		String text2 = "Testing font glyph rendering!";
		textSize = getTextSize(text2, width, fontSize);
		drawString(text2, new Rectangle(10, y, width, textSize.height), fontSize, Align.CENTER, Align.TOP);
		
		y += textSize.height;
		drawString("LEFT", new Rectangle(0, y, width, textSize.height), fontSize, Align.LEFT, Align.TOP);
		y += textSize.height;
		drawString("CENTER", new Rectangle(0, y, width, textSize.height), fontSize, Align.CENTER, Align.TOP);
		y += textSize.height;
		drawString("RIGHT", new Rectangle(0, y, width, textSize.height), fontSize, Align.RIGHT, Align.TOP);
		
		y += textSize.height;
		drawString("TOP", new Rectangle(0, y, width, 100), fontSize, Align.LEFT, Align.TOP);
		drawString("CENTER", new Rectangle(0, y, width, 100), fontSize, Align.LEFT, Align.CENTER);
		drawString("BOTTOM", new Rectangle(0, y, width, 100), fontSize, Align.LEFT, Align.BOTTOM);
	}
	
	public static Dimension getTextSize(String content, int width, int fontSize) {
		char[] string = new char[content.length()];
		content.getChars(0, content.length(), string, 0);
		int glyphGap = fontSize / 10 > 0 ? fontSize / 10 : 1;
		int maxWidth = 0;
		
		Point renderPoint = new Point();
		int possibleMaxWidth = 0;
		for(int i = 0; i < content.length(); i++) {
			int delta = (fontSize + 2 * glyphGap);
			renderPoint.x += delta;
			possibleMaxWidth += delta;
			if(renderPoint.x + fontSize > width) {
				// A new line, record max width
				if(maxWidth == 0 || maxWidth < renderPoint.x + fontSize) {
					maxWidth = renderPoint.x - glyphGap; 
				}

				renderPoint.x = 0;
				renderPoint.y += fontSize + (2 * glyphGap);
				
			}
		}
		
		return new Dimension(maxWidth == 0 ? possibleMaxWidth : maxWidth, 
						     renderPoint.y + fontSize + (2 * glyphGap));
	}
	
	private static void resetRenderPointX(Rectangle bounds, Point renderPoint, Dimension textSize, int hAlign) {
		renderPoint.x = bounds.x;
		switch(hAlign) {
			case Align.LEFT:
				break;
			case Align.CENTER:
				renderPoint.x = bounds.x + ((bounds.width - textSize.width) / 2);
				break;
			case Align.RIGHT:
				renderPoint.x = bounds.x + (bounds.width - textSize.width);
				break;
			default:
				Logger.log(FontRenderer.class, "Incompatible hAlign: " + hAlign, Logger.ERROR, false);
			    break;
		}
	}
	
	public static void drawString(String content, Rectangle bounds, int fontSize, int hAlign, int vAlign) {
		Dimension textSize = getTextSize(content, bounds.width, fontSize);
		int glyphGap = fontSize / 10 > 0 ? fontSize / 10 : 1;
		
		String[] words = content.split(" ");
		ArrayList<String> lines = new ArrayList<String>();
		
		if(!content.contains(" ")) {
			// Single word
			lines.add(words[0]);
		} else {
			// For every word...
			int lineLength = 0;
			String line = "";
			for(String word : words) {
				// Get length of this word
				int wordWidth = word.length() * fontSize;	
				
				// If it will fit, append it
				if(wordWidth < bounds.width - lineLength) {
					line += word + " ";
					lineLength += (word.length() + 1) * fontSize;
				} else {
					// Remove trailing space
					if(line.contains(" ")) {
						line = line.substring(0, line.length() - 1);
					}
					
					// Begin a new line
					lines.add(line);
					line = word;
					lineLength = (word.length() + 1) * fontSize;
				}
				
				if(word.equals(words[words.length - 1])) {
					// If the last word, store the line
					lines.add(line);
				}
			}
		}
		
		// Apply aligns
		Point renderPoint = new Point(bounds.x, bounds.y);
		resetRenderPointX(bounds, renderPoint, textSize, hAlign);
		switch(vAlign) {
			case Align.TOP:
				break;
			case Align.CENTER: {
				int diff = bounds.height - textSize.height;
				renderPoint.y = bounds.y + (Math.abs(diff / ((diff > 0) ? 2 : 4)));	
			}   break;
			case Align.BOTTOM:
				renderPoint.y = bounds.y + (bounds.height - textSize.height);
				break;
			default:
				Logger.log(FontRenderer.class, "Incompatible vAlign: " + vAlign, Logger.ERROR, false);
				break;
		}
		
		for(String l : lines) {
			textSize = getTextSize(l, bounds.width, fontSize);
			resetRenderPointX(bounds, renderPoint, textSize, hAlign);
			if(BuildConfig.DRAW_TEXT_BOUNDS) {
				GLHelpers.setColorFromColor(Color.PINK);
				GLHelpers.fillRect(new Rectangle(renderPoint.x, renderPoint.y, textSize.width, textSize.height));
				GLHelpers.popColor();
			}
			
			// Get characters in this line
			char[] chars = new char[l.length()];
			l.getChars(0, l.length(), chars, 0);
			
			// Render them
			for(char c : chars) {
				Point sheetPoint = characterToGlythPoint(c);
				int name = sheet.bindTileTexture(sheetPoint);
				GLHelpers.drawImageFromTextureName(name, renderPoint.x, renderPoint.y, fontSize, fontSize);
	
				renderPoint.x += (fontSize + 2 * glyphGap);
			}
			
			// New line
			renderPoint.y += fontSize + (2 * glyphGap);
		}
	}

}
