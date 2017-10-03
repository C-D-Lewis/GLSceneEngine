package scene_engine;

public class SceneManager {
    
    private static class State {
        public static final int
                STATE_LOADING = 0,
                STATE_RUNNING = 1;
    }
    
    private static Scene currentScene;
    private static Thread workerThread;
    private static int state = State.STATE_LOADING;
    
    private SceneManager() { }
    
    public static void update() {
        switch(state) {
            case State.STATE_RUNNING: currentScene.onUpdate();  break;
            default: break;
        }
    }

    public static void draw() {
        switch(state) {
            case State.STATE_LOADING: currentScene.onDrawWhileLoading();  break;
            case State.STATE_RUNNING: currentScene.onDraw(); break;
            default:
                Logger.log(SceneManager.class, "Unknown draw state", Logger.WARN, true);
                break;
        }
    }

    public static void setScene(final Scene newScene) {
        if(workerThread != null) {
            workerThread.interrupt();
            Logger.log(SceneManager.class, "Stopped scene " + currentScene, Logger.INFO, true);
        }
        
        if(currentScene != null) currentScene.onPause();
        currentScene = newScene;
        loadCurrentScene();
    }

    private static void loadCurrentScene() {
        state = State.STATE_LOADING;
        
        // Load stuff on background thread, allow moving loading screens
        workerThread = new Thread(() -> {
            try {
                //Load stuff in background, but only once per object
                if(!currentScene.hasLoaded()) {
                    Logger.log(SceneManager.class, "Loading scene " + currentScene, Logger.INFO, true);
                    currentScene.onLoad();
                    currentScene.onResume();
                    currentScene.setLoaded(true);
                } else {
                    Logger.log(SceneManager.class, "Scene " + currentScene + " already loaded. Skipping onLoadForView()...", Logger.INFO, true);
                    currentScene.onResume();
                }

                state = State.STATE_RUNNING;
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        
        workerThread.start();
    }

}
