package gl_scene_engine;


/**
 * Class that maintains all resources from disk
 */
public class Resources {
	
	public static class FontSheets {
		
		public static final String
			BLOCKY = "./res/fonts/blocky.png";
		
	}
	
	public static class FilePaths {
		
		public static final String 
			CONFIG = "./config.ini";
		
	}
	
	public static void initWithGL() {
		FontRenderer.loadFontFile(Resources.FontSheets.BLOCKY);
	}
	
}
