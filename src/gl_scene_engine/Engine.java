package gl_scene_engine;

import gl_scene_engine.FontRenderer.Align;

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

/**
 * Abstract instantiated single-threaded JOGL OpenGL engine with lifecycle-based scenes
 * @author Chris Lewis
 */
public abstract class Engine {
	public static final int
		CHROME_WIDTH = 5,
		CHROME_HEIGHT = 28;
	
	private static final String
		ENGINE_NAME = "SceneEngine",
		ENGINE_VERSION = "0.1.0";	// Major, feature, patch
	private static final int 
		FRAME_RATE = 60,
		FPS_PERIOD = 1000 / FRAME_RATE,
		SWAP_INTERVAL = 60 / FRAME_RATE;
	
	private static Thread secondThread;
	private static String windowName;
	private static Rectangle windowRect, fpsBounds;
	private static EngineCallbacks callbacks;
	
	private static int updateMemoryMBytes, fpsAverage, updateFps;
	private static long fpsNow, fpsLast, lastFramePeriod, glWindow;
	private static boolean fullscreen, seenFirstFrame;
	
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
			public int getSceneId() { return 0; }

			@Override
			public void onLoad() { }
			public void onUpdate() { }
			public void onDraw() { }
			
		});
		
		//Setup graphics, must happen in this order
		startOpenGLLoop();
		waitForOpenGLInit();
		
		//Load user stuff
		callbacks.onFirstLoad();
		
		begin();
		Logger.log(Engine.class, ENGINE_NAME + " version " + ENGINE_VERSION + " started.", Logger.INFO, true);
	}
	
	/*
	 * GRAPHICS METHODS
	 *********************************************************************************************
	 */
	
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
 
        // Keyboard input
        GLFW.glfwSetKeyCallback(glWindow, keyCallback = new GLFWKeyCallback() {
            
        	@Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                KeyboardManager.dispatchKeyEvent(key, action);
            }
        	
        });
        
        // Mouse input
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
 
        // Video mode
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
 
        while(GLFW.glfwWindowShouldClose(glWindow) == GLFW.GLFW_FALSE) {
        	GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            if(!seenFirstFrame) {
    			seenFirstFrame = true;
    			EventBus.broadcast(Events.EVENT_FIRST_GL_FRAME, new EventParams());
    		}
            
            draw();
            GLFW.glfwSwapBuffers(glWindow); // swap the color buffers
            GLFW.glfwPollEvents();
            
            update();
        }
        
        // Window has closed
        callbacks.onWindowClose();
    }
	
	/**
	 * Setup JOGL OpenGL bindings
	 * Note: Ignores frame rate setting
	 */
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
	
	private static void waitForOpenGLInit() {
		EventBus.register(new EventReceiver(Engine.Events.EVENT_FIRST_GL_FRAME, true) {
			
			@Override
			public void onReceive(EventParams params) {
				callbacks.onLoadResources();
				SceneManager.setScene(callbacks.getInitialGameScene());
			}
			
		});
	}

	/*
	 * ENGINE METHODS
	 **********************************************************************************************
	 */
	
	/**
	 * Start execution
	 */
	private static void begin() {
		// Second thread for non-renderable calculations
		secondThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						// Proceed at frame rate, but not neccessarily in sync
						Thread.sleep(FPS_PERIOD);
						callbacks.onSecondThreadFrame();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		});
		secondThread.start();
	}
	
	/**
	 * Highest level updating
	 * Public for calling from MasterScene
	 */
	public static void update() {
		callbacks.onUpdate();
	}
	
	/**
	 * Highest level drawing
	 * @param gl2
	 */
	public static void draw() {
		callbacks.onDraw();
		if(Config.DRAW_DEBUG_OVERLAY) {
			drawDebugOverlay();
		}
		
		countFPS();
	}
	
	/**
	 * Load all resources here, once texture uploading is available
	 */
	public static void onLoadResources() { }

	/**
	 * Debugging statistical overlay
	 */
	private static void drawDebugOverlay() {
		String string = "" + updateFps + " FPS " + lastFramePeriod/1000000 + " ms " + updateMemoryMBytes + " MB";
		
		GLHelpers.setColorFromColor(Color.WHITE);
		FontRenderer.drawString(string, fpsBounds, 8, Align.LEFT, Align.TOP);
	}
	
	public void printFPS() {
		Logger.log(Engine.class, "FPS: " + updateFps, Logger.DEBUG, false);
	}

	/**
	 * Count frame rate
	 */
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
	
	public static void onWindowClose() {
		callbacks.onWindowClose();
	}
	
	public static void stop() {
		onWindowClose();
	}
	
	public static class Events {
		
		public static final String 
			EVENT_FIRST_GL_FRAME = Engine.class.getName() + "FIRST_GL_FRAME";
		
	}
	
	private class Config {
		
		private static final boolean
			DRAW_DEBUG_OVERLAY = true;
		
	}
	
}
