package scene_engine;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;

public class EventBus {

	private class Config {

		private static final boolean LOG_ALL_EVENTS = false;

	}
	
	private static final HashMap<String, ArrayList<EventReceiver>> BUS = new HashMap<String, ArrayList<EventReceiver>>();
	
	private EventBus() { }
	
	public static void register(EventReceiver e) {
		synchronized(BUS) {
			String tag = e.getTag();
			
			if(BUS.containsKey(tag)) {
				ArrayList<EventReceiver> events = BUS.get(tag);
				if(!events.contains(e)) {
					events.add(e);
				} else {
					Logger.log(EventBus.class, "Ignoring reregistering event " + e.toString(), Logger.WARN, true);
				}
			} else {
				ArrayList<EventReceiver> events = new ArrayList<EventReceiver>();
				events.add(e);
				BUS.put(tag, events);
			}
			
			if(Config.LOG_ALL_EVENTS) {
				Logger.log(EventBus.class, "Registered event " + e.toString() + " for tag " + tag, Logger.INFO, true);
			}
		}
	}
	
	public static void unregister(EventReceiver e) {
		synchronized(BUS) {
			String tag = e.getTag();
			
			ArrayList<EventReceiver> events = BUS.get(tag);
			Iterator<EventReceiver> iter = events.iterator();
			while(iter.hasNext()) {
				if(iter.next() == e) {
					iter.remove();
					if(Config.LOG_ALL_EVENTS) {
						Logger.log(EventBus.class, "Deregistered event " + e.toString() + " for tag " + tag, Logger.INFO, true);
					}
				}
			}
		}
	}
	
	public static void broadcast(String tag, EventParams params) {
		synchronized(BUS) {
			if(BUS.containsKey(tag)) {
				if(Config.LOG_ALL_EVENTS) {
					Logger.log(EventBus.class, "Firing event: " + tag + " with params: " + params.toString(), Logger.DEBUG, false);
				}
				
				ArrayList<EventReceiver> events = BUS.get(tag);
				for(EventReceiver e : events) {
					try {
						e.trigger(params);
					} catch (ConcurrentModificationException ev) {
						//FIXME ConcurrentModificationException firing events
						Logger.log(EventBus.class, "CCM for event: " + e.toString() + " with param: " + params.toString(), Logger.WARN, true);
					}
				}
			}
		}
	}

}
