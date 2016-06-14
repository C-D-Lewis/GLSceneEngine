package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;

import managers.TextureManager;

import org.lwjgl.opengl.GL11;

import core.Logger;
import data.FontRenderer;

public class GLHelpers {
	
	private static Color lastColor, currentColor;
	
	public static void fillRect(Rectangle bounds) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(bounds.x, bounds.y);
		GL11.glVertex2f(bounds.x, bounds.y + bounds.height);
		GL11.glVertex2f(bounds.x + bounds.width, bounds.y + bounds.height);
		GL11.glVertex2f(bounds.x + bounds.width, bounds.y);
		GL11.glEnd();
	}
	
	public static void drawRect(Rectangle bounds) {
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex2f(bounds.x, bounds.y);
		GL11.glVertex2f(bounds.x, bounds.y + bounds.height);
		GL11.glVertex2f(bounds.x, bounds.y + bounds.height);
		GL11.glVertex2f(bounds.x + bounds.width, bounds.y + bounds.height);
		GL11.glVertex2f(bounds.x + bounds.width, bounds.y + bounds.height);
		GL11.glVertex2f(bounds.x + bounds.width, bounds.y);
		GL11.glVertex2f(bounds.x + bounds.width, bounds.y);
		GL11.glVertex2f(bounds.x, bounds.y);
		GL11.glEnd();
	}
	
	public static void drawImageFromFilePath(String path, int x, int y, int w, int h) {
		int name = TextureManager.getTextureName(path);
		drawImageFromTextureName(name, x, y, w, h);
	}
	
	public static void drawImageFromTextureName(int name, int x, int y, int w, int h) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, name);
		
		// CRITICAL - Reset the color, maybe not
	    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F); 
		
//		// Reset the alpha
//		float r = (float)currentColor.getRed() / 255.0F;
//		float g = (float)currentColor.getGreen() / 255.0F;
//		float b = (float)currentColor.getBlue() / 255.0F;
//		GL11.glColor4f(r, g, b, 1.0F);
	    
	    // LB, RB, RT, LT
	    GL11.glBegin(GL11.GL_QUADS);
	    GL11.glTexCoord2f(0.0F, 1.0F);
	    GL11.glVertex2f(x, y + h);
	    GL11.glTexCoord2f(1.0F, 1.0F);
	    GL11.glVertex2f(x + w, y + h);
	    GL11.glTexCoord2f(1.0F, 0.0F);
	    GL11.glVertex2f(x + w, y);
	    GL11.glTexCoord2f(0.0F, 0.0F);
	    GL11.glVertex2f(x, y);
	    GL11.glEnd();
	    
	    GL11.glDisable(GL11.GL_TEXTURE_2D);
	}
	
	public static Dimension getTextSize(String content, int width, int fontSize) {
		return FontRenderer.getTextSize(content, width, fontSize);
	}
	
	public static void setColorFromColor(Color color) {
		lastColor = currentColor;
		currentColor = color;
		GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
	}
	
	public static Color getCurrentColor() {
		return currentColor;
	}
	
	public static void popColor() {
		currentColor = lastColor;
		setColorFromColor(currentColor);
	}
	
	public static void test(Rectangle bounds) {
		GL11.glBegin(GL11.GL_TRIANGLES);
		GL11.glColor3f(0.0F, 1.0F, 0.0F);
		GL11.glVertex2f(bounds.width / 2, 0);
		GL11.glVertex2f(bounds.width, bounds.height);
		GL11.glVertex2f(0, bounds.height);
		GL11.glEnd();
	}
	
	public static void logGLError(String context) {
		Logger.log(GLHelpers.class, "(" + context + ")" + " -> " + GL11.glGetError(), Logger.DEBUG, true);
	}
	
}
