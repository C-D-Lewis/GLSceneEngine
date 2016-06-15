package gl_scene_engine;

public abstract class EngineCallbacks {
	
	public abstract void onFirstLoad();
	
	public abstract void onLoadResources();
	
	public abstract Scene getInitialGameScene();
	
	public abstract void onUpdate();
	
	public abstract void onDraw();
	
	public abstract void onSecondThreadFrame();
	
	public abstract void onWindowClose();

}
