package scene_engine;

public abstract class EventReceiver {
    
    private String tag;
    private boolean oneShot, fired;

    public abstract void onEvent(EventParams params);

    public EventReceiver(String tag, boolean oneShot) {
        this.tag = tag;
        this.oneShot = oneShot;
    }
    
    public void trigger(EventParams params) {
        if(oneShot && !fired) { 
            fired = true;
            onEvent(params);
        } else onEvent(params);
    }
    
    public String getTag() { return tag; }

}
