package scenes;

import java.awt.Color;

import core.BuildConfig;
import gl_scene_engine.FontRenderer;
import gl_scene_engine.GLHelpers;
import gl_scene_engine.Scene;
import gl_scene_engine.FontRenderer.Align;

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
