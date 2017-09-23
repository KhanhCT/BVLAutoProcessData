package vn.com.daisy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import vn.com.daisy.DAO.BvlBill;
import vn.com.daisy.DAO.BvlBillDAO;
import vn.com.daisy.DAO.BvlCollateInfo;
import vn.com.daisy.DAO.BvlCollateInfoDAO;
import vn.com.daisy.DAO.BvlCompanyDAO;
import vn.com.daisy.DAO.BvlOutletDAO;
import vn.com.daisy.DAO.SysvarDAO;
import vn.com.daisy.excel.Bvnt;
import vn.com.daisy.excel.ExcelExporter;

// Create a class extends with TimerTask
public class ScheduledUploadTask extends TimerTask {

	private SimpleDateFormat dateFormat;
	private SimpleDateFormat timeFormat;
	private int index = 1;
	private BvlBillDAO billDAO = new BvlBillDAO();
	private List<BvlBill> bills = null;
	private SysvarDAO sysDAO = new SysvarDAO();
	private Date preDate = new Date();
	private Log log = new Log(this.getClass().getName());
	private Ftp ftp;
	private BvlCollateInfoDAO bvlOutletDAO = new BvlCollateInfoDAO();
	private BvlCompanyDAO bvlDAO = new BvlCompanyDAO();
	private ExcelExporter ee = new ExcelExporter();

	@SuppressWarnings("deprecation")
	public void run() {
		String ftpServer = null;
		int ftpPort = ConfigTag.DEFAULT_PORT;
		String ftpUsername = null;
		String ftpPassword = null;
		String fileName = null;
		String hostDir = ConfigTag.DEFAULT_HOSTDIR;
		String localFolder = ConfigTag.DEFAULT_FOLDER;
		String backupOut = null;
		Date currentDate = new Date();
		// List file
		List<String> al1 = null;
		List<String> al2 = null;
		List<Bvnt> bvnts = null;
		int checkExport = 0;
		boolean status = false;

		Bvnt bvnt = null;
		double amount = 0;
		try {
			ftpServer = sysDAO.getValue(ConfigTag.FTP_SERVER).trim();
			ftpUsername = sysDAO.getValue(ConfigTag.FTP_USERNAME).trim();
			ftpPassword = sysDAO.getValue(ConfigTag.FTP_PASSWORD).trim();
			ftpPort = Integer.parseInt(sysDAO.getValue(ConfigTag.FTP_PORT).trim());
			hostDir = sysDAO.getValue(ConfigTag.FTP_OUTDIR).trim();
			localFolder = sysDAO.getValue(ConfigTag.EXPORT_DIR).trim();
			backupOut = sysDAO.getValue(ConfigTag.FTP_BACKUP_OUT).trim();
			if (Common.DEBUG)
				log.writeLogInfo("ScheduledUploadTask()",
						ftpServer + " " + ftpPassword + " " + ftpPassword + " " + ftpPort);
			else
				Common.print("ScheduledUploadTask()",
						ftpServer + " " + ftpPassword + " " + ftpPassword + " " + ftpPort);
		} catch (NullPointerException ex) {
			if (Common.DEBUG) {
				log.writeLogError("ScheduledUploadTask()", "Oops! System varibles is NULL  . System exit");
				log.writeLogError("ScheduledUploadTask()", ex.toString());
			} else {
				Common.print("ScheduledUploadTask()", "Oops! System varibles is NULL  . System exit");
				ex.printStackTrace();
			}
			System.exit(0);
		}
		ftp = new Ftp(ftpServer, ftpUsername, ftpPassword, ftpPort);

		BvlCollateInfo bvlCollateInfo = bvlOutletDAO.getBvlCollateInfo(currentDate, "MATCH", "NO");
		if (bvlCollateInfo == null) {
			status = false;
			if (Common.DEBUG)
				log.writeLogInfo("ScheduledUploadTask", "Not Mach");
			else
				Common.print("ScheduledUploadTask", "Not Mach");
		} else {
			status = true;
			if (Common.DEBUG)
				log.writeLogInfo("ScheduledUploadTask", "Mach");
			else
				Common.print("ScheduledUploadTask", "Mach");
		}

		if (ftp.isConnected() && status) {
			status = false;

			// Write BVL Report
			bills = billDAO.getAllBills(currentDate, "2", "01", "1");
			System.out.println(bills.size());
			if (bills.toString() != "[]") {
				// Common.print("ScheduledUploadTask", "BVL");
				writeBVLCSVFile(bills, localFolder, index);
				checkExport++;
				fileName = createFileName(2, index);
				ftp.ftpUploadFile(localFolder + fileName, hostDir, fileName);

			} else {
				if (Common.DEBUG) {
					log.writeLogWarning("ScheduledUploadTask", "Not data Available");
				} else
					Common.print("ScheduledUploadTask", "BVL Not data Available");
			}

			// Export TLM Report
			bills = billDAO.getAllBills(currentDate, "1", "01", "1");
			if (bills.toString() != "[]") {
				writeTLMCSVFile(bills, localFolder, index);
				checkExport++;
				fileName = createFileName(3, index);
				ftp.ftpUploadFile(localFolder + fileName, hostDir, fileName);
				// Update database file name
				for (BvlBill bill : bills) {
					bill.setBbiFileOut(fileName);
					bill.setBbiTimeOut(currentDate);
					billDAO.addData(bill);
				}
			} else {
				if (Common.DEBUG) {
					log.writeLogWarning("run()", "Not data Available");
				} else {
					Common.print("ScheduledUploadTask", "TLM Not data Available");
				}
			}

			// Export BVB Report
			bills = billDAO.getAllBills(currentDate, "01");
			if (bills.toString() != "[]") {
				// init array
				al1 = new ArrayList<>();
				al2 = new ArrayList<>();
				bvnts = new ArrayList<>();
				// write report BVB
				writeBVBCSVFile(bills, localFolder, index);

				// upload to FTP server
				fileName = createFileName(4, index);
				ftp.ftpUploadFile(localFolder + fileName, hostDir, fileName);
				// Update database file name
				for (BvlBill bill : bills) {
					bill.setBbiFileCoreOut(fileName);
					bill.setBbiTimeCoreOut(currentDate);
					billDAO.addData(bill);
				}
				// create excel CTTV file
				fileName = createFileName(6, index);
				// Upload to FTP server
				ee.createCttvReport(localFolder + fileName, bills, "TCT BVNT–CHITIET");
				ftp.ftpUploadFile(localFolder + fileName, hostDir, fileName);
				// create excel BVNT file
				fileName = createFileName(5, index);
				// process bvnt report
				al1.clear();
				al2.clear();
				for (BvlBill bill : bills) {
					al1.add(bill.getBbiCompanyCode());
				}
				al1 = Common.removeDuplicates(al1);

				for (String str : al1) {
					for (BvlBill bill : bills) {
						if (bill.getBbiCompanyCode().equals(str)) {
							al2.add(bill.getBbiTransactionId());
							amount += bill.getBbiInvoiceAmount();
						}
					}
					al2 = Common.removeDuplicates(al2);
					bvnt = new Bvnt(currentDate, str, bvlDAO.getBCOBankAccount(str), al2.size(), amount);
					bvnts.add(bvnt);
				}
				ee.createBvntReport(localFolder + fileName, bvnts, "TCT BVNT–TONGHOP");
				checkExport++;
				ftp.ftpUploadFile(localFolder + fileName, hostDir, fileName);

			} else {
				if (Common.DEBUG) {
					log.writeLogWarning("run()", "Not data Available");
				} else {
					Common.print("ScheduledUploadTask", "BVB Not data Available");
				}
			}

			/* Process delete file */
			al1 = ftp.listFile(backupOut);
			al2 = ftp.listFile(hostDir);
			al2.retainAll(al1);

			for (String str : al2) {
				ftp.ftpDeleteFile(hostDir + str);
			}
			ftp.disconnect();
			// Auto increment period index report
			if (currentDate.getDate() != preDate.getDate()) {
				index = 1;
				preDate = currentDate;
				if (Common.DEBUG)
					log.writeLogInfo("run()", "Change STT :" + index);
			}
			index++;
			Common.print("ScheduledUploadTask", "Export File Successful");
			if (checkExport == 3) {
				bvlCollateInfo.setBciExportStatus("YES");
				bvlOutletDAO.addBvlCollateInfos(bvlCollateInfo);
			}
			checkExport = 0;
		} else {
			if (Common.DEBUG) {
				log.writeLogInfo("run()", "FTP disconneted!!!");
			}
		}
	}

	public String createFileName(int type, int stt) {
		String name = null;
		dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		switch (type) {

		case 1: {
			name = "BVL_BILL_" + dateFormat.format(date) + "_" + String.valueOf(stt) + ".csv";
		}

			break;
		/* Hóa đơn BVLife đã thu */
		case 2: {
			name = "BVB_Import_BVL_" + dateFormat.format(date) + "_" + String.valueOf(stt) + ".csv";

		}
			break;
		/* Hóa đơn Talisman đã thu */
		case 3: {
			name = "BVB_Import_TLM_" + dateFormat.format(date) + "_" + String.valueOf(stt) + ".csv";
		}
			break;
		/* Hóa đơn thu qua BVB */
		case 4: {
			name = "BVB_Import_" + dateFormat.format(date) + "_" + String.valueOf(stt) + ".csv";
		}
			break;
		case 5: {
			name = "TCT_BVNT_TONGHOP-" + dateFormat.format(date) + "_" + String.valueOf(stt) + ".xlsx";
		}
			break;
		case 6: {
			name = "TCT_BVNT_CHITIET-" + dateFormat.format(date) + "_" + String.valueOf(stt) + ".xlsx";
		}
			break;
		}

		return name;
	}

	/**
	 * Function: create file of BVL
	 * 
	 * @param bills
	 * @param folderUrl
	 * @param stt
	 */
	public void writeBVLCSVFile(List<BvlBill> bills, String folderUrl, int stt) {
		List<String> strs;
		int rows = 1;
		String nameFile = null;
		nameFile = createFileName(2, stt);
		String urlFile = folderUrl + nameFile;
		BvlOutletDAO outDAO = new BvlOutletDAO();
		dateFormat = new SimpleDateFormat("yyyyMMdd");
		timeFormat = new SimpleDateFormat("hhmmss");
		if (Common.DEBUG)
			log.writeLogInfo("writeBVLCSVFile()", "File Url: " + urlFile);
		String companyCode = null;
		Date date = new Date();

		int numberBills = bills.size();
		double moneys = 0;
		String trailer = "END OF FILE";
		FileWriter fw = null;
		File csvFile = null;
		try {

			csvFile = new File(urlFile);
			if (!csvFile.exists()) {
				csvFile.createNewFile();
			}
			fw = new FileWriter(csvFile);
			// Write Header file
			strs = new ArrayList<>();
			strs.add(String.valueOf(rows));
			strs.add(companyCode);

			strs.add(dateFormat.format(date));
			strs.add(timeFormat.format(date));
			strs.add(String.valueOf(numberBills));
			for (BvlBill bill : bills) {
				moneys += bill.getBbiInvoiceAmount();
			}
			// need check
			strs.add(String.valueOf(moneys));
			CSVUtils.writeLine(fw, strs);
			dateFormat = new SimpleDateFormat("dd/MM/yyy");
			timeFormat = new SimpleDateFormat("dd/MM/yyy hh:mm:ss");
			// write transaction file
			for (BvlBill bill : bills) {
				rows++;
				strs.clear();
				strs.add(String.valueOf(rows));

				strs.add(checkNull(bill.getBbiCompanyCode()));
				strs.add(checkNull(outDAO.getoutletBvlifeId(bill.getBbiPaymentOutlet())));
				strs.add(checkNull(bill.getId().getBbiInvoiceNumber()));
				if (bill.getBbiFromDate() != null)
					strs.add(dateFormat.format(bill.getBbiFromDate()));
				else
					strs.add("\"\"");
				if (bill.getBbiToDate() != null)
					strs.add(dateFormat.format(bill.getBbiToDate()));
				else
					strs.add("\"\"");

				strs.add(checkNull(String.valueOf(bill.getBbiInvoiceAmount())));
				strs.add(checkNull(bill.getBbiAccountNumber()));
				strs.add(checkNull(bill.getId().getBbiBarcode()));
				if (bill.getBbiFeeDate() != null)
					strs.add(timeFormat.format(bill.getBbiFeeDate()));
				else
					strs.add("\"\"");
				if (bill.getBbiEffectiveDate() != null)
					strs.add(dateFormat.format(bill.getBbiEffectiveDate()));
				else
					strs.add("\"\"");
				strs.add(checkNull(bill.getBbiTransactionCoCode()));
				CSVUtils.writeLine(fw, strs);
			}
			// write footer file
			strs.clear();
			strs.add(trailer);
			CSVUtils.writeLine(fw, strs);
			fw.flush();
			fw.close();

		} catch (IOException e) {
			if (Common.DEBUG)
				log.writeLogError("writeBVLCSVFile()", "Error export file");
			else
				e.printStackTrace();
		} catch (NullPointerException ex) {
			if (Common.DEBUG)
				log.writeLogError("writeBVLCSVFile()", "Null point");
			else
				Common.print("writeBVLCSVFile()", "Null point");
		}

	}

	/**
	 * Function: create file of TLM
	 * 
	 * @param bills
	 * @param folderUrl
	 * @param stt
	 */
	public void writeTLMCSVFile(List<BvlBill> bills, String folderUrl, int stt) {
		List<String> strs;

		int rows = 1;
		String nameFile = null;
		nameFile = createFileName(3, stt);

		String urlFile = folderUrl + nameFile;
		BvlCompanyDAO bvlCompanyDAO = new BvlCompanyDAO();
		if (Common.DEBUG)
			log.writeLogInfo("writeTLMCSVFile()", "File Url: " + urlFile);
		else
			Common.print("writeTLMCSVFile()", "File Url: " + urlFile);
		//String trailer = "END OF FILE";
		FileWriter fw = null;
		File csvFile = null;
		dateFormat = new SimpleDateFormat("ddMMyyy");
		timeFormat = new SimpleDateFormat("dd/MM/yyy hh:mm:ss");
		try {
			csvFile = new File(urlFile);
			if (!csvFile.exists()) {
				csvFile.createNewFile();
			}
			fw = new FileWriter(csvFile);
			// Write Header file
			strs = new ArrayList<>();
			// write transaction file

			for (BvlBill bill : bills) {
				strs.clear();
				strs.add(String.valueOf(rows)); // Running number
				strs.add("BTRF");
				strs.add(checkNull(bill.getBbiCompanyCode()));
				strs.add(checkNull(bvlCompanyDAO.getBCOBankAccount(bill.getBbiCompanyCode())));// Bank
				// Account
				strs.add(checkNull(bill.getId().getBbiBarcode()));
				strs.add(checkNull(bill.getId().getBbiBarcode()));

				strs.add(checkNull(bill.getBbiPolicyholder()));
				strs.add(checkNull(String.valueOf(bill.getBbiInvoiceAmount())));
				strs.add(checkNull(bill.getBbiOutletNumber()));

				if (bill.getBbiFeeDate() != null)
					strs.add(dateFormat.format(bill.getBbiFeeDate()));
				else
					strs.add("\"\"");
				if (bill.getBbiEffectiveDate() != null)
					strs.add(dateFormat.format(bill.getBbiEffectiveDate()));
				else
					strs.add("\"\"");

				strs.add(bill.getBbiAccountNumber());
				strs.add("\"\"");
				strs.add("PR");
				CSVUtils.writeLine(fw, strs);
				rows++;
			}
			// write footer file
		//	strs.clear();
		//	strs.add(trailer);
		//	CSVUtils.writeLine(fw, strs);

			fw.flush();
			fw.close();
		} catch (IOException e) {
			if (Common.DEBUG)
				log.writeLogInfo("writeBVLCSVFile()", "Error export file");
			else
				e.printStackTrace();
		}

	}

	/**
	 * Function: create file of BVB
	 * 
	 * @param bills
	 * @param folderUrl
	 * @param stt
	 */
	public void writeBVBCSVFile(List<BvlBill> bills, String folderUrl, int stt) {
		List<String> strs;
		int rows = 1;

		String nameFile = null;
		nameFile = createFileName(4, stt);
		String urlFile = folderUrl + nameFile;
		dateFormat = new SimpleDateFormat("dd/MM/yyy");
		timeFormat = new SimpleDateFormat("dd/MM/yyy hh:mm:ss");
		// timeDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (Common.DEBUG)
			log.writeLogInfo("writeBVLCSVFile()", "File Url: " + urlFile);
	//	String trailer = "END OF FILE";
		FileWriter fw = null;
		File csvFile = null;
		try {
			csvFile = new File(urlFile);
			if (!csvFile.exists()) {
				csvFile.createNewFile();
			}
			fw = new FileWriter(csvFile);
			// Write Header file
			strs = new ArrayList<>();
			CSVUtils.writeLine(fw, strs);
			// write transaction file
			for (BvlBill bill : bills) {
				strs.clear();
				strs.add(String.valueOf(rows));
				strs.add(checkNull(String.valueOf(bill.getBbiId())));
				strs.add(checkNull(bill.getBbiCompanyCode()));
				strs.add(checkNull(bill.getBbiOutletNumber()));
				strs.add(checkNull(bill.getBbiInvoiceType()));
				strs.add(checkNull(String.valueOf(bill.getBbiInvoiceAmount())));
				strs.add(checkNull(bill.getBbiAccountNumber()));
				strs.add(checkNull(bill.getBbiPolicyholder()));
				if (bill.getBbiFromDate() != null)
					strs.add(dateFormat.format(bill.getBbiFromDate()));
				else
					strs.add("\"\"");

				if (bill.getBbiToDate() != null)
					strs.add(dateFormat.format(bill.getBbiToDate()));
				else
					strs.add("\"\"");

				strs.add(checkNull(bill.getId().getBbiBarcode()));

				strs.add(checkNull(bill.getBbiDataSource()));
				strs.add(checkNull(bill.getBbiInvoiceType()));
				strs.add(checkNull(bill.getBbiChannelFreeRegister()));
				strs.add(checkNull(bill.getBbiChannelFreePerform()));
				strs.add(checkNull(bill.getBbiStatus()));
				if (bill.getBbiFeeDate() != null)
					strs.add(timeFormat.format(bill.getBbiFeeDate()));
				else
					strs.add("\"\"");
				if (bill.getBbiEffectiveDate() != null)
					strs.add(dateFormat.format(bill.getBbiEffectiveDate()));
				else
					strs.add("\"\"");
				strs.add(checkNull(bill.getBbiTransactionCoCode()));
				strs.add(checkNull(bill.getBbiTransactionId()));
				if (bill.getBbiLastUpdatedDate() != null)
					strs.add(timeFormat.format(bill.getBbiLastUpdatedDate()));
				else
					strs.add("\"\"");
				strs.add(checkNull(bill.getBbiTransactionInputter()));
				strs.add(checkNull(bill.getBbiTransactionAuthoriser()));
				try {
					strs.add(checkNull(bill.getBbiTransactionAmount().toString()));
				} catch (Exception ex) {
					strs.add("out of range");
				}
				strs.add(checkNull(bill.getBbiBankAccount()));
				strs.add(checkNull(bill.getBbiCifNumber()));
				if (bill.getBbiRegisterFeeDate() != null)
					strs.add(dateFormat.format(bill.getBbiRegisterFeeDate()));
				else
					strs.add("\"\"");

				strs.add(checkNull(bill.getBbiBankRegister()));
				strs.add(checkNull(bill.getBbiBankPerform()));
				if (bill.getBbiInvoiceEdate() != null)
					strs.add(dateFormat.format(bill.getBbiInvoiceEdate()));
				else
					strs.add("\"\"");
				strs.add(checkNull(bill.getBbiPaymentInstruction()));
				strs.add(checkNull(bill.getBbiPaymentOutlet()));

				strs.add(checkNull(bill.getBbiFileIn()));
				if (bill.getBbiTimeIn() != null)
					strs.add(dateFormat.format(bill.getBbiTimeIn()));
				else
					strs.add("\"\"");
				strs.add(checkNull(bill.getBbiFileCoreOut()));
				// DateFormat dFormat = null;
				if (bill.getBbiTimeCoreOut() != null) {

					// dFormat = new SimpleDateFormat("yyyy-MM-dd");
					strs.add(bill.getBbiTimeCoreOut().toString());
				} else
					strs.add("\"\"");
				strs.add(checkNull(bill.getBbiFileOut()));
				if (bill.getBbiTimeOut() != null)
					strs.add(bill.getBbiTimeOut().toString());
				else
					strs.add("\"\"");
				CSVUtils.writeLine(fw, strs);
				rows++;
			}
			// write footer file
		//	strs.clear();
	//		strs.add(trailer);
	//		CSVUtils.writeLine(fw, strs);
			strs.clear();
			fw.flush();
			fw.close();

		} catch (IOException e) {
			if (Common.DEBUG)
				log.writeLogError("writeBVLCSVFile()", "Error export file");
			else
				e.printStackTrace();
		}

	}

	private String checkNull(String str) {
		if (str == null)
			return "\"\"";
		else
			return str;
	}
}