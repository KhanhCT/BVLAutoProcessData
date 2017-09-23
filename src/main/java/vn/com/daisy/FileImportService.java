package vn.com.daisy;

import java.util.Date;
import java.util.Timer;

import vn.com.daisy.DAO.SysvarDAO;

public class FileImportService {

	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		SysvarDAO sysDAO = new SysvarDAO();
		Log log = new Log("FileImportService");
		long timePoll = 28800000;
		Timer time = new Timer(); // Instantiate Timer Object
		ScheduledDownloadTask st = new ScheduledDownloadTask(); // Instantiate
																// SheduledTas
		Date alarm = new Date();
		try {
			Common.print("FileImportService()","Starting.....");
			timePoll = Long.parseLong(sysDAO.getValue(ConfigTag.TIME_SCHEDULER));

		} catch (Exception ex) {
			if (Common.DEBUG)
				log.writeLogInfo("FileImportService", "Can not get time poll value.Time poll = 28800000(Default=8h)");
			// System.exit(0);
		}
		time.schedule(st, alarm, timePoll);
	}

}
