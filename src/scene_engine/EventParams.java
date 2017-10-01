package scene_engine;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class EventParams {
    
    private HashMap<String, Object> data = new HashMap<String, Object>();
    
    public EventParams put(String tag, Object value) {
        data.put(tag, value);
        return this;
    }
    
    public String getString(String tag) {
        if(data.containsKey(tag)) return (String)data.get(tag);
        else {
            Logger.log(EventParams.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return "NONE";
        }
    }
    
    public boolean getBoolean(String tag) {
        if(data.containsKey(tag)) return (boolean)data.get(tag);
        else {
            Logger.log(EventParams.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return false;
        }
    }
    
    public Point getPoint(String tag) {
        if(data.containsKey(tag)) return (Point)data.get(tag);
        else {
            Logger.log(EventParams.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return null;
        }
    }

    public int getInteger(final String tag) {
        if(data.containsKey(tag)) return (int)data.get(tag);
        else {
            Logger.log(EventParams.class, "Tag " + tag + " not found!", Logger.ERROR, true);
            return 0;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(256);
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
