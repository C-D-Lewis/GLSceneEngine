package scene_engine;

public abstract class EngineCallbacks {
    
    public abstract void onStartComplete();
    public abstract Scene getInitialScene();
    public abstract void onWindowClose();

}
