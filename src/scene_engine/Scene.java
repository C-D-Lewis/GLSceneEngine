package scene_engine;

import java.util.ArrayList;


public abstract class Scene {
	
	private ArrayList<EventReceiver> events = new ArrayList<EventReceiver>();
	
	private boolean loaded;
	
	public abstract void onLoad();
	
	public void onResume() {
		// Register events
		for(EventReceiver e : events) {
			EventBus.register(e);
		}
	}
	
	public void onPause() {
		// Deregister events when Scene is not visible
		for(EventReceiver e : events) {
			EventBus.deregister(e);
		}
	}
	
	public void manageEventReceiver(EventReceiver e) {
		events.add(e);
		EventBus.register(e);
	}
	
	public void onSecondThreadFrame() { }

	public void onDrawWhileLoading() { }

	public abstract void onUpdate();

	public abstract void onDraw();

	public abstract int getSceneId();
	
	public boolean getHasLoaded() {
		return loaded;
	}

	public void setHasLoaded(boolean hasLoaded) {
		loaded = hasLoaded;
	}
	
}
