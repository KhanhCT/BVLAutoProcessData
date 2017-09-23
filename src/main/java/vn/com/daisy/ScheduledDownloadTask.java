package vn.com.daisy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import vn.com.daisy.DAO.BvlBill;
import vn.com.daisy.DAO.BvlBillDAO;
import vn.com.daisy.DAO.BvlBillId;
import vn.com.daisy.DAO.BvlOutlet;
import vn.com.daisy.DAO.BvlOutletDAO;
import vn.com.daisy.DAO.BvlOutletId;
import vn.com.daisy.DAO.SysvarDAO;

public class ScheduledDownloadTask extends TimerTask {
	private Ftp ftp;
	private SysvarDAO sysDAO = new SysvarDAO();
	private List<String> preFiles = new ArrayList<>();
	private Log log = new Log(this.getClass().getName());

	@Override
	public void run() {
		String ftpServer = null;
		int ftpPort = ConfigTag.DEFAULT_PORT;
		String ftpUsername = null;
		String ftpPassword = null;
		String inFolder = ConfigTag.DEFAULT_FOLDER;
		String hostDir = ConfigTag.DEFAULT_HOSTDIR;
		String backupIn = null;
		List<String> currentFiles = new ArrayList<>();
		List<String> tempFiles = new ArrayList<>();
		List<String> fileFTPs = new ArrayList<>();
		try {
			ftpServer = sysDAO.getValue(ConfigTag.FTP_SERVER).trim();
			ftpUsername = sysDAO.getValue(ConfigTag.FTP_USERNAME).trim();
			ftpPassword = sysDAO.getValue(ConfigTag.FTP_PASSWORD).trim();
			ftpPort = Integer.parseInt(sysDAO.getValue(ConfigTag.FTP_PORT).trim());
			inFolder = sysDAO.getValue(ConfigTag.IMPORT_DIR).trim();
			hostDir = sysDAO.getValue(ConfigTag.FTP_INDIR).trim();
			backupIn = sysDAO.getValue(ConfigTag.FTP_BACKUP_IN).trim();
			if (Common.DEBUG)
				log.writeLogInfo("run()", ftpServer + " " + ftpPassword + " " + ftpPassword + " " + ftpPort);
		} catch (NullPointerException ex) {
			if (Common.DEBUG) {
				log.writeLogError("ScheduledDownloadTask()", "Oops! System varibles is NULL  . System exit");
				log.writeLogError("ScheduledDownloadTask()", ex.toString());
			} else {
				Common.print("ScheduledDownloadTask()", "Oops! System varibles is NULL  . System exit");
				ex.printStackTrace();
			}
			System.exit(0);
		}
		ftp = new Ftp(ftpServer, ftpUsername, ftpPassword, ftpPort);
		currentFiles = ftp.listFile(hostDir);
		if (currentFiles.toString() =="[]") {
			if (Common.DEBUG)
				log.writeLogInfo("run()", "Not file on FTP Server");
			else
				Common.print("ScheduledDownloadTask()", "Not file on FTP Server");
		} else {
			if (currentFiles.size() != preFiles.size()) {
				if (Common.DEBUG)
					log.writeLogInfo("ScheduledDownloadTask()", "New file on FTP server created");
				else
					Common.print("ScheduledDownloadTask()", "New file on FTP server created");
				tempFiles.clear();
				for (String str : currentFiles)
					tempFiles.add(str);
				currentFiles.removeAll(preFiles);
				if (Common.DEBUG) {
					log.writeLogInfo("run()", "Size preFiles " + preFiles.size());
					log.writeLogInfo("run()", "Size currentFiles " + currentFiles.size());
				} else {
					Common.print("ScheduledDownloadTask()", "Size preFiles " + preFiles.size());
					Common.print("ScheduledDownloadTask()", "Size currentFiles " + currentFiles.size());
				}

				for (String str : currentFiles) {
					ftp.ftpDownloadFile(hostDir + str, inFolder + str);
				}
				fileFTPs = Common.readFile(inFolder + "file_ftp.txt");
				for (String str : currentFiles) {
					// check file type
					if (str.contains("OUTLET") && str.endsWith(".txt") && !Common.checkUpdate(fileFTPs, str)) {
						Common.print("ScheduledDownloadTask()", "Update Outlet table");
						updateOutletTable(inFolder + str);
						Common.writeFile(inFolder + "file_ftp.txt", str);
					} else {
						Common.print("ScheduledDownloadTask()", "Not Update Outlet table");
						if (Common.DEBUG)
							log.writeLogInfo("ScheduledDownloadTask()", "Unknow File type");
						else
							Common.print("ScheduledDownloadTask()", "Unknow File type");
					}
					if (str.contains("BILL") && str.endsWith(".txt") && !Common.checkUpdate(fileFTPs, str)) {
						Common.print("ScheduledDownloadTask()", "Update Bill table");
						updateBvlBillTable(inFolder, str);
						Common.writeFile(inFolder + "file_ftp.txt", str);
					} else {
						Common.print("ScheduledDownloadTask()", "not Update Bill table");
						if (Common.DEBUG)
							log.writeLogInfo("ScheduledDownloadTask()", "Unknow File type");
						else
							Common.print("ScheduledDownloadTask()", "Unknow File type");
					}
					// upload file
					ftp.ftpUploadFile(inFolder + str, backupIn, str);
				}
				preFiles.clear();
				for (String str : tempFiles)
					preFiles.add(str);

				if (Common.DEBUG)
					log.writeLogInfo("run()", "Size preFile " + preFiles.size());
				else
					Common.print("ScheduledDownloadTask()", "Size preFile " + preFiles.size());
			}
		}
		ftp.disconnect();
	}

	public void updateOutletTable(String urlFile) {
		BvlOutletDAO bvlODAO = new BvlOutletDAO();
		BvlOutlet bo = new BvlOutlet();
		BufferedReader bufferedReader = null;
		File sourceFile = null;

		try {
			sourceFile = new File(urlFile);
			if (sourceFile.isFile() && Common.DEBUG)
				log.writeLogInfo("updateDatabaseFromFile()", "This is File");
			Common.print("updateDatabaseFromFile()", "This is File");

			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), "UTF8"));

			String line = bufferedReader.readLine();
			line = bufferedReader.readLine();
			while (line != null && line != "") {
				if (Common.DEBUG)
					log.writeLogInfo("updateOutleTable()", line);
				else
					Common.print("updateOutleTable()", line);
				bo = convertLineToBvlOutlet(line);
				Common.print("updateOutleTable()", bo.getCompanyCode());
				bvlODAO.addData(bo);
				line = bufferedReader.readLine();
			}
			if (Common.DEBUG)
				log.writeLogInfo("updateOutleTable()", "Update BvlOutlet Table Successfull!!");
			else
				Common.print("updateOutleTable()", "Update BvlOutlet Table Successfull!!");
		} catch (FileNotFoundException e) {
			if (Common.DEBUG)
				log.writeLogInfo("updateOutleTable()", "File not found!!");
			else
				Common.print("updateOutleTable()", "File not found!!");
			e.printStackTrace();
		} catch (IOException e) {
			if (Common.DEBUG) {
				log.writeLogInfo("updateOutleTable()", "File Busy!!!");
				log.writeLogInfo("updateOutleTable()", e.toString());
			} else {
				Common.print("updateOutleTable()", "File Busy!!!");
				e.printStackTrace();
			}

		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					if (Common.DEBUG)
						log.writeLogInfo("updateOutleTable()", "Close buffer reader successfully!!");
					else
						Common.print("updateOutleTable()", "Close buffer reader successfully!!");
				} catch (IOException e) {

					if (Common.DEBUG) {
						log.writeLogWarning("updateOutleTable()", "Oops! Can not close file");
						log.writeLogWarning("updateOutleTable()", e.toString());
					} else {
						Common.print("updateOutleTable()", "Oops! Can not close file");
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void updateBvlBillTable(String folder, String fileName) {
		BvlBillDAO billDAO = new BvlBillDAO();
		BvlBill bill = new BvlBill();
		BufferedReader bufferedReader = null;
		File sourceFile = null;
		String line = null;
		// DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh/mm/ss");
		try {
			sourceFile = new File(folder + fileName);
			if (sourceFile.isFile() && Common.DEBUG)
				log.writeLogInfo("updateBvlBillTable()", "This is File");
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), "UTF8"));
			line = bufferedReader.readLine();
			line = bufferedReader.readLine();
			while (line != null && line != "") {
				if (Common.DEBUG)
					log.writeLogInfo("updateBvlBillTable()", "Data input: " + line);
				else
					Common.print("updateBvlBillTable()", "Data input: " + line);
				bill = convertLineToBvlBill(line);
				if (bill != null) {
					bill.setBbiFileIn(fileName);
					bill.setBbiTimeIn(new Date());
					if (billDAO.getBill(bill.getId().getBbiBarcode()) == null) {
						if (Common.DEBUG)
							log.writeLogInfo("updateBvlBillTable()", "Add new entry to database");
						else
							Common.print("updateBvlBillTable()", "Add new entry to database");
						billDAO.addData(bill);
					} else {
						String str = billDAO.getBill(bill.getId().getBbiBarcode()).getBbiBankAccount();
						if (str == null || !str.matches("01")) {
							if (Common.DEBUG)
								log.writeLogInfo("updateBvlBillTable()", "Update old entry to database");
							else
								Common.print("updateBvlBillTable()", "Update old entry to database");
							billDAO.addData(bill);
						}
					}
				}
				line = bufferedReader.readLine();
				if (line.contains("BBI_")) {
					line = null;

				}

			}
			if (Common.DEBUG)
				log.writeLogInfo("updateBvlBillTable()", "Update BvlBill Table Successfull!!");
			else
				Common.print("updateBvlBillTable()", "Update BvlBill Table Successfull!!");

		} catch (IOException ex) {
			if (Common.DEBUG) {
				log.writeLogWarning("updateBvlBillTable()", "File not ready");
				log.writeLogError("updateBvlBillTable()", ex.toString());
			} else {
				Common.print("updateBvlBillTable()", "File not ready");
				ex.printStackTrace();
			}

		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
					if (Common.DEBUG)
						log.writeLogInfo("updateBvlBillTable()", "Close buffer reader successfully!!");
					else
						Common.print("updateBvlBillTable()", "Close buffer reader successfully!!");
				} catch (IOException e) {

					if (Common.DEBUG) {
						log.writeLogWarning("updateBvlBillTable()", "Oops! Can not close file");
						log.writeLogWarning("updateBvlBillTable()", e.toString());
					} else {
						Common.print("updateBvlBillTable()", "Oops! Can not close file");
						e.printStackTrace();
					}

				}
			}
		}
	}

	public static String extractText(String text) {
		String[] arr = text.split("\"");
		return arr[1];
	}

	/**
	 * Function: Convert data from file to Bvl_Bill object
	 * 
	 * @param data
	 * @return
	 */
	public BvlBill convertLineToBvlBill(String data) {
		BvlBill bill = new BvlBill();
		String str = null;
		int i = 0;
		BvlBillId billId = new BvlBillId();
		String[] arr = data.split(",");
		Date date = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		if (arr.length != 21) {
			if (Common.DEBUG)
				log.writeLogInfo("convertLineToBvlBill()", "Line is not format");
			else
				Common.print("convertLineToBvlBill()", "Line is not format");
			return null;
		}
		try {
			// 1
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiId(extractText(str));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlBill()", "BbiId is NULL");
				else
					Common.print("convertLineToBvlBill()", "BbiId is NULL");
				return null;
			}
			// 2
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiCompanyCode(extractText(str));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlBill()", "BbiCompanyCode is NULL");
				else
					Common.print("convertLineToBvlBill()", "BbiCompanyCode is NULL");
				return null;
			}
			// 3
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiOutletNumber(extractText(str));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlBill()", "BbiOutletNumber is NULL");
				else
					Common.print("convertLineToBvlBill()", "BbiOutletNumber is NULL");
				return null;
			}
			// 4
			str = arr[i++];
			if (!str.matches("\"\"")) {
				billId.setBbiInvoiceNumber(extractText(str));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlBill()", "BbiInvoiceNumber is NULL");
				else
					Common.print("convertLineToBvlBill()", "BbiInvoiceNumber is NULL");
				return null;
			}
			// 5
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiInvoiceAmount(Long.parseLong(extractText(str)));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlBill()", "BbiInvoiceAmount is NULL");
				else
					Common.print("convertLineToBvlBill()", "BbiInvoiceAmount is NULL");
				return null;
			}
			// 6
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiAccountNumber(extractText(str));
			}
			// 7
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiPolicyholder(extractText(str));
			}
			// 8
			str = arr[i++];
			if (!str.matches("\"\"")) {

				date = dateFormat.parse(extractText(str));
				bill.setBbiFromDate(date);

			}
			// 9
			str = arr[i++];
			if (!str.matches("\"\"")) {

				date = dateFormat.parse(extractText(str));
				bill.setBbiToDate(date);

			}
			// 10
			str = arr[i++];
			if (!str.matches("\"\"")) {
				billId.setBbiBarcode(extractText(str));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlBill()", "BbiBarcode is NULL");
				else
					Common.print("convertLineToBvlBill()", "BbiBarcode is NULL");
				return null;
			}
			// 11
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiDataSource(extractText(str));
			}
			// 12
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiInvoiceType(extractText(str));
			}
			// 13
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiChannelFreeRegister(extractText(str));
			}
			// 14
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiStatus(extractText(str));
			} else {
				return null;
			}
			// 15
			str = arr[i++];
			if (!str.matches("\"\"")) {

				date = dateFormat.parse(extractText(str));
				bill.setBbiFeeDate(date);

			}

			// 16
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiBankAccount(extractText(str));
			}
			// 17
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiCifNumber(extractText(str));
			}
			// 18
			str = arr[i++];
			if (!str.matches("\"\"")) {

				date = dateFormat.parse(extractText(str));
				bill.setBbiRegisterFeeDate(date);

			}
			// 19
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiBankRegister(extractText(str));
			}

			// 20

			str = arr[i++];
			if (!str.matches("\"\"")) {
				date = dateFormat.parse(extractText(str));
				bill.setBbiInvoiceEdate(date);
			}
			// 21
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bill.setBbiPaymentInstruction(extractText(str));
			}
		} catch (Exception ex) {
			if (Common.DEBUG) {
				log.writeLogWarning("convertLineToBvlBill()", "Data is not format");
				log.writeLogWarning("convertLineToBvlBill()", ex.toString());
			} else {
				Common.print("convertLineToBvlOutlet()", "Data is not format");
				ex.printStackTrace();

			}
			return null;
		}
		bill.setId(billId);
		return bill;
	}

	public BvlOutlet convertLineToBvlOutlet(String data) {
		BvlOutlet bvlOutlet = new BvlOutlet();
		BvlOutletId id = new BvlOutletId();
		String[] arr = data.split(",");
		int i = 0;
		String str = null;
		if (arr.length != 22) {
			if (Common.DEBUG)
				log.writeLogInfo("convertLineToBvlOutlet()", "Line is not format");
			else
				Common.print("convertLineToBvlOutlet()", "Line is not format");
			return null;
		}
		try {
			// 1
			str = arr[i++];
			if (!str.matches("\"\"")) {
				id.setOutletId(Long.parseLong(extractText(str)));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlOutlet()", "OutletId is NULL");
				else
					Common.print("convertLineToBvlOutlet()", "OutletId is NULL");
				return null;
			}

			// 2
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setLocationId(Long.parseLong(extractText(str)));
			}
			// 3
			str = arr[i++];
			if (!str.matches("\"\"")) {
				id.setOutletNumber(extractText(str));
			} else {
				if (Common.DEBUG)
					log.writeLogInfo("convertLineToBvlOutlet()", "OutletNumber is NULL");
				else
					Common.print("convertLineToBvlOutlet()", "OutletNumber is NULL");
				return null;
			}
			// 4
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setOutletBvlifeId(extractText(str));
			}
			// 5
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setCompanyId(Long.parseLong(extractText(str)));
			}
			// 6
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setCompanyCode(extractText(str));
			}
			// 7
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setCompanyBvlifeId(extractText(str));
			}
			// 8
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setCompanyName(extractText(str));
			}
			// 9
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesOfficeId(Long.parseLong(extractText(str)));
			}
			// 10
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesOfficeCode(extractText(str));
			}
			// 11
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesOfficeBvlifeId(extractText(str));
			}
			// 12
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesOfficeName(extractText(str));
			}
			// 13
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesAgencyId(Long.parseLong(extractText(str)));
			}
			// 14
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesAgencyCode(extractText(str));
			}

			// 15
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesAgencyBvlifeId(extractText(str));
			}
			// 16
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesAgencyName(extractText(str));
			}
			// 17
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesUnitId(Long.parseLong(extractText(str)));
			}

			// 18
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesUnitCode(extractText(str));
			}
			// 19
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesUnitBvlifeId(extractText(str));
			}
			// 20
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setSalesUnitName(extractText(str));
			}
			// 21
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setOutletName(extractText(str));
			}
			// 22
			str = arr[i++];
			if (!str.matches("\"\"")) {
				bvlOutlet.setStatus(extractText(str).toCharArray()[0]);
			}
		} catch (Exception ex) {
			if (Common.DEBUG) {
				log.writeLogWarning("convertLineToBvlOutlet()", "Data is not format");
				log.writeLogWarning("convertLineToBvlOutlet()", ex.toString());
			} else {
				Common.print("convertLineToBvlOutlet()", "Data is not format");
				ex.printStackTrace();

			}
			return null;
		}
		bvlOutlet.setId(id);
		return bvlOutlet;
	}

}