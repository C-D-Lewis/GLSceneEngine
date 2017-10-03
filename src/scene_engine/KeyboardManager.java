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
    
    private KeyboardManager() { }
    
    public static boolean getKeyState(int keyCode) { return (keys.get(keyCode) != null) ? keys.get(keyCode) : false; }
    
    public static void broadcastKeyEvent(int key, int action) {
        boolean pressed = (action == GLFW.GLFW_PRESS) || (action == GLFW.GLFW_REPEAT);
        keys.put(key, pressed);
        
        EventBus.broadcast(Events.KEY_CHANGE,
            new EventBus.Params()
                .put(Events.PARAM_KEY, key)
                .put(Events.PARAM_STATE, pressed));
    }
    
}
