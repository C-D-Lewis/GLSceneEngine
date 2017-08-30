package scene_engine;

import java.awt.Point;

public class MouseManager {

    public static class Events {
        public static final String
                MOVED_POSITION_CHANGED = MouseManager.class.getName() + "MOVED_POSITION_CHANGED",
                BUTTON_STATE_CHANGED = MouseManager.class.getName() + "BUTTON_STATE",
                PARAM_POSITION = "POSITION",
                PARAM_BUTTON = "BUTTON",
                PARAM_PRESSED = "PRESSED";
    }
    
    private static boolean enabled;
    
    public static void dispatchMouseButtonEvent(int button, boolean pressed) {
        if(!enabled) return;

        EventParams params = new EventParams();
        params.put(Events.PARAM_BUTTON, button);
        params.put(Events.PARAM_PRESSED, pressed);
        EventBus.broadcast(Events.BUTTON_STATE_CHANGED, params);
    }
    
    public static void dispatchMousePositionEvent(Point pos) {
        if(!enabled) return;

        EventParams params = new EventParams();
        params.put(Events.PARAM_POSITION, pos);
        EventBus.broadcast(Events.MOVED_POSITION_CHANGED, params);
    }
    
    public static void setEnabled(boolean enabled) {
        MouseManager.enabled = enabled;
    }

}
