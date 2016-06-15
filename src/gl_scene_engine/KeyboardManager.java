package gl_scene_engine;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

/**
 * User Input Dispatcher class to maintain state of any implemented key for checking at any time.
 */
public class KeyboardManager {

	private static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
	
	private KeyboardManager() { }
	
	public static boolean getKeyState(int keyCode) {
		return (keys.get(keyCode) != null) ? keys.get(keyCode) : false;
	}
	
	public static void dispatchKeyEvent(int key, int action) {
		boolean pressed = (action == GLFW.GLFW_PRESS) || (action == GLFW.GLFW_REPEAT);
		keys.put(key, pressed);
		
		EventParams params = new EventParams();
		params.put(Events.PARAM_KEY, key);
		params.put(Events.PARAM_STATE, pressed);
		EventBus.broadcast(Events.EVENT_KEY_CHANGE, params);
	}
	
	public static class Events {
		
		public static final String
			EVENT_KEY_CHANGE = KeyboardManager.class.getName() + "KEY_CHANGE";
		
		public static final String
			PARAM_KEY = "KEY",
			PARAM_STATE = "STATE";
		
	}
	
}
