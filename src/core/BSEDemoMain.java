package core;

import java.awt.Point;

import managers.ConfigManager;
import managers.KeyboardManager;
import managers.MouseManager;
import managers.SceneManager;

import org.lwjgl.glfw.GLFW;

import scenes.HelloWorld;
import data.Resources;
import engine.Engine;
import engine.EngineCallbacks;
import engine.EventBus;
import engine.EventParams;
import engine.EventReceiver;
import engine.Scene;

public class BSEDemoMain {
	
	public static void main(String[] args) {
		init();
	}
	
	private static void init() {
		Logger.setLogPath(BuildConfig.DEBUG_LOG_PATH);
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
				Resources.initWithGL();
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
