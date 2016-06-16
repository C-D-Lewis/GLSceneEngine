package core;

import gl_scene_engine.Blocky;
import gl_scene_engine.Engine;
import gl_scene_engine.EngineCallbacks;
import gl_scene_engine.FontRenderer;
import gl_scene_engine.Logger;
import gl_scene_engine.Scene;
import gl_scene_engine.SceneManager;
import scenes.HelloWorld;

public class BSEDemoMain {
	
	public static void main(String[] args) {
		init();
	}
	
	private static void init() {
		Logger.setLogPath("./debug-log.log");
		setupEngine();
	}

	private static void setupEngine() {
		String title = BuildConfig.GAME_NAME + " v" + BuildConfig.VERSION_STRING;

		Engine.start(title, BuildConfig.SCREEN_RECT, new EngineCallbacks() {
			
			@Override
			public void onFirstLoad() { }
			
			@Override
			public void onLoadResources() {
				// initialize resources that require OpenGL to be initialized
				FontRenderer.setFont(new Blocky());
			}
			
			@Override
			public Scene getInitialGameScene() {
				// First game Scene once OpenGL is initialized
				return new HelloWorld();
			}
			
			@Override
			public void onUpdate() {
				// Update the current Scene's logic (and all sub-components)
				SceneManager.onUpdate();
			}
			
			@Override
			public void onDraw() {
				// Draw the current Scene (and all sub-components)
				SceneManager.onDraw();
			}

			@Override
			public void onSecondThreadFrame() {
				// Perform any per-frame asynchronous work off the drawing thread
				SceneManager.onSecondThreadFrame();
			}

			@Override
			public void onWindowClose() {
				System.exit(0);
			}
			
		});
	}
	
}
