package core;

import scene_engine.*;
import scenes.HelloWorld;

import java.util.Objects;

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
        Engine.start(title, BuildConfig.SCREEN_RECT, new Engine.Callbacks() {
            @Override
            public void onStartComplete() {
                if(Engine.getRenderMode().equals(Engine.RenderMode.OPEN_GL)) FontRenderer.setFont(new Blocky());
            }

            @Override
            public Scene getInitialScene() { return new HelloWorld(); }
        });
    }
    
}
