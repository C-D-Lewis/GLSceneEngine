package core;

import scene_engine.Blocky;
import scene_engine.Engine;
import scene_engine.EngineCallbacks;
import scene_engine.FontRenderer;
import scene_engine.Logger;
import scene_engine.Scene;
import scene_engine.SceneManager;
import scenes.HelloWorld;

public class SEDemoMain {
    
    public static void main(String[] args) {
        init();
    }
    
    private static void init() {
        Logger.setLogPath("./debug-log.log");
        setupEngine();
    }

    private static void setupEngine() {
        String title = BuildConfig.GAME_NAME + " v" + BuildConfig.VERSION_STRING;
        Engine.start(title, BuildConfig.SCREEN_RECT, new EngineCallbacks() {
            @Override
            public void onStartComplete() {
                FontRenderer.setFont(new Blocky());
            }
            
            @Override
            public Scene getInitialScene() { return new HelloWorld(); }
            
            @Override
            public void onWindowClose() {
                System.exit(0);
            }
        });
    }
    
}
