package scene_engine;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

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

    public static void broadcastMouseScrollEvent(final double yOffset) {
        EventBus.broadcast(Events.MOUSE_SCROLL_CHANGED,
            new EventBus.Params()
                .put(Events.PARAM_SCROLL_DIRECTION, (int)Math.round(yOffset)));
    }

    public static void useJFrame(JFrame window) {
        window.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                broadcastButtonEvent(e.getButton(), true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                broadcastButtonEvent(e.getButton(), false);
            }

            @Override
            public void mouseClicked(MouseEvent e) { }
        });
        window.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseMoved(MouseEvent e) {
                broadcastPositionEvent(new Point(e.getX(), e.getY()));
            }

            @Override
            public void mouseDragged(MouseEvent e) { }
        });
    }
    
}
