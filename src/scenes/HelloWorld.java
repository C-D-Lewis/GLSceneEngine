package scenes;

import core.BuildConfig;
import entities.TexturedBox;
import org.lwjgl.glfw.GLFW;
import scene_engine.*;
import scene_engine.FontRenderer.Align;

import java.awt.*;

public class HelloWorld extends Scene {

    private TexturedBox box;

    public HelloWorld() {
        box = new TexturedBox(new Rectangle(0, 0, 100, 100), "./res/entity/box.png");
    }
    
    @Override
    public void onLoad() {
        EventBus.register(new EventBus.Receiver(KeyboardManager.Events.KEY_CHANGE, false) {
            @Override
            public void onEvent(EventBus.Params params) {
                int glfwKey = params.getInteger(KeyboardManager.Events.PARAM_KEY);
                boolean pressed = params.getBoolean(KeyboardManager.Events.PARAM_STATE);
                Logger.log(HelloWorld.class, "Key " + glfwKey + " pressed " + pressed, Logger.INFO, false);

                if(glfwKey == GLFW.GLFW_KEY_ESCAPE && !pressed) Engine.stop();
            }
        });
        EventBus.register(new EventBus.Receiver(MouseManager.Events.BUTTON_STATE_CHANGED, false) {
            @Override
            public void onEvent(EventBus.Params params) {
                boolean pressed = params.getBoolean(MouseManager.Events.PARAM_PRESSED);
                String str = "Mouse " + (pressed ? "clicked!" : "released!");
                Logger.log(HelloWorld.class, str, Logger.INFO, false);
            }
        });
        EventBus.register(new EventBus.Receiver(MouseManager.Events.POSITION_CHANGED, false) {
            @Override
            public void onEvent(EventBus.Params params) {
                Point pos = params.getPoint(MouseManager.Events.PARAM_POSITION);
                Logger.log(HelloWorld.class, "Mouse now at: " + pos, Logger.INFO, false);
            }
        });
        EventBus.register(new EventBus.Receiver(MouseManager.Events.MOUSE_SCROLL_CHANGED, false) {
            @Override
            public void onEvent(EventBus.Params params) {
                int dir = params.getInteger(MouseManager.Events.PARAM_SCROLL_DIRECTION);
                String direction = null;
                switch(dir) {
                    case MouseManager.ScrollDirection.UP: direction = "UP"; break;
                    case MouseManager.ScrollDirection.DOWN: direction = "DOWN"; break;
                }
                Logger.log(HelloWorld.class, "Mouse scrolled " + direction, Logger.INFO, false);
            }
        });
        Logger.log(HelloWorld.class, "Registered EventBus handlers", Logger.INFO, false);
    }

    @Override
    public void update() {
        box.update();
    }

    @Override
    public void draw() {
        GLHelpers.pushNewColor(Color.WHITE);
        FontRenderer.drawString("Hello world!", BuildConfig.SCREEN_RECT, 16, Align.CENTER, Align.CENTER);

        box.draw();
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.drawString("Hello world!", BuildConfig.SCREEN_RECT.width / 2, BuildConfig.SCREEN_RECT.height / 2);

        box.draw(g2d);
    }

}
