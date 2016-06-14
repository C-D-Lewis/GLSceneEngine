package core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Allows logging to console and log file simultaneously
 */
public class Logger {

	public static final String 
		INFO = "[I]",
		WARN = "[W]",
		DEBUG = "[D]",
		ERROR = "[E]";
	
	private static String logPath = "./NO_LOG_PATH_SET.log";
	
	public static void setLogPath(String path) {
		logPath = path;
	}
	
	/**
	 * Log info about game
	 * @param toFile Note: Only non-per-frame entries should be logged to file.
	 * @return
	 */
	public static boolean log(Class<?> clz, String message, String level, boolean toFile) {
		try {	
			String TAG = clz.getName();
			
			// Debug indenting
			if(!level.equals(INFO)) {
				level = "    " + level;
			}
			
			// File?
			if(toFile) {
				File f = new File(logPath);
				FileWriter fw = new FileWriter(f, true);
				fw.write(level + " " + TAG + ": " + message + "\n");
				fw.flush();
				fw.close();
			}
			
			if(BuildConfig.RELEASE_BUILD) {
				// Keep the console clean for consumers
				return true;
			}

			// Console unless release
			String content = level + " " + TAG + ": " + message;
			if(level.equals(ERROR)) {
				System.err.println(content);
			} else {
				System.out.println(content);
			}
			
			return true;
		} catch (Exception e) {
			System.err.println("ERROR LOGGING: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public static void logStackTrace(Exception e) {
		PrintWriter pw;
		try {
			// To file
			pw = new PrintWriter(new FileOutputStream(logPath, true));
			e.printStackTrace(pw);
			pw.flush();
			pw.close();
			
			// To console
			e.printStackTrace();
		} catch (Exception e1) {
			System.out.println("Logging stack trace failed!");
			e1.printStackTrace();
		}
	}
	
	public static void assertOrCrash(boolean condition, String description) {
		if(!condition) {
			log(Logger.class, "ASSERTION FAILED:", Logger.ERROR, true);
			log(Logger.class, description, Logger.ERROR, true);
			System.exit(1);
		}
	}

}
