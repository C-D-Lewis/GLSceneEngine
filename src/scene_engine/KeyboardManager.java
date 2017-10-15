package scene_engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;

import javax.swing.*;

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

    public static void useJFrame(JFrame window) {
        window.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                broadcastKeyEvent(e.getKeyCode(), GLFW.GLFW_PRESS);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                broadcastKeyEvent(e.getKeyCode(), GLFW.GLFW_RELEASE);
            }

            @Override
            public void keyTyped(KeyEvent e) { }
        });
    }
    
}
