package scene_engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import scene_engine.FontRenderer.Align;

import java.awt.*;

public abstract class Engine {

    public static class Events {
        public static final String
                INIT_COMPLETE = "INIT_COMPELTE";
    }

    public static class ConfigKeys {
        public static final String DB_KEY_FULLSCREEN = "fullscreen";
    }

    private static final Rectangle FPS_BOUNDS = new Rectangle(5, 5, 300, 20);
    private static final boolean DRAW_DEBUG_OVERLAY = true;
    private static final int
            FRAME_RATE = 60,
            SWAP_INTERVAL = 60 / FRAME_RATE;

    private static String windowName;
    private static Rectangle windowRect;
    private static Callbacks callbacks;
    private static INIParser configIni;

    private static int updateMemoryMBytes, fpsAverage, updateFps;
    private static long fpsLast, lastFramePeriod, glWindow;
    private static float zoomMultiplier = 1.0f;

    private Engine() { }
    
    public static void start(String name, Rectangle rect, Callbacks cbs) {
        windowName = name;
        windowRect = rect;
        callbacks = cbs;

        // Init config
        configIni = new INIParser("./engine-config.ini");
        if(!configIni.contains(ConfigKeys.DB_KEY_FULLSCREEN, false)) {
            configIni.put(ConfigKeys.DB_KEY_FULLSCREEN, Boolean.toString(false));
        }

        SceneManager.setScene(new Scene() {
            @Override
            public void onLoad() { }
            public void onUpdate() { }
            public void onDraw() { }
        });

        try {
            lwjglInit();

            EventBus.broadcast(Events.INIT_COMPLETE, new EventBus.Params());

            lwjglLoop();

            callbacks.onWindowClose();
            GLFW.glfwDestroyWindow(glWindow);
        } finally {
            GLFW.glfwTerminate();
        }
    }
    
    private static void lwjglInit() {
        GLFW.glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
        if(!GLFW.glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
 
        // Create the window
        boolean fullscreen = configIni.getBoolean(ConfigKeys.DB_KEY_FULLSCREEN, true);
        long fs = fullscreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL;
        glWindow = GLFW.glfwCreateWindow(windowRect.width, windowRect.height, windowName, fs, MemoryUtil.NULL);
        if(glWindow == MemoryUtil.NULL) throw new RuntimeException("Failed to create the GLFW window");

        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if(!fullscreen) {
            GLFW.glfwSetWindowPos(glWindow,
                    (vidmode.width() - windowRect.width) / 2,
                    (vidmode.height() - windowRect.height) / 2);
        }

        GLFW.glfwSetKeyCallback(glWindow, (long window, int key, int scancode, int action, int mods) -> {
            KeyboardManager.broadcastKeyEvent(key, action);
        });
        GLFW.glfwSetMouseButtonCallback(glWindow, (long window, int glfwButton, int action, int mods) -> {
            MouseManager.broadcastButtonEvent(glfwButton, (action == GLFW.GLFW_PRESS));
        });
        GLFW.glfwSetCursorPosCallback(glWindow, (long window, double xpos, double ypos) -> {
            MouseManager.broadcastPositionEvent(new Point((int)xpos, (int)ypos));
        });
        GLFW.glfwSetScrollCallback(glWindow, (long window, double xoffset, double yoffset) -> {
            MouseManager.dispatchMouseScrollEvent(yoffset);
        });
 
        GLFW.glfwMakeContextCurrent(glWindow);
        GLFW.glfwSwapInterval(SWAP_INTERVAL);
        GLFW.glfwShowWindow(glWindow);
    }

    private static void lwjglLoop() {
        GL.createCapabilities();
        Logger.log(Engine.class, "OpenGL version " + GL11.glGetString(GL11.GL_VERSION), Logger.INFO, true);
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glViewport(0, 0, windowRect.width, windowRect.height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);

        callbacks.onStartComplete();
        SceneManager.setScene(callbacks.getInitialScene());

        while (!GLFW.glfwWindowShouldClose(glWindow)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0f, windowRect.width, windowRect.height, 0.0f, 0.0f, 1.0f);  // 2D pixel perfect projection!
            GL11.glScalef(zoomMultiplier, zoomMultiplier, 1.0f);

            draw();
            GLFW.glfwSwapBuffers(glWindow);
            GLFW.glfwPollEvents();
            update();
        }
    }

    private static void update() { SceneManager.update(); }
    
    private static void draw() {
        long frameStart = System.nanoTime();
        SceneManager.draw();
        lastFramePeriod = System.nanoTime() - frameStart;
        countFPS();
        if(DRAW_DEBUG_OVERLAY) drawDebugOverlay();
    }
    
    private static void drawDebugOverlay() {
        String string = updateFps + " FPS " + (lastFramePeriod / 1000000) + " ms " + updateMemoryMBytes + " MB";
        GLHelpers.pushNewColor(Color.WHITE);
        FontRenderer.drawString(string, FPS_BOUNDS, 8, Align.LEFT, Align.TOP);
    }
    
    private static void countFPS() {
        long now = System.nanoTime() / 1000000;
        if((now - fpsLast) > 1000) {
            fpsLast = System.nanoTime() / 1000000;
            updateFps = fpsAverage;
            updateMemoryMBytes = Math.round((float)getMemoryUsage());
            fpsAverage = 0;
        } else fpsAverage++;
    }
    
    private static double getMemoryUsage() {
        java.lang.Runtime r = java.lang.Runtime.getRuntime();
        return ((double)(r.totalMemory() / 1024) / 1024) -
               ((double)(r.freeMemory() / 1024) / 1024);
    }
    
    public static void stop() { callbacks.onWindowClose(); }

    public static void setZoomMultiplier(float multiplier) { zoomMultiplier = multiplier; }

    public abstract static class Callbacks {

        public abstract void onStartComplete();
        public abstract Scene getInitialScene();
        public abstract void onWindowClose();

    }

}
