package scene_engine;

import java.awt.*;
import java.util.ArrayList;

public abstract class Scene implements Loopable {
    
    private ArrayList<EventBus.Receiver> events = new ArrayList<EventBus.Receiver>();
    
    private boolean loaded;
    
    public void onResume() {
        for(EventBus.Receiver e : events) EventBus.register(e);
    }
    
    public void onPause() {
        for(EventBus.Receiver e : events) EventBus.unregister(e);
    }
    
    public void manageEventReceiver(EventBus.Receiver e) {
        events.add(e);
        EventBus.register(e);
    }
    
    public void drawWhileLoading() { }

    public void drawWhileLoading(Graphics2D g2d) { }

    public abstract void onLoad();

    public boolean hasLoaded() { return loaded; }

    public void setLoaded(boolean hasLoaded) { loaded = hasLoaded; }
    
}
