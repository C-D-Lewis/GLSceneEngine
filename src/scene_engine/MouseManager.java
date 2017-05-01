package scene_engine;

import java.awt.Point;

public class MouseManager {

	public static class Events {

		public static final String
				EVENT_MOVED = MouseManager.class.getName() + "MOVED",
				EVENT_BUTTON_STATE = MouseManager.class.getName() + "BUTTON_STATE";

		public static final String
				PARAM_POSITION = "POSITION",
				PARAM_BUTTON = "BUTTON",
				PARAM_PRESSED = "PRESSED";

	}
	
	private static boolean enabled;
	
	public static void dispatchMouseButtonEvent(int button, boolean pressed) {
		if(!enabled) {
			return;
		}
		
		EventParams params = new EventParams();
		params.put(Events.PARAM_BUTTON, button);
		params.put(Events.PARAM_PRESSED, pressed);
		EventBus.broadcast(Events.EVENT_BUTTON_STATE, params);
	}
	
	public static void dispatchMousePositionEvent(Point pos) {
		if(!enabled) {
			return;
		}
		
		EventParams params = new EventParams();
		params.put(Events.PARAM_POSITION, pos);
		EventBus.broadcast(Events.EVENT_MOVED, params);
	}
	
	public static void setEnabled(boolean enabled) {
		MouseManager.enabled = enabled;
	}

}
