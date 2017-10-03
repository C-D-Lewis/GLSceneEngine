package scene_engine;

import java.awt.Point;

public class MouseManager {

    public static class Events {
        public static final String
                POSITION_CHANGED = "POSITION_CHANGED",
                BUTTON_STATE_CHANGED = "BUTTON_STATE",
                MOUSE_SCROLL_CHANGED = "SCROLL_CHANGED",
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
    
    public static void broadcastButtonEvent(final int button, final boolean pressed) {
        EventBus.broadcast(Events.BUTTON_STATE_CHANGED,
            new EventBus.Params()
                .put(Events.PARAM_BUTTON, button)
                .put(Events.PARAM_PRESSED, pressed));
    }
    
    public static void broadcastPositionEvent(final Point pos) {
        EventBus.broadcast(Events.POSITION_CHANGED,
            new EventBus.Params()
                .put(Events.PARAM_POSITION, pos));
    }

    public static void dispatchMouseScrollEvent(final double yOffset) {
        EventBus.broadcast(Events.MOUSE_SCROLL_CHANGED,
            new EventBus.Params()
                .put(Events.PARAM_SCROLL_DIRECTION, (int)Math.round(yOffset)));
    }
    
}
