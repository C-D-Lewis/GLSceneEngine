package scene_engine;

import java.awt.*;

public class SceneManager implements Loopable {
    
    private Scene currentScene;
    private Thread workerThread;
    private int state = State.STATE_LOADING;
    
    @Override
    public void update() {
        switch(state) {
            case State.STATE_RUNNING: currentScene.update();  break;
            default: break;
        }
    }

    @Override
    public void draw() {
        switch(state) {
            case State.STATE_LOADING: currentScene.drawWhileLoading();  break;
            case State.STATE_RUNNING: currentScene.draw(); break;
            default:
                Logger.log(SceneManager.class, "Unknown draw state", Logger.WARN, true);
                break;
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        switch(state) {
            case State.STATE_LOADING: currentScene.drawWhileLoading(g2d);  break;
            case State.STATE_RUNNING: currentScene.draw(g2d); break;
            default:
                Logger.log(SceneManager.class, "Unknown draw state", Logger.WARN, true);
                break;
        }
    }

    public void setScene(final Scene newScene) {
        if(workerThread != null) {
            workerThread.interrupt();
            Logger.log(SceneManager.class, "Stopped scene " + currentScene, Logger.INFO, true);
        }
        
        if(currentScene != null) currentScene.onPause();
        currentScene = newScene;
        loadCurrentScene();
    }

    private void loadCurrentScene() {
        state = State.STATE_LOADING;
        
        workerThread = new Thread(() -> {
            try {
                // Load stuff in background, allow moving loading screens
                if(currentScene.hasLoaded()) {
                    Logger.log(SceneManager.class, "Scene " + currentScene + " already loaded. Skipping onLoadForView()...", Logger.INFO, true);
                    currentScene.onResume();
                    return;
                }

                Logger.log(SceneManager.class, "Loading scene " + currentScene, Logger.INFO, true);
                currentScene.onLoad();
                currentScene.onResume();
                currentScene.setLoaded(true);

                state = State.STATE_RUNNING;
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        
        workerThread.start();
    }

    private static class State {
        public static final int
                STATE_LOADING = 0,
                STATE_RUNNING = 1;
    }

}
