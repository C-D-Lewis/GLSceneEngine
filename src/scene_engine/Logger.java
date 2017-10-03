package scene_engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger {

    public static final String
        INFO = "[I]",
        WARN = "[W]",
        DEBUG = "[D]",
        ERROR = "[E]";
    
    private static String logPath = "./NO_LOG_PATH_SET.log";
    
    public static void setLogPath(String path) { logPath = path; }
    
    public static boolean log(Class<?> clz, String message, String level, boolean toFile) {
        try {    
            String TAG = clz.getName();
            
            if(!level.equals(INFO)) level = "    " + level;
            if(toFile) {
                File f = new File(logPath);
                FileWriter fw = new FileWriter(f, true);
                fw.write(level + " " + TAG + ": " + message + "\n");
                fw.flush();
                fw.close();
            }
            String content = level + " " + TAG + ": " + message;
            if(level.equals(ERROR)) {
                System.err.println(content);
                return true;
            }

            System.out.println(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void logStackTrace(Exception e) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(logPath, true));
            e.printStackTrace(pw);
            pw.flush();
            pw.close();
            
            e.printStackTrace();
        } catch (Exception e1) {
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
