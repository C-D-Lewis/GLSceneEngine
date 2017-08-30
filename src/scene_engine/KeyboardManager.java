package scene_engine;

import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

public class KeyboardManager {

    public static class Events {
        public static final String
                KEY_CHANGE = "KEY_CHANGE",
                PARAM_KEY = "KEY",
                PARAM_STATE = "STATE";
    }

    private static HashMap<Integer, Boolean> keys = new HashMap<Integer, Boolean>();
    
    private static boolean enabled;
    
    private KeyboardManager() { }
    
    public static boolean getKeyState(int keyCode) { return (keys.get(keyCode) != null) ? keys.get(keyCode) : false; }
    
    public static void dispatchKeyEvent(int key, int action) {
        if(!enabled) return;

        boolean pressed = (action == GLFW.GLFW_PRESS) || (action == GLFW.GLFW_REPEAT);
        keys.put(key, pressed);
        
        EventParams params = new EventParams();
        params.put(Events.PARAM_KEY, key);
        params.put(Events.PARAM_STATE, pressed);
        EventBus.broadcast(Events.KEY_CHANGE, params);
    }
    
    public static void setEnabled(boolean enabled) {
        KeyboardManager.enabled = enabled;
    }
    
}
