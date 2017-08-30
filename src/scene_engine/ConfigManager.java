package scene_engine;

public class ConfigManager {
    
    public static final String DB_KEY_FULLSCREEN = "fullscreen";
    
    private static INIParser db;
    
    private ConfigManager() { }
    
    public static void load() {
        db = new INIParser("./config.ini");
        if(db.get(DB_KEY_FULLSCREEN, false) == null) db.put(DB_KEY_FULLSCREEN, Boolean.toString(false));
    }
    
    public static String getString(String key, boolean required) { return db.get(key, required); }
    
    public static boolean getBoolean(String key, boolean required) { return Boolean.parseBoolean(getString(key, required)); }
    
}
