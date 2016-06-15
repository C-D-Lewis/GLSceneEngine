package gl_scene_engine;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

/**
 * For when passing abstract methods up and down three classes seems a bit silly
 */
public class EventBus {
	
	// I love how easy parameterized types makes this!
	private static HashMap<String, ArrayList<EventReceiver>> bus = new HashMap<String, ArrayList<EventReceiver>>();
	
	private EventBus() { }
	
	/**
	 * Register a new event for this broadcast tag
	 * Deregister in onPause if the scene goes away
	 */
	public static void register(EventReceiver e) {
		synchronized(bus) {
			String tag = e.getTag();
			
			if(bus.containsKey(tag)) {
				ArrayList<EventReceiver> events = bus.get(tag);
				if(!events.contains(e)) {
					events.add(e);
				} else {
					Logger.log(EventBus.class, "Ignoring reregistering event " + e.toString(), Logger.WARN, true);
				}
			} else {
				ArrayList<EventReceiver> events = new ArrayList<EventReceiver>();
				events.add(e);
				bus.put(tag, events);
			}
			Logger.log(EventBus.class, "Registered event " + e.toString() + " for tag " + tag, Logger.INFO, true);
		}
	}
	
	public static void deregister(EventReceiver e) {
		synchronized(bus) {
			String tag = e.getTag();
			
			ArrayList<EventReceiver> events = bus.get(tag);
			Iterator<EventReceiver> iter = events.iterator();
			while(iter.hasNext()) {
				if(iter.next() == e) {
					iter.remove();
					Logger.log(EventBus.class, "Deregistered event " + e.toString() + " for tag " + tag, Logger.INFO, true);
				}
			}
		}
	}
	
	public static void broadcast(String tag, EventParams params) {
		synchronized(bus) {
			if(bus.containsKey(tag)) {
				if(Config.LOG_ALL_EVENTS) {
					Logger.log(EventBus.class, "Firing event: " + tag + " with params: " + params.toString(), Logger.DEBUG, false);
				}
				
				ArrayList<EventReceiver> events = bus.get(tag);
				for(EventReceiver e : events) {
					try {
						e.trigger(params);
					} catch (ConcurrentModificationException ev) {
						//FIXME ConcurrentModificationException firing events
						Logger.log(EventBus.class, "CCM for event: " + e.toString() + " with param: " + params.toString(), Logger.WARN, true);
					}
				}
			} else {
				Logger.log(EventBus.class, "No events registered for event tag " + tag, Logger.WARN, true);
			}
		}
	}
	
	private class Config {
		
		private static final boolean
			LOG_ALL_EVENTS = false;
		
	}

}
