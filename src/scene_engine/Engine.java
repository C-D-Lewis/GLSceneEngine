package scene_engine;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import scene_engine.FontRenderer.Align;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Engine {

    private static final Rectangle FPS_BOUNDS = new Rectangle(5, 5, 300, 20);
    private static final boolean DRAW_DEBUG_OVERLAY = true;
    private static final int
            FRAME_RATE = 60,
            SWAP_INTERVAL = 60 / FRAME_RATE;

    private static String windowName, renderMode;
    private static Rectangle windowRect;
    private static Callbacks callbacks;
    private static INIParser configIni;
    private static SceneManager sceneManager = new SceneManager();
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
        if(!configIni.contains(ConfigKeys.FULLSCREEN, false)) {
            configIni.put(ConfigKeys.FULLSCREEN, Boolean.toString(false));
            configIni.put(ConfigKeys.RENDER_MODE, RenderMode.OPEN_GL);
        }
        renderMode = configIni.getString(ConfigKeys.RENDER_MODE, true);

        sceneManager.setScene(new Scene() {
            @Override
            public void onLoad() { }
            public void update() { }
            public void draw() { }
            public void draw(Graphics2D g2d) { }
        });

        switch(renderMode) {
            case RenderMode.OPEN_GL: useOpenGl(); break;
            case RenderMode.JAVA_2D: useJava2D(); break;
            default:
                Logger.log(Engine.class, "Invalid rendermode: " + renderMode, Logger.ERROR, true);
                break;
        }
    }

    private static void useJava2D() {
        JFrame window = new JFrame(windowName);
        window.setPreferredSize(new Dimension(windowRect.width, windowRect.height));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, windowRect.width, windowRect.height);
                draw(g2d);
            }
        };
        window.add(panel);

        // Set mouse, keyboard, cursor position callbacks
        KeyboardManager.useJFrame(window);
        MouseManager.useJFrame(window);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
                panel.repaint();
            }
        }, 0, (1000 / 60));

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.pack();

        EventBus.broadcast(Events.INIT_COMPLETE, new EventBus.Params());
        callbacks.onStartComplete();
        sceneManager.setScene(callbacks.getInitialScene());
    }

    private static void useOpenGl() {
        try {
            lwjglInit();
            EventBus.broadcast(Events.INIT_COMPLETE, new EventBus.Params());
            lwjglLoop();
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
        boolean fullscreen = configIni.getBoolean(ConfigKeys.FULLSCREEN, true);
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
            MouseManager.broadcastMouseScrollEvent(yoffset);
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
        sceneManager.setScene(callbacks.getInitialScene());

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

    private static void update() { sceneManager.update(); }

    private static void draw() {
        long frameStart = System.nanoTime();
        sceneManager.draw();
        lastFramePeriod = System.nanoTime() - frameStart;
        countFPS();
        if(DRAW_DEBUG_OVERLAY) drawDebugOverlay();
    }

    private static void draw(Graphics2D g2d) {
        long frameStart = System.nanoTime();
        sceneManager.draw(g2d);
        lastFramePeriod = System.nanoTime() - frameStart;
        countFPS();
    }

    public static void stop() { System.exit(0); }

    public static String getRenderMode() { return renderMode; }

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
    
    public static void setZoomMultiplier(float multiplier) { zoomMultiplier = multiplier; }

    public abstract static class Callbacks {
        public abstract void onStartComplete();
        public abstract Scene getInitialScene();
    }

    public static class Events {
        public static final String
                INIT_COMPLETE = "INIT_COMPELTE";
    }

    public static class ConfigKeys {
        public static final String
                FULLSCREEN = "fullscreen",
                RENDER_MODE = "rendermode";
    }

    public static class RenderMode {
        public static final String
                OPEN_GL = "opengl",
                JAVA_2D = "java2d";
    }

}
