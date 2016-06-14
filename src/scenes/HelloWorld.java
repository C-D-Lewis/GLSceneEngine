package scenes;

import java.awt.Color;

import util.GLHelpers;
import core.BuildConfig;
import data.FontRenderer;
import data.FontRenderer.Align;
import engine.Scene;

public class HelloWorld extends Scene {
	
	public static final int SCENE_ID = 2364;

	@Override
	public void onLoad() {

	}

	@Override
	public void onUpdate() {

	}

	@Override
	public void onDraw() {
		
		GLHelpers.setColorFromColor(Color.WHITE);
		FontRenderer.drawString("Hello world!", BuildConfig.SCREEN_RECT, 16, Align.CENTER, Align.CENTER);
	}

	@Override
	public int getSceneId() {
		return SCENE_ID;
	}

}
