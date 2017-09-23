package vn.com.daisy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import vn.com.daisy.DAO.SysvarDAO;

public class FileExportService {
	
	@SuppressWarnings("deprecation")
	public static void main(String args[]) throws InterruptedException {
		System.setProperty("file.encoding", "UTF-8");
		Log log = new Log("FileImportService");
		SysvarDAO sysDAO = new SysvarDAO();
		long timePoll = 28800000;
		String timeExport = null;
		Timer time = new Timer(); // Instantiate Timer Object
		ScheduledUploadTask st = new ScheduledUploadTask(); // Instantiate
															// SheduledTas
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = null;
		Date alarm = new Date();
		try {
			timePoll = Long.parseLong(sysDAO.getValue(ConfigTag.TIME_SCHEDULER));
			timeExport = sysDAO.getValue(ConfigTag.EXPORT_DATE);
			date = dateFormat.parse(timeExport);
			alarm.setHours(date.getHours());
			alarm.setMinutes(date.getMinutes());
			alarm.setSeconds(date.getSeconds());

		} catch (Exception ex) {
			if (Common.DEBUG)
				log.writeLogInfo("FileExportService", "Oops! Can not get time poll value.Time poll = 28800000(Default=8h)");
		}
		time.schedule(st, alarm, timePoll);
	}
}
