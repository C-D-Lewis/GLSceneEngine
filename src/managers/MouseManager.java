package managers;

import java.awt.Point;

import engine.EventBus;
import engine.EventParams;

public class MouseManager {
	
	public static final int
		MOUSE_LEFT = 0,
		MOUSE_RIGHT = 1,
		MOUSE_MIDDLE = 2;

	public static void dispatchMouseButtonEvent(int button, boolean pressed) {
		EventParams params = new EventParams();
		params.put(Events.PARAM_BUTTON, button);
		params.put(Events.PARAM_PRESSED, pressed);
		EventBus.broadcast(Events.EVENT_BUTTON_STATE, params);
	}
	
	public static void dispatchMousePositionEvent(Point pos) {
		EventParams params = new EventParams();
		params.put(Events.PARAM_POSITION, pos);
		EventBus.broadcast(Events.EVENT_MOVED, params);
	}
	
	public static class Events {
		
		public static final String
			EVENT_MOVED = "MOVED",
			EVENT_BUTTON_STATE = "BUTTON_STATE";
		
		public static final String
			PARAM_POSITION = "POSITION",
			PARAM_BUTTON = "BUTTON",
			PARAM_PRESSED = "PRESSED";
		
	}

}
