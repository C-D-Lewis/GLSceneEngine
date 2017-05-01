package scenes;

import java.awt.Color;
import java.awt.Point;

import org.lwjgl.glfw.GLFW;

import scene_engine.Engine;
import scene_engine.EventBus;
import scene_engine.EventParams;
import scene_engine.EventReceiver;
import scene_engine.FontRenderer;
import scene_engine.GLHelpers;
import scene_engine.KeyboardManager;
import scene_engine.Logger;
import scene_engine.MouseManager;
import scene_engine.Scene;
import scene_engine.FontRenderer.Align;
import core.SEDemoMain;
import core.BuildConfig;

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
				Logger.log(HelloWorld.class, "Key " + glfwKey + " pressed " + pressed, Logger.INFO, false);
				
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
				
				Logger.log(SEDemoMain.class, str, Logger.INFO, false);
			}
			
		});
		EventBus.register(new EventReceiver(MouseManager.Events.EVENT_MOVED, false) {
			
			@Override
			public void onReceive(EventParams params) {
				Point pos = params.getPoint(MouseManager.Events.PARAM_POSITION);
				
				Logger.log(SEDemoMain.class, "Mouse now at: " + pos, Logger.INFO, false);
			}
			
		});
	}

	@Override
	public void onUpdate() { }

	@Override
	public void onDraw() {
		GLHelpers.pushColor(Color.WHITE);
		FontRenderer.drawString("Hello world!", BuildConfig.SCREEN_RECT, 16, Align.CENTER, Align.CENTER);
	}

}
