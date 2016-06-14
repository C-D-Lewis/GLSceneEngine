package core;

import java.awt.Rectangle;

/**
 * Values set at compile-time
 */
public class BuildConfig {
	
	public static final boolean RELEASE_BUILD = false;
	
	public static final String
		GAME_NAME = "Base Scene Engine",
		VERSION_STRING = "0.0.1";	// Stable, Feature, Patch
	public static final int RELEASE_NUMBER = 1;
	
	public static final Rectangle
		SCREEN_RECT = new Rectangle(0, 0, 1280, 720);
	
	public static boolean
		DEBUG = true,
		DRAW_DEBUG_OVERLAY = true,
		DRAW_TEXT_BOUNDS = false,
		LOG_ALL_EVENTS_FIRED = true;
	
	public static final String DEBUG_LOG_PATH = "./debug-log.log";
	
}
