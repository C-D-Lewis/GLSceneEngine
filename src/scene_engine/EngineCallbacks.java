package scene_engine;

public abstract class EngineCallbacks {
	
	public abstract void onFirstLoad();
	
	public abstract void onLoadResources();
	
	public abstract Scene getInitialScene();
	
	public abstract void onUpdate();
	
	public abstract void onDraw();
	
	public abstract void onBackgroundFrame();
	
	public abstract void onWindowClose();

}
