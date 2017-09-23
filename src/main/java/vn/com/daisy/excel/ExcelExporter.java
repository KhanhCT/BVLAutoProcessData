package vn.com.daisy.excel;

import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import vn.com.daisy.Common;
import vn.com.daisy.Log;
import vn.com.daisy.DAO.BvlBill;
import vn.com.daisy.DAO.BvlCompanyDAO;

public class ExcelExporter {
	private CellStyle cs = null;
	private CellStyle csBold = null;
	private CellStyle csTop = null;
	private CellStyle csRight = null;
	private CellStyle csBottom = null;
	private CellStyle csLeft = null;
	private CellStyle csTopLeft = null;
	private CellStyle csTopRight = null;
	private CellStyle csBottomLeft = null;
	private CellStyle csBottomRight = null;
	private Log log = new Log(this.getClass().getName());

	public ExcelExporter() {

	}

	public void createCttvReport(String fileUrl, List<BvlBill> bill, String sheetName) {
		try {
			Workbook workbook = null;
			Sheet sheet = null;
			if (fileUrl.endsWith("xlsx")) {
				workbook = new XSSFWorkbook();
				sheet = workbook.createSheet(sheetName);
				// createBvntReport(fileUrl);
			} else if (fileUrl.endsWith("xls")) {
				workbook = new HSSFWorkbook();
				sheet = workbook.createSheet(sheetName);
				// createBvntReport(fileUrl);
			} else {
				if (Common.DEBUG)
					log.writeLogError("ExcelExporter()", "The specified file is not Excel file");
				else
					Common.print("ExcelExporter()", "The specified file is not Excel file");
			}
			setCellStyles(workbook);
			// Get current Date and Time
			Date date = new Date(System.currentTimeMillis());
			DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			// Set Column Widths
			sheet.setColumnWidth(0, 4000);
			sheet.setColumnWidth(1, 3000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 4000);
			sheet.setColumnWidth(4, 4000);

			// Setup the Page margins - Left, Right, Top and Bottom
			sheet.setMargin(Sheet.LeftMargin, 0.25);
			sheet.setMargin(Sheet.RightMargin, 0.25);
			sheet.setMargin(Sheet.TopMargin, 0.75);
			sheet.setMargin(Sheet.BottomMargin, 0.75);

			// Setup the Header and Footer Margins
			sheet.setMargin(Sheet.HeaderMargin, 0.25);
			sheet.setMargin(Sheet.FooterMargin, 0.25);

			// Set Header Information
			Header header = sheet.getHeader();
			header.setLeft("BVL");
			header.setCenter(HSSFHeader.font("Arial", "Bold") + HSSFHeader.fontSize((short) 14) + "BVNT Report");
			header.setRight(df.format(date));

			// Set Footer Information with Page Numbers
			Footer footer = sheet.getFooter();
			footer.setRight("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());
			int rowIndex = 0;

			rowIndex = insertCttvHeader(sheet, rowIndex, bill);
			rowIndex = insertCttvBody(sheet, rowIndex, bill);

			FileOutputStream fos = new FileOutputStream(fileUrl);
			workbook.write(fos);
		} catch (Exception ex) {
			if (Common.DEBUG)
				log.writeLogError("createBvntReport()", "Error with create Bvtn Report");
			else
				Common.print("ExcelExporter()", "Error with create Bvtn Report");
			ex.printStackTrace();
		}
	}

	public void createBvntReport(String fileUrl, List<Bvnt> bvnts, String sheetName) {
		try {
			Workbook workbook = null;
			Sheet sheet = null;
			if (fileUrl.endsWith("xlsx")) {
				workbook = new XSSFWorkbook();
				sheet = workbook.createSheet(sheetName);
				// createBvntReport(fileUrl);
			} else if (fileUrl.endsWith("xls")) {
				workbook = new HSSFWorkbook();
				sheet = workbook.createSheet(sheetName);
				// createBvntReport(fileUrl);
			} else {
				if (Common.DEBUG)
					log.writeLogError("ExcelExporter()", "The specified file is not Excel file");
				else
					Common.print("ExcelExporter()", "The specified file is not Excel file");
			}
			setCellStyles(workbook);
			// Get current Date and Time
			Date date = new Date(System.currentTimeMillis());
			DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			// Set Column Widths
			sheet.setColumnWidth(0, 4000);
			sheet.setColumnWidth(1, 3000);
			sheet.setColumnWidth(2, 6000);
			sheet.setColumnWidth(3, 4000);
			sheet.setColumnWidth(4, 4000);

			// Setup the Page margins - Left, Right, Top and Bottom
			sheet.setMargin(Sheet.LeftMargin, 0.25);
			sheet.setMargin(Sheet.RightMargin, 0.25);
			sheet.setMargin(Sheet.TopMargin, 0.75);
			sheet.setMargin(Sheet.BottomMargin, 0.75);

			// Setup the Header and Footer Margins
			sheet.setMargin(Sheet.HeaderMargin, 0.25);
			sheet.setMargin(Sheet.FooterMargin, 0.25);

			// Set Header Information
			Header header = sheet.getHeader();
			header.setLeft("BVL");
			header.setCenter(HSSFHeader.font("Arial", "Bold") + HSSFHeader.fontSize((short) 14) + "BVNT Report");
			header.setRight(df.format(date));

			// Set Footer Information with Page Numbers
			Footer footer = sheet.getFooter();
			footer.setRight("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());
			int rowIndex = 0;

			rowIndex = insertBvntHeader(sheet, rowIndex, bvnts);
			rowIndex = insertBvntBody(sheet, rowIndex, bvnts);

			FileOutputStream fos = new FileOutputStream(fileUrl);
			workbook.write(fos);
		} catch (Exception ex) {
			if (Common.DEBUG)
				log.writeLogError("createBvntReport()", "Error with create Bvtn Report");
			else
				Common.print("createBvntReport()", "Error with create Bvtn Report");
			ex.printStackTrace();
		}
	}

	private void setCellStyles(Workbook wb) {

		// font size 10
		Font f = wb.createFont();
		f.setFontHeightInPoints((short) 10);

		// Simple style
		cs = wb.createCellStyle();
		cs.setFont(f);

		// Bold Fond
		Font bold = wb.createFont();
		bold.setBoldweight(Font.BOLDWEIGHT_BOLD);
		bold.setFontHeightInPoints((short) 10);

		// Bold style
		csBold = wb.createCellStyle();
		csBold.setBorderBottom(CellStyle.BORDER_THIN);
		csBold.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		csBold.setFont(bold);

		// Setup style for Top Border Line
		csTop = wb.createCellStyle();
		csTop.setBorderTop(CellStyle.BORDER_THIN);
		csTop.setTopBorderColor(IndexedColors.BLACK.getIndex());
		csTop.setFont(f);

		// Setup style for Right Border Line
		csRight = wb.createCellStyle();
		csRight.setBorderRight(CellStyle.BORDER_THIN);
		csRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
		csRight.setFont(f);

		// Setup style for Bottom Border Line
		csBottom = wb.createCellStyle();
		csBottom.setBorderBottom(CellStyle.BORDER_THIN);
		csBottom.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		csBottom.setFont(f);

		// Setup style for Left Border Line
		csLeft = wb.createCellStyle();
		csLeft.setBorderLeft(CellStyle.BORDER_THIN);
		csLeft.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		csLeft.setFont(f);

		// Setup style for Top/Left corner cell Border Lines
		csTopLeft = wb.createCellStyle();
		csTopLeft.setBorderTop(CellStyle.BORDER_THIN);
		csTopLeft.setTopBorderColor(IndexedColors.BLACK.getIndex());
		csTopLeft.setBorderLeft(CellStyle.BORDER_THIN);
		csTopLeft.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		csTopLeft.setFont(f);

		// Setup style for Top/Right corner cell Border Lines
		csTopRight = wb.createCellStyle();
		csTopRight.setBorderTop(CellStyle.BORDER_THIN);
		csTopRight.setTopBorderColor(IndexedColors.BLACK.getIndex());
		csTopRight.setBorderRight(CellStyle.BORDER_THIN);
		csTopRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
		csTopRight.setFont(f);

		// Setup style for Bottom/Left corner cell Border Lines
		csBottomLeft = wb.createCellStyle();
		csBottomLeft.setBorderBottom(CellStyle.BORDER_THIN);
		csBottomLeft.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		csBottomLeft.setBorderLeft(CellStyle.BORDER_THIN);
		csBottomLeft.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		csBottomLeft.setFont(f);

		// Setup style for Bottom/Right corner cell Border Lines
		csBottomRight = wb.createCellStyle();
		csBottomRight.setBorderBottom(CellStyle.BORDER_THIN);
		csBottomRight.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		csBottomRight.setBorderRight(CellStyle.BORDER_THIN);
		csBottomRight.setRightBorderColor(IndexedColors.BLACK.getIndex());
		csBottomRight.setFont(f);

	}

	private int insertCttvHeader(Sheet sheet, int index, List<BvlBill> bills) {
		int rowIndex = index;
		Row row = null;
		Cell c = null;
		rowIndex++;
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Tổng số giao dịch");
		c.setCellStyle(csTopLeft);

		c = row.createCell(1);
		String transAmount = null;
		transAmount = String.format("%,3d%n", getCttcTransAmount(bills));
		c.setCellValue(transAmount.trim());
		c.setCellStyle(csTopRight);

		rowIndex += 2;
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Tổng tiền");
		c.setCellStyle(csBottomLeft);

		c = row.createCell(1);
		c.setCellValue(this.getCttvInvoiceAmount(bills));
		c.setCellStyle(csBottomRight);

		if (!Common.DEBUG)
			Common.print("insertCttvHeader()", "Create Header");
		rowIndex = rowIndex++;
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellStyle(csBold);
		c.setCellValue("Ngày sổ phụ");

		c = row.createCell(1);
		c.setCellValue("Ngày nộp tiền");
		c.setCellStyle(csBold);

		c = row.createCell(2);
		c.setCellValue("Mã công ty");
		c.setCellStyle(csBold);

		c = row.createCell(3);
		c.setCellValue("Tên công ty");
		c.setCellStyle(csBold);

		c = row.createCell(4);
		c.setCellValue("Mã TVV thu phí");
		c.setCellStyle(csBold);

		c = row.createCell(5);
		c.setCellValue("Tên khách hàng");
		c.setCellStyle(csBold);

		c = row.createCell(6);
		c.setCellValue("Số tiền");
		c.setCellStyle(csBold);

		c = row.createCell(7);
		c.setCellValue("Số ấn chỉ");
		c.setCellStyle(csBold);

		c = row.createCell(8);
		c.setCellValue("Số hợp đồng/số GYC");
		c.setCellStyle(csBold);

		c = row.createCell(9);
		c.setCellValue("Từ ngày");
		c.setCellStyle(csBold);

		c = row.createCell(10);
		c.setCellValue("Đến ngày");
		c.setCellStyle(csBold);

		c = row.createCell(11);
		c.setCellValue("Mã giao dịch");
		c.setCellStyle(csBold);

		c = row.createCell(12);
		c.setCellValue("Loại giao dịch");
		c.setCellStyle(csBold);
		c = row.createCell(13);
		c.setCellValue("Nguồn dữ liệu");
		c.setCellStyle(csBold);

		c = row.createCell(14);
		c.setCellValue("Kênh thu phí");
		c.setCellStyle(csBold);

		c = row.createCell(15);
		c.setCellValue("Số giao dịch");
		c.setCellStyle(csBold);

		c = row.createCell(16);
		c.setCellValue("Điểm giao dịch");
		c.setCellStyle(csBold);

		return rowIndex;
	}

	private int insertCttvBody(Sheet sheet, int index, List<BvlBill> bills) {
		int rowIndex = index;
		Row row = null;
		Cell c = null;
		BvlCompanyDAO bvlDAO = new BvlCompanyDAO();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String temp = null;
		rowIndex++;
		if (!Common.DEBUG)
			Common.print("insertCttvBody()", "Amount: " + bills.size());
		for (BvlBill bill : bills) {
			rowIndex++;
			row = sheet.createRow(rowIndex);
			c = row.createCell(0);
			if (bill.getBbiEffectiveDate() == null)
				temp = "";
			else
				temp = simpleDateFormat.format(bill.getBbiEffectiveDate());
			c.setCellValue(temp);
			c.setCellStyle(csBold);

			c = row.createCell(1);
			if (bill.getBbiFeeDate() == null)
				temp = "";
			else
				temp = simpleDateFormat.format(bill.getBbiFeeDate());

			c.setCellValue(temp);
			c.setCellStyle(csBold);

			c = row.createCell(2);
			c.setCellValue(bill.getBbiCompanyCode());
			c.setCellStyle(csBold);

			c = row.createCell(3);
			temp = bvlDAO.getBCOBankAccount(bill.getBbiCompanyCode());
			c.setCellValue(temp);
			c.setCellStyle(csBold);

			c = row.createCell(4);
			c.setCellValue(bill.getBbiPaymentOutlet());
			c.setCellStyle(csBold);

			c = row.createCell(5);
			c.setCellValue(bill.getBbiPolicyholder());
			c.setCellStyle(csBold);

			c = row.createCell(6);
			c.setCellValue(bill.getBbiInvoiceAmount());
			c.setCellStyle(csBold);

			c = row.createCell(7);
			c.setCellValue(bill.getBbiInvoiceType());
			c.setCellStyle(csBold);

			c = row.createCell(8);
			c.setCellValue(bill.getBbiAccountNumber());
			c.setCellStyle(csBold);

			c = row.createCell(9);
			if (bill.getBbiFromDate() == null)
				temp = "";
			else
				temp = simpleDateFormat.format(bill.getBbiFromDate());
			c.setCellValue(temp);
			c.setCellStyle(csBold);

			c = row.createCell(10);
			if (bill.getBbiFeeDate() == null)
				temp = "";
			else
				temp = simpleDateFormat.format(bill.getBbiToDate());
			c.setCellValue(temp);
			c.setCellStyle(csBold);

			c = row.createCell(11);
			c.setCellValue(bill.getId().getBbiBarcode());
			c.setCellStyle(csBold);

			c = row.createCell(12);
			c.setCellValue(bill.getBbiPaymentInstruction());
			c.setCellStyle(csBold);

			c = row.createCell(13);
			c.setCellValue(bill.getBbiDataSource());
			c.setCellStyle(csBold);

			c = row.createCell(14);
			c.setCellValue(bill.getBbiChannelFreePerform());
			c.setCellStyle(csBold);

			c = row.createCell(15);
			c.setCellValue(bill.getBbiTransactionId());
			c.setCellStyle(csBold);

			c = row.createCell(16);
			c.setCellValue(bill.getBbiTransactionCoCode());
			c.setCellStyle(csBold);

		}
		return rowIndex;
	}

	private int insertBvntHeader(Sheet sheet, int index, List<Bvnt> bvnts) {
		int rowIndex = index;
		Row row = null;
		Cell c = null;
		rowIndex++;
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Tổng giao dịch");
		c.setCellStyle(csTopLeft);

		c = row.createCell(1);
		String transAmount = String.format("%,3d%n", getBvntTransAmount(bvnts));
		c.setCellValue(transAmount.trim());
		c.setCellStyle(csTopRight);

		rowIndex++;
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Tổng tiền");
		c.setCellStyle(csBottomLeft);
		c = row.createCell(1);
		c.setCellValue(getBvntInvoiceAmount(bvnts));
		c.setCellStyle(csBottomRight);

		rowIndex = rowIndex + 3;
		row = sheet.createRow(rowIndex);
		c = row.createCell(0);
		c.setCellValue("Ngày giao dịch");
		c.setCellStyle(csBold);
		c = row.createCell(1);
		c.setCellValue("Mã công ty");
		c.setCellStyle(csBold);
		c = row.createCell(2);
		c.setCellValue("Tên công ty");
		c.setCellStyle(csBold);
		c = row.createCell(3);
		c.setCellValue("Tổng số giao dịch");
		c.setCellStyle(csBold);
		c = row.createCell(4);
		c.setCellValue("Tổng số tiền");
		c.setCellStyle(csBold);

		return rowIndex;
	}

	private int insertBvntBody(Sheet sheet, int index, List<Bvnt> bvnts) {
		int rowIndex = index;
		Row row = null;
		Cell c = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String date = null;
		for (Bvnt bvnt : bvnts) {
			rowIndex++;
			row = sheet.createRow(rowIndex);
			c = row.createCell(0);
			date = simpleDateFormat.format(bvnt.getDate());
			c.setCellValue(date);
			c.setCellStyle(cs);
			c = row.createCell(1);
			c.setCellValue(bvnt.getCompanyCode());
			c.setCellStyle(cs);
			c = row.createCell(2);
			c.setCellValue(bvnt.getCompanyName());
			c.setCellStyle(cs);
			c = row.createCell(3);
			String transAmount = String.format("%,3d%n", bvnt.getTransAmount());
			c.setCellValue(transAmount.trim());
			c.setCellStyle(cs);
			c = row.createCell(4);
			c.setCellValue(bvnt.getMoneyAmount());
			c.setCellStyle(cs);
		}

		return rowIndex;
	}

	private int getCttcTransAmount(List<BvlBill> bills) {
		int sum = 0;
		bills.sort(new BvlBill());
		String temp = bills.get(0).getBbiTransactionId();
		for (BvlBill bill : bills) {
			if (bill.getBbiTransactionId() != null && !temp.equals(bill.getBbiTransactionId())) {
				sum++;
			}
			temp = bill.getBbiTransactionId();
		}
		return sum;
	}

	private double getCttvInvoiceAmount(List<BvlBill> bills) {
		double sum = 0;
		for (BvlBill bill : bills) {
			sum += bill.getBbiInvoiceAmount();
		}
		return sum;
	}

	private int getBvntTransAmount(List<Bvnt> bvnts) {
		int sum = 0;
		for (Bvnt bvnt : bvnts) {
			sum += bvnt.getTransAmount();
		}
		return sum;
	}

	private double getBvntInvoiceAmount(List<Bvnt> bvnts) {
		double sum = 0;
		for (Bvnt bvnt : bvnts) {
			sum += bvnt.getMoneyAmount();
		}
		return sum;
	}

}