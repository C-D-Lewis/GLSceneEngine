package scenes;

import java.awt.Color;
import java.awt.Point;

import org.lwjgl.glfw.GLFW;

import core.BSEDemoMain;
import core.BuildConfig;
import gl_scene_engine.Engine;
import gl_scene_engine.EventBus;
import gl_scene_engine.EventParams;
import gl_scene_engine.EventReceiver;
import gl_scene_engine.FontRenderer;
import gl_scene_engine.GLHelpers;
import gl_scene_engine.KeyboardManager;
import gl_scene_engine.Logger;
import gl_scene_engine.MouseManager;
import gl_scene_engine.Scene;
import gl_scene_engine.FontRenderer.Align;

public class HelloWorld extends Scene {
	
	public static final int SCENE_ID = 2364;

	@Override
	public void onLoad() {
		// Register for keypresses
		EventBus.register(new EventReceiver(KeyboardManager.Events.EVENT_KEY_CHANGE, false) {
			
			@Override
			public void onReceive(EventParams params) {
				int glfwKey = params.getInt(KeyboardManager.Events.PARAM_KEY);
				boolean pressed = params.getBoolean(KeyboardManager.Events.PARAM_STATE);
				
				// Press escape to exit
				if(glfwKey == GLFW.GLFW_KEY_ESCAPE && !pressed) {
					Engine.stop();
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
	public void onUpdate() { }

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
