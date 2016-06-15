package gl_scene_engine;

public abstract class EventReceiver {
	
	private String tag;
	private boolean oneShot, fired;
	
	public EventReceiver(String tag, boolean oneShot) {
		this.tag = tag;
		this.oneShot = oneShot;
	}
	
	public void trigger(EventParams params) {
		if(oneShot && !fired) { 
			fired = true;
			onReceive(params);
		} else {
			onReceive(params);
		}
	}
	
	public String getTag() {
		return tag;
	}

	public abstract void onReceive(EventParams params);
	
}
