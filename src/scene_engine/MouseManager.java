package scene_engine;

import java.awt.Point;

public class MouseManager {

    public static class Events {
        public static final String
                MOVED_POSITION_CHANGED = MouseManager.class.getName() + "MOVED_POSITION_CHANGED",
                BUTTON_STATE_CHANGED = MouseManager.class.getName() + "BUTTON_STATE",
                MOUSE_SCROLL_CHANGED = MouseManager.class.getName() + "SCROLL_CHANGED",
                PARAM_POSITION = "POSITION",
                PARAM_BUTTON = "BUTTON",
                PARAM_PRESSED = "PRESSED",
                PARAM_SCROLL_DIRECTION = "SCROLL_DIRECTION";
    }

    public static class ScrollDirection {
        public static final int
            UP = 1,
            DOWN = -1;
    }
    
    public static void dispatchMouseButtonEvent(int button, boolean pressed) {
        EventBus.broadcast(Events.BUTTON_STATE_CHANGED,
            new EventParams()
                .put(Events.PARAM_BUTTON, button)
                .put(Events.PARAM_PRESSED, pressed));
    }
    
    public static void dispatchMousePositionEvent(Point pos) {
        ;
        EventBus.broadcast(Events.MOVED_POSITION_CHANGED,
            new EventParams()
                .put(Events.PARAM_POSITION, pos));
    }

    public static void dispatchMouseScrollEvent(final double yOffset) {
        EventBus.broadcast(Events.MOUSE_SCROLL_CHANGED,
            new EventParams()
                .put(Events.PARAM_SCROLL_DIRECTION, (int)Math.round(yOffset)));
    }
    
}
