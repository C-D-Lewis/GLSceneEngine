package core;

import java.awt.Point;

import org.lwjgl.glfw.GLFW;

import scenes.HelloWorld;
import gl_scene_engine.ConfigManager;
import gl_scene_engine.Engine;
import gl_scene_engine.EngineCallbacks;
import gl_scene_engine.EventBus;
import gl_scene_engine.EventParams;
import gl_scene_engine.EventReceiver;
import gl_scene_engine.FontRenderer;
import gl_scene_engine.KeyboardManager;
import gl_scene_engine.Logger;
import gl_scene_engine.MouseManager;
import gl_scene_engine.Scene;
import gl_scene_engine.SceneManager;

public class BSEDemoMain {
	
	public static void main(String[] args) {
		init();
	}
	
	private static void init() {
		Logger.setLogPath("./debug-log.log");
		ConfigManager.load();
		setupEngine();
	}

	private static void setupEngine() {
		String title = BuildConfig.GAME_NAME + " v" + BuildConfig.VERSION_STRING;
		boolean fullscreen = ConfigManager.getBoolean(ConfigManager.DB_KEY_FULLSCREEN, true);

		Engine.start(title, BuildConfig.SCREEN_RECT, fullscreen, new EngineCallbacks() {
			
			@Override
			public void onFirstLoad() { 
				// Register for keypresses
				EventBus.register(new EventReceiver(KeyboardManager.Events.EVENT_KEY_CHANGE, false) {
					
					@Override
					public void onReceive(EventParams params) {
						int glfwKey = params.getInt(KeyboardManager.Events.PARAM_KEY);
						boolean pressed = params.getBoolean(KeyboardManager.Events.PARAM_STATE);
						
						if(glfwKey == GLFW.GLFW_KEY_ESCAPE && !pressed) {
							// Escape to exit
							onWindowClose();
						}
					}
					
				});
				
				// Register for mouse input
				EventBus.register(new EventReceiver(MouseManager.Events.EVENT_BUTTON_STATE, false) {
					
					@Override
					public void onReceive(EventParams params) {
						boolean pressed = params.getBoolean(MouseManager.Events.PARAM_PRESSED);
						String str = "Mouse " + (pressed ? "clicked!" : "released!");
						
						Logger.log(BSEDemoMain.class, str, Logger.INFO, false);
					}
					
				});
				EventBus.register(new EventReceiver(MouseManager.Events.EVENT_MOVED, false) {
					
					@Override
					public void onReceive(EventParams params) {
						Point pos = params.getPoint(MouseManager.Events.PARAM_POSITION);
						
						Logger.log(BSEDemoMain.class, "Mouse now at: " + pos, Logger.INFO, false);
					}
					
				});
			}
			
			@Override
			public void onLoadResources() {
				// initialize resources that require OpenGL to be initialized
				FontRenderer.loadFontFile("./res/fonts/blocky.png");
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
			}

			@Override
			public void onWindowClose() {
				System.exit(0);
			}
			
		});
	}
	
}
