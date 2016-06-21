package scene_engine;


/**
 * Manages the current Scene's lifecycle and iteration
 */
public class SceneManager {
	
	private static final int
		STATE_LOADING = 0,
		STATE_RUNNING = 1;
	
	private static Scene currentScene;
	private static Thread workerThread;
	private static int state = STATE_LOADING;
	
	private SceneManager() { }
	
	public static void onUpdate() {
		switch(state) {
		case STATE_RUNNING:
			currentScene.onUpdate();
			break;
		}
	}

	public static void onDraw() {
		switch(state) {
		case STATE_LOADING:
			currentScene.onDrawWhileLoading();
			break;
		case STATE_RUNNING:
			currentScene.onDraw();
			break;
		default:
			Logger.log(SceneManager.class, "Unknown draw state", Logger.WARN, true);
			break;
		}
	}

	/**
	 * Set a new Stage to manage
	 */
	public static void setScene(Scene newScene) {
		if(workerThread != null) {
			workerThread.interrupt();
			Logger.log(SceneManager.class, "Stopped scene " + currentScene, Logger.INFO, true);
		}
		
		if(currentScene != null) {
			currentScene.onPause();
		}
		
		currentScene = newScene;
		loadCurrentScene();
	}

	/**
	 * Start new worker thread for loading
	 */
	private static void loadCurrentScene() {
		state = STATE_LOADING;
		
		//Load stuff on background thread
		workerThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					//Load stuff in background, but only once per object
					if(!currentScene.getHasLoaded()) { 
						Logger.log(SceneManager.class, "Loading scene " + currentScene, Logger.INFO, true);
						
						currentScene.onLoad();
						currentScene.onResume();
						currentScene.setHasLoaded(true);
					} else {
						Logger.log(SceneManager.class, "Scene " + currentScene + " already loaded. Skipping onLoadForView()...", Logger.INFO, true);
						currentScene.onResume();
					}
					
					//All done!
					state = STATE_RUNNING;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
		workerThread.start();
	}

	public static void onSecondThreadFrame() {
		switch(state) {
		case STATE_RUNNING:
			currentScene.onSecondThreadFrame();
			break;
		default:
			break;
		}
	}

	public static Scene getCurrentScene() {
		return currentScene;
	}

	public void setCurrentScene(Scene scene) {
		currentScene = scene;
	}
	
}
