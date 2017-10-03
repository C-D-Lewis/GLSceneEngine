package scene_engine;

import java.util.ArrayList;

public abstract class Scene {
    
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
    
    public void onDrawWhileLoading() { }

    public abstract void onLoad();
    public abstract void onUpdate();
    public abstract void onDraw();

    public boolean hasLoaded() { return loaded; }

    public void setLoaded(boolean hasLoaded) { loaded = hasLoaded; }
    
}
