package scene_engine;

import java.awt.*;
import java.util.*;

public class EventBus {

    private class Config {
        private static final boolean LOG_ALL_EVENTS = false;
    }
    
    private static final HashMap<String, ArrayList<Receiver>> BUS = new HashMap<String, ArrayList<Receiver>>();
    
    private EventBus() { }
    
    public static void register(Receiver e) {
        synchronized(BUS) {
            String tag = e.getTag();
            if(Config.LOG_ALL_EVENTS) Logger.log(EventBus.class, "Registered event " + e.toString() + " for tag " + tag, Logger.INFO, true);

            if(BUS.containsKey(tag)) {
                ArrayList<Receiver> events = BUS.get(tag);
                if(!events.contains(e)) events.add(e);
                else Logger.log(EventBus.class, "Ignoring reregistering event " + e.toString(), Logger.WARN, true);
                return;
            }

            ArrayList<Receiver> events = new ArrayList<Receiver>();
            events.add(e);
            BUS.put(tag, events);
        }
    }
    
    public static void unregister(Receiver e) {
        synchronized(BUS) {
            String tag = e.getTag();
            ArrayList<Receiver> events = BUS.get(tag);
            Iterator<Receiver> iter = events.iterator();
            while(iter.hasNext()) {
                if(iter.next() == e) {
                    iter.remove();
                    if(Config.LOG_ALL_EVENTS) Logger.log(EventBus.class, "Deregistered event " + e.toString() + " for tag " + tag, Logger.INFO, true);
                }
            }
        }
    }
    
    public static void broadcast(String tag, Params params) {
        synchronized(BUS) {
            if(BUS.containsKey(tag)) {
                if(Config.LOG_ALL_EVENTS) Logger.log(EventBus.class, "Firing event: " + tag + " with params: " + params.toString(), Logger.DEBUG, false);
                ArrayList<Receiver> events = BUS.get(tag);
                for(Receiver e : events) {
                    try {
                        e.trigger(params);
                    } catch (ConcurrentModificationException ev) {
                        Logger.log(EventBus.class, "CCM for event: " + e.toString() + " with param: " + params.toString(), Logger.WARN, true);
                    }
                }
            }
        }
    }

    public static abstract class Receiver {

        private String tag;
        private boolean oneShot, fired;

        public abstract void onEvent(Params params);

        public Receiver(String tag, boolean oneShot) {
            this.tag = tag;
            this.oneShot = oneShot;
        }

        public void trigger(Params params) {
            if(oneShot && !fired) {
                onEvent(params);
                fired = true;
                return;
            }

            onEvent(params);
        }

        public String getTag() { return tag; }

    }

    public static class Params {

        private HashMap<String, Object> data = new HashMap<String, Object>();

        public Params put(String tag, Object value) {
            data.put(tag, value);
            return this;
        }

        public String getString(String tag) {
            if(data.containsKey(tag)) return (String)data.get(tag);

            Logger.log(Params.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return null;
        }

        public boolean getBoolean(String tag) {
            if(data.containsKey(tag)) return (boolean)data.get(tag);

            Logger.log(Params.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return false;
        }

        public Point getPoint(String tag) {
            if(data.containsKey(tag)) return (Point)data.get(tag);

            Logger.log(Params.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return null;
        }

        public int getInteger(final String tag) {
            if(data.containsKey(tag)) return (int)data.get(tag);

            Logger.log(Params.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return 0;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            for(Map.Entry<String, Object> entry : data.entrySet()) {
                builder.append(entry.getKey() + "=");
                builder.append(entry.getValue().toString());
                builder.append(" ");
            }

            builder.append("}");
            return builder.toString();
        }

    }

}
