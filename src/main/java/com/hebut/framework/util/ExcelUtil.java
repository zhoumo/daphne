package com.hebut.framework.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.DateFormat;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {

	public static final String USER_VALIDATE_STRING = "ID USERNAME LOGINNAME PASSWORD";

	public static boolean validateExcel(File filePath, int sheetIndex, String validateString) {
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(filePath, encodingSetting());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Sheet sheet = workbook.getSheet(sheetIndex);
		String currentValidateString = "";
		for (int index = 0; index < sheet.getColumns(); index++) {
			currentValidateString += sheet.getCell(index, 0).getContents() + " ";
		}
		if (validateString.equalsIgnoreCase(currentValidateString.trim())) {
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<ArrayList<String>> readExcel(File filePath, int sheetIndex) {
		ArrayList<ArrayList<String>> rowList = new ArrayList<ArrayList<String>>();
		Workbook workbook = null;
		try {
			workbook = Workbook.getWorkbook(filePath, encodingSetting());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Sheet sheet = workbook.getSheet(sheetIndex);
		for (int rowIndex = 0; rowIndex < sheet.getRows(); rowIndex++) {
			ArrayList<String> columnList = new ArrayList<String>();
			for (int columnIndex = 0; columnIndex < sheet.getColumns(); columnIndex++) {
				columnList.add(sheet.getCell(columnIndex, rowIndex).getContents());
			}
			rowList.add(columnList);
		}
		workbook.close();
		return rowList;
	}

	private static WorkbookSettings encodingSetting() {
		WorkbookSettings workbookSettings = new WorkbookSettings();
		workbookSettings.setLocale(new Locale("zh", "CN"));
		workbookSettings.setEncoding("ISO-8859-1");
		return workbookSettings;
	}

	public static void createExcel(String filePath, String sheetName, String[] titleArray, String[][] dataArray) {
		WritableWorkbook workbook;
		try {
			OutputStream outputStream = new FileOutputStream(filePath);
			workbook = Workbook.createWorkbook(outputStream);
			WritableSheet writableSheet = workbook.createSheet(sheetName, 0);
			writableSheetSetting(writableSheet);
			for (int i = 0; i < titleArray.length; i++) {
				writableSheet.addCell(new Label(i, 0, titleArray[i], titleCellFormat()));
			}
			writableSheet.addCell(new Label(0, 1, null, dataCellFormat(CellType.STRING_FORMULA)));
			workbook.write();
			workbook.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writableSheetSetting(WritableSheet writableSheet) {
		try {
			writableSheet.getSettings().setProtected(true);
			writableSheet.getSettings().setDefaultColumnWidth(10);
			writableSheet.setRowView(2, false);
			writableSheet.setColumnView(0, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static WritableCellFormat titleCellFormat() {
		WritableCellFormat writableCellFormat = null;
		try {
			WritableFont writableFont = new WritableFont(WritableFont.TIMES, 12, WritableFont.NO_BOLD, false);
			writableFont.setColour(Colour.RED);
			writableCellFormat = new WritableCellFormat(writableFont);
			writableCellFormat.setAlignment(Alignment.CENTRE);
			writableCellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			writableCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);
			writableCellFormat.setBackground(Colour.GREY_25_PERCENT);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return writableCellFormat;
	}

	private static WritableCellFormat dataCellFormat(CellType cellType) {
		WritableCellFormat writableCellFormat = null;
		try {
			if (cellType == CellType.NUMBER || cellType == CellType.NUMBER_FORMULA) {
				writableCellFormat = new WritableCellFormat(new NumberFormat("#.00"));
			} else if (cellType == CellType.DATE || cellType == CellType.DATE_FORMULA) {
				writableCellFormat = new WritableCellFormat(new DateFormat("yyyy-MM-dd hh:mm:ss"));
			} else {
				writableCellFormat = new WritableCellFormat(new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD, false));
			}
			writableCellFormat.setAlignment(Alignment.CENTRE);
			writableCellFormat.setVerticalAlignment(VerticalAlignment.CENTRE);
			writableCellFormat.setBorder(Border.LEFT, BorderLineStyle.THIN);
			writableCellFormat.setBorder(Border.BOTTOM, BorderLineStyle.THIN);
			writableCellFormat.setBorder(Border.RIGHT, BorderLineStyle.THIN);
			writableCellFormat.setBackground(Colour.WHITE);
			writableCellFormat.setWrap(true);
		} catch (WriteException e) {
			e.printStackTrace();
		}
		return writableCellFormat;
	}
}
