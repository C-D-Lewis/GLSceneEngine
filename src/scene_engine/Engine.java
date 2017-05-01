package scene_engine;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import scene_engine.FontRenderer.Align;

public abstract class Engine {

    public static class Events {

        public static final String EVENT_FIRST_GL_FRAME = Engine.class.getName() + "FIRST_GL_FRAME";

    }

    private class Config {

        private static final boolean DRAW_DEBUG_OVERLAY = true;

        private static final int FRAME_RATE = 60;

    }

	private static final int SWAP_INTERVAL = 60 / Config.FRAME_RATE;
	
	private static Thread secondThread;
	private static String windowName;
	private static Rectangle windowRect, fpsBounds;
	private static EngineCallbacks callbacks;
	
	private static int updateMemoryMBytes, fpsAverage, updateFps;
	private static long fpsNow, fpsLast, lastFramePeriod, glWindow;
	private static boolean fullscreen;

    private Engine() { }
	
	public static void start(String name, Rectangle rect, EngineCallbacks cbs) {
		windowName = name;
		windowRect = rect;
		callbacks = cbs;
		fpsBounds = new Rectangle(5, 5, 300, 20);

		ConfigManager.load();
		fullscreen = ConfigManager.getBoolean(ConfigManager.DB_KEY_FULLSCREEN, true);

		KeyboardManager.setEnabled(true);
		
		// Blank until OpenGL init
		SceneManager.setScene(new Scene() {

			@Override
			public void onLoad() { }
			public void onUpdate() { }
			public void onDraw() { }

		});
		
		startOpenGLLoop();

		//Load user stuff
		callbacks.onFirstLoad();
	}

    // ---------------------------------- Graphics ---------------------------------
	
    private static GLFWErrorCallback errorCallback;
    private static GLFWKeyCallback keyCallback;
    private static GLFWMouseButtonCallback mouseCallback;
    private static GLFWCursorPosCallback posCallback;
    
    private static void lwjglInit() {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if(GLFW.glfwInit() != GLFW.GLFW_TRUE) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
 
        GLFW.glfwDefaultWindowHints(); 
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
 
        // Create the window
        long fs = fullscreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL;
        glWindow = GLFW.glfwCreateWindow(windowRect.width, windowRect.height, windowName, fs, MemoryUtil.NULL);
        if(glWindow == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
 
        GLFW.glfwSetKeyCallback(glWindow, keyCallback = new GLFWKeyCallback() {
            
        	@Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                KeyboardManager.dispatchKeyEvent(key, action);
            }
        	
        });
        GLFW.glfwSetMouseButtonCallback(glWindow, mouseCallback = new GLFWMouseButtonCallback() {
			
			@Override
			public void invoke(long window, int glfwButton, int action, int mods) {
				boolean pressed = (action == GLFW.GLFW_PRESS);
				MouseManager.dispatchMouseButtonEvent(glfwButton, pressed);
			}
			
		});
        GLFW.glfwSetCursorPosCallback(glWindow, posCallback = new GLFWCursorPosCallback() {
			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				Point pos = new Point((int)xpos, (int)ypos);
				MouseManager.dispatchMousePositionEvent(pos);
			}
			
		});
 
        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if(!ConfigManager.getBoolean(ConfigManager.DB_KEY_FULLSCREEN, true)) {
	        GLFW.glfwSetWindowPos(glWindow,
	            (vidmode.width() - windowRect.width) / 2,
	            (vidmode.height() - windowRect.height) / 2);
        }
 
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
	    GL11.glLoadIdentity();
	    
	    // 2D pixel perfect projection!
	    GL11.glOrtho(0.0f, windowRect.width, windowRect.height, 0.0f, 0.0f, 1.0f);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        EventBus.broadcast(Events.EVENT_FIRST_GL_FRAME, new EventParams());
        callbacks.onLoadResources();
        SceneManager.setScene(callbacks.getInitialScene());
 
        while(GLFW.glfwWindowShouldClose(glWindow) == GLFW.GLFW_FALSE) {
        	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            draw();
            GLFW.glfwSwapBuffers(glWindow); // swap the color buffers
            GLFW.glfwPollEvents();
            
            update();
        }
        
        callbacks.onWindowClose();
    }
	
	private static void startOpenGLLoop() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
		            lwjglInit();
		            lwjglLoop();
		 
		            GLFW.glfwDestroyWindow(glWindow);
		            keyCallback.release();
		            mouseCallback.release();
		            posCallback.release();
		        } finally {
		            GLFW.glfwTerminate();
		            errorCallback.release();
		        }
			}
			
		}).start();
	}

    // ---------------------------------- Engine -----------------------------------

	// Highest level updating
	public static void update() {
		callbacks.onUpdate();
	}
	
	// Highest level drawing
	public static void draw() {
		long frameStart = System.nanoTime();
		callbacks.onDraw();
		lastFramePeriod = System.nanoTime() - frameStart;
		if(Config.DRAW_DEBUG_OVERLAY) {
			drawDebugOverlay();
		}
		countFPS();
	}
	
	private static void drawDebugOverlay() {
		String string = "" + updateFps + " FPS " + lastFramePeriod/1000000 + " ms " + updateMemoryMBytes + " MB";
		
		GLHelpers.pushColor(Color.WHITE);
		FontRenderer.drawString(string, fpsBounds, 8, Align.LEFT, Align.TOP);
	}
	
	public void printFPS() {
		Logger.log(Engine.class, "FPS: " + updateFps, Logger.DEBUG, false);
	}

	private static void countFPS() {
		fpsNow = System.nanoTime() / 1000000;
		if((fpsNow - fpsLast) > 1000) {
			fpsLast = System.nanoTime() / 1000000;
			updateFps = fpsAverage;
			updateMemoryMBytes = Math.round((float)getMemoryUsage());
			fpsAverage = 0;
		} else {
			fpsAverage++;
		}
	}
	
	private static double getMemoryUsage() {
		java.lang.Runtime r = java.lang.Runtime.getRuntime();
		return ((double)((double)(r.totalMemory()/1024)/1024)) - ((double)((double)(r.freeMemory()/1024)/1024));
	}
	
	public static long getGlWindow() {
		return glWindow;
	}
	
	public static void stop() {
		callbacks.onWindowClose();
	}
	
}
