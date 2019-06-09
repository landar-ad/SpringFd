package ru.landar.spring.service;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

public interface XlsService {
	void copyRow(HSSFRow rowSource, HSSFRow rowTarget);
	HSSFRow createRow(HSSFSheet sheet, int row);
	int[] findCell(HSSFSheet sheet, String v);
	int findColumn(HSSFRow row, String v);
	int findRow(HSSFSheet sheet, String v);
	HSSFCell getCell(HSSFSheet sheet, String addr);
	String getCellAddress(int row, int column);
	java.awt.Font getCellFont(HSSFCell cell);
	int getCellHeight(HSSFRow row, int c, Integer width, java.awt.Font cf);
	int getCellHeight(HSSFRow row, int c, Integer width, java.awt.Font cf, Integer rh);
	double getCellWidth(HSSFCell cell, boolean merged);
	HSSFRow getRow(HSSFSheet sheet, String addr);
	java.awt.Font getWorkbookFont(HSSFWorkbook wb);
	Workbook loadWorkbook(InputStream is) throws Exception;
	void removeRow(HSSFSheet sheet, int rowIndex);
	void replaceValue(HSSFSheet sheet, int startRow, int endRow, int startColumn, int endColumn, Map<String, Object> mapValue);
	void replaceValue(HSSFSheet sheet, int startRow, int endRow, int startColumn, int endColumn, Map<String, Object> mapValue, boolean clear);
	void setAutoHeight(HSSFSheet sheet, int rowIndex, int columnIndex);
	void setAutoHeight(HSSFSheet sheet, int rowIndex, int columnIndex, Integer rh);
	void setCellValue(HSSFCell cell, Object value);
	void setCellValue(HSSFRow row, int c, Object value);
	void setCellValue(HSSFSheet sheet, String addr, Object value);
	void setFormulaValue(HSSFRow row, int c, String value);
}
