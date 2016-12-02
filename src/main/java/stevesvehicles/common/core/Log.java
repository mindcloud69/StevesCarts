package stevesvehicles.common.core;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class Log {

	public static Logger logger;

	public static void trace(String message, Object... params) {
		log(Level.TRACE, message, params);
	}

	public static void debug(String message, Object... params) {
		log(Level.DEBUG, message, params);
	}

	public static void info(String message, Object... params) {
		log(Level.INFO, message, params);
	}

	public static void warn(String message, Object... params) {
		log(Level.WARN, message, params);
	}

	public static void err(String message, Object... params) {
		log(Level.ERROR, message, params);
	}

	private static void log(Level logLevel, String message, Object... params) {
		logger.log(logLevel, message, params);
	}

}