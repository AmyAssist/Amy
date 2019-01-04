package io.github.amyassist.amy.logger;

import org.slf4j.LoggerFactory;

import asg.cliche.Command;
import asg.cliche.Param;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class LoggerConsole {
	/**
	 * Set the log level
	 */
	@Command(abbrev = "log", description = "Set the log level of the given logger to the given level. This settings are not persisted.")
	public void setLogLevel(@Param(name = "logger") String loggerName, @Param(name = "level") String level) {
		Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
		logger.setLevel(Level.toLevel(level));
	}

	/**
	 * Set the log level of the root logger
	 */
	@Command(abbrev = "log", description = "Set the log level of the root logger to the given level. This settings are not persisted.")
	public void setLogLevel(@Param(name = "level") String level) {
		this.setLogLevel(Logger.ROOT_LOGGER_NAME, level);
	}
}
