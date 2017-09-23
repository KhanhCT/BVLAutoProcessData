package vn.com.daisy;

import java.io.File;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import vn.com.daisy.DAO.SysvarDAO;

public class Log {
	private Logger logger;

	public Log(String className) {
		String workingDir = null;
		SysvarDAO sysDAO = new SysvarDAO();
		workingDir = sysDAO.getValue(ConfigTag.LOGFILE_URL);
		if (workingDir == null) {
			workingDir = System.getProperty("user.dir");
			File file = new File(workingDir + "/log4j.properties");
			if (file.exists()) {
				PropertyConfigurator.configure(workingDir + "/log4j.properties");
			} else {
				// Common.print("Log()", "Cannot find log4j.properties. Cannot
				// Write log file!!!");
			}
		}
		logger = Logger.getLogger(className);
	}

	public void writeLogInfo(String header, String payload) {
		logger.info("[" + header + "]: " + payload);
	}

	public void writeLogDebug(String header, String payload) {
		logger.debug("[" + header + "]: " + payload);
	}

	public void writeLogError(String header, String payload) {
		logger.error("[" + header + "]: " + payload);

	}

	public void writeLogWarning(String header, String payload) {
		logger.warn("[" + header + "]: " + payload);

	}

	public void writeLogFatal(String header, String payload) {
		logger.fatal("[" + header + "]: " + payload);

	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

}
