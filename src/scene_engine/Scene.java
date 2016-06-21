package scene_engine;

import java.util.ArrayList;


/**
 * Basic scene to be extended by all other derivative scenes
 * Abstract functions must be implemented. Other events can be optionally overridden.
 */
public abstract class Scene {
	
	private ArrayList<EventReceiver> events = new ArrayList<EventReceiver>();
	
	private boolean loaded;
	
	/**
	 * Work to do in background when loading a stage
	 */
	public abstract void onLoad();
	
	/**
	 * Scene was previously loaded, but shown again
	 */
	public void onResume() {
		// Register events
		for(EventReceiver e : events) {
			EventBus.register(e);
		}
	}
	
	/**
	 * Scene cleanup
	 */
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
	
	/**
	 * Non-renderable work to be calculated out of sync
	 */
	public void onSecondThreadFrame() { }

	/**
	 * Drawing to do on main thread while loading on secondary thread
	 */
	public void onDrawWhileLoading() { }

	/**
	 * Main foreground logic loop once loaded
	 */
	public abstract void onUpdate();

	/**
	 * Drawing to do once loaded
	 */
	public abstract void onDraw();

	/**
	 * Used for scene-conditional global operations
	 */
	public abstract int getSceneId();
	
	/**
	 * Used to prevent loading more than once per run
	 * @return true if the scene has loaded, otherwise false
	 */
	public boolean getHasLoaded() {
		return loaded;
	}

	/**
	 * Set whether this scene has now been loaded
	 * @param hasLoaded The value to set
	 */
	public void setHasLoaded(boolean hasLoaded) {
		loaded = hasLoaded;
	}
	
}
