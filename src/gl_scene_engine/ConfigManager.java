package gl_scene_engine;


/**
 * Very thin FileDB wrapper that includes config keys the game will listen for
 */
public class ConfigManager {
	
	// Key list
	public static final String
		DB_KEY_FULLSCREEN = "fullscreen";
	
	private static INIParser db;
	
	private ConfigManager() { }
	
	public static void load() {
		db = new INIParser(Resources.FilePaths.CONFIG);
		if(db.get(DB_KEY_FULLSCREEN, false) == null) {
			// Default settings
			db.put(DB_KEY_FULLSCREEN, Boolean.toString(false));
		}
	}
	
	public static String getString(String key, boolean requried) {
		return db.get(key, requried);
	}
	
	public static boolean getBoolean(String key, boolean required) {
		return Boolean.parseBoolean(getString(key, required));
	}
	
}
