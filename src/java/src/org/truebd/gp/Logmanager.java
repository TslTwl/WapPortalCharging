/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.org.truebd.gp;

import java.io.IOException;
import java.util.Enumeration;

import org.apache.log4j.*;

/**
 * 
 * @author True
 */
public class Logmanager {
    /*
     * TRACE, DEBUG, INFO, WARN, ERROR and FATAL
     */

    public static Logger logger = Logger.getLogger(Logmanager.class);
    private static Appender fileAppender;

    public static void setLoggerLevel(String loggerName, String logLevel) {
        logger.debug("logger=" + loggerName + " setting to " + logLevel);
        Level level;
        if (logLevel.equals("NULL")) {
            level = null;
            logger.debug("logger will be set to null");
        } else {
            level = Level.toLevel(logLevel);
        }
        logger.trace("setting level to=" + level);

        if (loggerName.equals("RootLogger")) {
            if (level != null) {
                Logger.getRootLogger().setLevel(level);
            }
        } else {
            Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
            while (loggers.hasMoreElements()) {
                Logger thislogger = loggers.nextElement();
                if (thislogger.getName().contains(loggerName)) {
                    thislogger.setLevel(level);

                }

            }
        }

    }

    public static String getAllLoggers() {
        Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
        String str = "RootLogger-->" + Logger.getRootLogger().getLevel() + "<BR>";
        while (loggers.hasMoreElements()) {
            Logger thislogger = loggers.nextElement();
            str = str + thislogger.getName() + "-->" + thislogger.getLevel() + "<BR>";

        }
        return str;
    }

    public static void intLogger() {
        Logger rootLogger = Logger.getLogger("src.org.truebd.gp");
        Layout layout = new PatternLayout("%d{dd MMM yyyy HH:mm:ss,SSS};%p;%t;%l;%m%n");
        try {
            String logLocation = "../logs/charging.log";
            //logLocation=ConfigLoader.getParamAsString("Log", "logLocation", logLocation);
            rootLogger.removeAllAppenders();
            RollingFileAppender rollFileApp = new RollingFileAppender(layout, logLocation, true);
            rollFileApp.setImmediateFlush(true);
            int maxLogFileCount = 5;//ConfigLoader.getParamAsInt("Log", "maxLogFileCount", 5);
            rollFileApp.setMaxBackupIndex(maxLogFileCount);
            String maxFileSize = "1GB";//ConfigLoader.getParamAsString("Log", "maxFileSize", "1GB");
            rollFileApp.setMaxFileSize(maxFileSize);
            fileAppender = rollFileApp;

        } catch (IOException e) {
            logger.fatal(e);
            e.printStackTrace();
        }
        // PropertyConfigurator.configureAndWatch("log4j.properties");

        fileAppender.setName("Main Appender");
        rootLogger.addAppender(fileAppender);
        rootLogger.info("logger loaded");
        String defLevel = "ALL";//ConfigLoader.getParamAsString("Log", "defaultLogLevel", "INFO");
        rootLogger.setLevel(Level.toLevel(defLevel));
    }

    public static void shutdown() {
        Logger rootLogger = Logger.getRootLogger();
        rootLogger.info("shutting down Loggers");
        if (fileAppender != null) {
            fileAppender.close();
        }
    }
}
