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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XlsServiceImpl implements XlsService {
	@Autowired
	private HelperService hs;
	/**
	 * Получение книги из потока
	 * @param is
	 * @return
	 */
	public Workbook loadWorkbook(InputStream is) throws Exception {
		return WorkbookFactory.create(is);
	}
	/**
	 * Получение высоты строки в зависимости от текста и ширины ячейки
	 * @param row
	 * @param c
	 * @param width
	 * @param cf
	 * @param rh
	 * @return
	 */
	public int getCellHeight(HSSFRow row, int c, Integer width, java.awt.Font cf, Integer rh) {
		HSSFCell cell = row.getCell(c);
		if (cell == null) return -1;
		String value = cell.toString();
		if (hs.isEmpty(value)) return -1;
		java.awt.Font currFont = cf;
		if (currFont == null) currFont = getCellFont(cell);
		if (currFont == null) currFont = getWorkbookFont(cell.getSheet().getWorkbook());
		AttributedString attrStr = new AttributedString(value);
		attrStr.addAttribute(TextAttribute.FAMILY, currFont.getFontName());
		attrStr.addAttribute(TextAttribute.SIZE, (float)currFont.getSize2D());
		if (currFont.isBold()) attrStr.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
		if (currFont.isItalic()) attrStr.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
		FontRenderContext frc = new FontRenderContext(null, true, true);
		double rowHeight = (currFont.getSize() + 3.25) * 20.;
		double w = width != null ? width : (getCellWidth(cell, true) - cell.getCellStyle().getIndention()) * 0.7;
		LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
		int nextPos = 0, lineCnt = 0;
		while (measurer.getPosition() < value.length()) {
			nextPos = measurer.nextOffset((float)w);
			lineCnt++;
			measurer.setPosition(nextPos);
		}
		if (nextPos < value.length()) lineCnt++;
		return rh != null ? rh * lineCnt  : (int)(rowHeight * lineCnt);
	}
	public int getCellHeight(HSSFRow row, int c, Integer width, java.awt.Font cf) {
		return getCellHeight(row, c, width, cf, null);
	}
	/**
	 * Расчет ширины ячейки
	 * @param cell
	 * @param merged
	 * @return
	 */
	public double getCellWidth(HSSFCell cell, boolean merged) {
		int row = cell.getRowIndex(), col = cell.getColumnIndex();
		HSSFSheet sheet = cell.getSheet();
		if (sheet.isColumnHidden(col) || sheet.isColumnBroken(col)) return 0;
		java.awt.Font currFont = getWorkbookFont(cell.getSheet().getWorkbook());
		FontRenderContext frc = new FontRenderContext(null, true, true);
		double charWidth = currFont.getStringBounds("0", frc).getWidth();
		boolean found = false;
		double ret = 0.;
		if (merged) {
			int minColumn = -1, maxColumn = -1;
			for (int i=0; i<sheet.getNumMergedRegions(); i++) {
	            CellRangeAddress c = sheet.getMergedRegion(i);
	            if (col >= c.getFirstColumn() && col <= c.getLastColumn() && row >= c.getFirstRow() && row <= c.getLastRow()) {
	            	for (int j=c.getFirstColumn(); j<=c.getLastColumn(); j++) {
						if (minColumn < 0 || j < minColumn) minColumn = j;
						if (maxColumn < 0 || j > maxColumn) maxColumn = j;
	            		if (j == col) found = true;
	            	}
	            }
	        }
			if (found && minColumn >= 0) {
				for (int j=minColumn; j<=maxColumn; j++) {
					HSSFCell cellMerged = sheet.getRow(row).getCell(j);
	            	double cw = getCellWidth(cellMerged, false);
	            	ret += cw;
				}
			}
		}
		if (!found) {
			//double colWidth = (double)cell.getSheet().getColumnWidth(col);
			//ret = (colWidth - 5) / 256. * charWidth;
			ret = widthUnits2Pixel(cell.getSheet().getColumnWidth(col));
		}
		return ret;
	}
	private final short EXCEL_COLUMN_WIDTH_FACTOR = 256; 
	private final int UNIT_OFFSET_LENGTH = 7; 
	private final int[] UNIT_OFFSET_MAP = new int[] { 0, 36, 73, 109, 146, 182, 219 }; 
	private int pixel2WidthUnits(int pxs) { 
		int widthUnits = (int)(EXCEL_COLUMN_WIDTH_FACTOR * (pxs / UNIT_OFFSET_LENGTH)); 
		widthUnits += UNIT_OFFSET_MAP[(pxs % UNIT_OFFSET_LENGTH)]; 
		return widthUnits; 
	 } 
	private double widthUnits2Pixel(int widthUnits) { 
		double pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR) * UNIT_OFFSET_LENGTH; 
		double offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR; 
		pixels += Math.round(offsetWidthUnits / (EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH)); 
		return pixels; 
	 } 
	/**
	 * Шрифт по умолчанию в книге
	 * @param wb
	 * @return
	 */
	public java.awt.Font getWorkbookFont(HSSFWorkbook wb) {
		HSSFFont f = wb.getFontAt((short)0);
		if (f == null) return new java.awt.Font("Arial", java.awt.Font.PLAIN, 10);
		String fontName = f.getFontName();
		int fontStyle = 0;
		short bw = f.getBoldweight();
		if (bw == HSSFFont.BOLDWEIGHT_BOLD) fontStyle |= java.awt.Font.BOLD;
		if (f.getItalic()) fontStyle |= java.awt.Font.ITALIC;
		short fontHeight = f.getFontHeightInPoints();
		return new java.awt.Font(fontName, fontStyle, fontHeight);
	}
	/**
	 * Шрифт в ячейке
	 * @param cell
	 * @return
	 */
	public java.awt.Font getCellFont(HSSFCell cell) {
		HSSFFont f = cell.getSheet().getWorkbook().getFontAt(cell.getCellStyle().getFontIndex());
		if (f == null) return null;
		String fontName = f.getFontName();
		int fontStyle = 0;
		short bw = f.getBoldweight();
		if (bw == HSSFFont.BOLDWEIGHT_BOLD) fontStyle |= java.awt.Font.BOLD;
		if (f.getItalic()) fontStyle |= java.awt.Font.ITALIC;
		short fontHeight = f.getFontHeightInPoints();
		return new java.awt.Font(fontName, fontStyle, fontHeight);
	}
	/**
	 * Замена данных в ячейках на листе
	 * @param sheet - лист
	 * @param startRow - стартовая строка (-1 - первая на листе)
	 * @param endRow - последняя строка включительно (-1 - последня на листе) 
	 * @param startColumn - стартовая колонка (-1 - первая на листе)
	 * @param endColumn - конечная колонка (-1 - последняя на листе)
	 * @param mapValue  - именованный массив данных (переменная - значение) 
	 */
	public void replaceValue(HSSFSheet sheet, int startRow, int endRow, int startColumn, int endColumn, Map<String, Object> mapValue) {
		replaceValue(sheet, startRow, endRow, startColumn, endColumn, mapValue, true);
	}
	public void replaceValue(HSSFSheet sheet, int startRow, int endRow, int startColumn, int endColumn, Map<String, Object> mapValue, boolean clear) {
		if (startRow < 0) startRow = sheet.getFirstRowNum();
		if (endRow < 0) endRow = sheet.getLastRowNum();
		for (int i=startRow; i<=endRow; i++)
		{
			HSSFRow row = sheet.getRow(i);
			if (row == null) continue;
			int sC = startColumn, eC = endColumn;
			if (sC < 0) sC = row.getFirstCellNum();
			if (eC < 0) eC = row.getLastCellNum();
			for (int j=sC; j<=eC; j++) {
				HSSFCell cell = row.getCell(j);
				if (cell == null) continue;
				String v = cell.toString();
				if (v.indexOf('{') < 0 || v.indexOf('}') < 0) continue;
				boolean formula = v.startsWith("=");
				if (formula) v = v.substring(1);
				Object oValue = null;
				String sValue = "", t = v;
				int s = 0;
				for (; ;) {
					int k = v.indexOf("{", s);
					if (k < 0) {
						if (s < v.length()) sValue += t.substring(s);
						break;
					}
					else {
						if (k > s) sValue += t.substring(s, k); 
						int e = t.indexOf("}", k + 1);
						if (e < 0) {
							sValue += t.substring(k);
							break;
						}
						// Выделили атрибут в фигурных скобках
						String attr = t.substring(k + 1, e);
						// Заменить данные
						Object o = mapValue.get(attr);
						if (!clear && !mapValue.containsKey(attr)) o = "{" + attr + "}";
						if (o != null) {
							if (k == 0 && e == v.length() - 1) {
								oValue = o;
								break;
							}
							sValue += hs.getObjectString(o);
						}
						// Передвинуться дальше
						s = e + 1;
					}
				}
				if (oValue == null) oValue = sValue;
				if (formula) setFormulaValue(row, j, sValue);
				else setCellValue(row, j, oValue == null ? sValue: oValue);
			}
		}
	}
	/**
	 * Сохранение формулы
	 * @param row
	 * @param c
	 * @param value
	 */
	public void setFormulaValue(HSSFRow row, int c, String value) {
		if (row == null || value == null) return;
		HSSFCell cell = row.getCell(c);
		if (cell == null) return;
		cell.setCellFormula(value);
	}
	/**
	 * Сохранение данных в ячейке по символическому адресу
	 * @param sheet
	 * @param addr
	 * @param value
	 */
	public void setCellValue(HSSFSheet sheet, String addr, Object value) { setCellValue(getCell(sheet, addr), value); }
	/**
	 * Сохранение данных в ячейке по строке и номеру ячейки
	 * @param row
	 * @param c
	 * @param value
	 */
	public void setCellValue(HSSFRow row, int c, Object value) { if (row != null) setCellValue(row.getCell(c), value); }
	/**
	 * Сохранение данных в ячейке
	 * @param cell
	 * @param value
	 */
	public void setCellValue(HSSFCell cell, Object value) {
		if (cell == null || value == null) return;
		if (value instanceof String) {
			cell.setCellType(Cell.CELL_TYPE_STRING);
			cell.setCellValue((String)value);
		}
		else if (value instanceof Date) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime((Date)value);
			cell.setCellValue(calendar);
		}
		else if (value instanceof Integer) {
			Integer i = (Integer)value;
			cell.setCellValue(i);
		}
		else if (value instanceof BigDecimal) {
			BigDecimal bd = (BigDecimal)value;
			cell.setCellValue(bd.doubleValue());
		}
	}
	/**
	 * Получение ячейки по символическому адресу
	 * @param sheet
	 * @param addr
	 * @return
	 */
	public HSSFCell getCell(HSSFSheet sheet, String addr) {
		CellReference cr = new CellReference(addr);
		HSSFRow row = sheet.getRow(cr.getRow());
		if (row == null) return null;
		HSSFCell cell = row.getCell((int)cr.getCol());
		return cell;
	}
	/**
	 * Получение строки по символическому адресу
	 * @param sheet
	 * @param addr
	 * @return
	 */
	public HSSFRow getRow(HSSFSheet sheet, String addr) {
		CellReference cr = new CellReference(addr);
		return sheet.getRow(cr.getRow());
	}
	/**
	 * Копирование строки
	 * @param rowSource
	 * @param rowTarget
	 */
	public void copyRow(HSSFRow rowSource, HSSFRow rowTarget) {
		for (int i=rowSource.getFirstCellNum(); i<rowSource.getLastCellNum(); i++) {
            HSSFCell oldCell = rowSource.getCell(i);
            HSSFCell newCell = rowTarget.createCell(i);
            if (oldCell == null) {
                newCell = null;
                continue;
            }
            newCell.setCellStyle(oldCell.getCellStyle());
            if (oldCell.getCellComment() != null) newCell.setCellComment(oldCell.getCellComment());
            if (oldCell.getHyperlink() != null) newCell.setHyperlink(oldCell.getHyperlink());
            newCell.setCellType(oldCell.getCellType());
            switch (oldCell.getCellType()) {
                case Cell.CELL_TYPE_BLANK:
                    newCell.setCellValue(oldCell.getStringCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    newCell.setCellValue(oldCell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_ERROR:
                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    newCell.setCellFormula(oldCell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    newCell.setCellValue(oldCell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    newCell.setCellValue(oldCell.getRichStringCellValue());
                    break;
            }
        }
		HSSFSheet sheet = rowSource.getSheet();
		for (int i= 0; i<sheet.getNumMergedRegions(); i++) {
            CellRangeAddress cellRangeAddress = sheet.getMergedRegion(i);
            if (cellRangeAddress.getFirstRow() == rowSource.getRowNum()) {
                CellRangeAddress newCellRangeAddress = new CellRangeAddress(
                		rowTarget.getRowNum(),
                        (rowTarget.getRowNum() + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow())),
                        cellRangeAddress.getFirstColumn(),
                        cellRangeAddress.getLastColumn());
                sheet.addMergedRegion(newCellRangeAddress);
            }
        }
		rowTarget.setHeight(rowSource.getHeight());
	}
	/**
	 * Получение адреса в виде строки
	 * @param row
	 * @param column
	 * @return
	 */
	public String getCellAddress(int row, int column) {
		return String.format("%s%d", CellReference.convertNumToColString(column), row + 1);
	} 
	/**
	 * Создание новой строки
	 * @param sheet
	 * @param row
	 * @return
	 */
	public HSSFRow createRow(HSSFSheet sheet, int row) {
		HSSFRow newRow = sheet.getRow(row);
        if (newRow != null) {
        	List<Short> l = new ArrayList<Short>();
        	for (int i=row; i<=sheet.getLastRowNum(); i++) {
        		HSSFRow r = sheet.getRow(i);
        		l.add(r != null ? r.getHeight() : null);
    		}
        	sheet.shiftRows(row, sheet.getLastRowNum(), 1);
        	for(int i=0; i<l.size(); i++) {
	        	HSSFRow r = sheet.getRow(row + i + 1);
	        	Short h = l.get(i);
	        	if (r != null && h != null) {
	        		r.setHeight(h);
	        	}
        	}
        	newRow = sheet.getRow(row);
        }
        else newRow = sheet.createRow(row);
        return newRow;
	}
	/**
	 * Удаление строки на листе
	 * @param sheet
	 * @param rowIndex
	 */
	public void removeRow(HSSFSheet sheet, int rowIndex) {
	    int lastRowNum = sheet.getLastRowNum();
	    if (rowIndex >= 0 && rowIndex < lastRowNum) {
	    	List<Short> l = new ArrayList<Short>();
        	for (int i=rowIndex+1; i<=sheet.getLastRowNum(); i++) {
        		HSSFRow r = sheet.getRow(i);
        		l.add(r != null ? r.getHeight() : null);
    		}
	    	sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
	    	for(int i=0; i<l.size(); i++) {
	        	HSSFRow r = sheet.getRow(rowIndex + i);
	        	Short h = l.get(i);
	        	if (r != null && h != null) {
	        		r.setHeight(h);
	        	}
        	}
	    }
	    if (rowIndex == lastRowNum) {
	        HSSFRow removingRow = sheet.getRow(rowIndex);
	        if (removingRow != null) sheet.removeRow(removingRow);
	    }
	}
	/**
	 * Найти строку на листе, содержащую указанный текст
	 * @param sheet - лист
	 * @param v - текст для поиска
	 * @return - номер строки или -1
	 */
	public int findRow(HSSFSheet sheet, String v) {
		if (sheet == null || hs.isEmpty(v)) return -1;
		for (int i=sheet.getFirstRowNum(); i<=sheet.getLastRowNum(); i++)
		{
			HSSFRow row = sheet.getRow(i);
			if (row == null) continue;
			for (int j=row.getFirstCellNum(); j<=row.getLastCellNum(); j++) {
				HSSFCell cell = row.getCell(j);
				if (cell == null) continue;
				String t = cell.toString();
				if (hs.isEmpty(t)) continue;
				if (t.indexOf(v) >= 0) return i;
			}
		}
		return -1;
	}
	/**
	 * Найти колонку в строке, содержащую указанный текст
	 * @param row - строка
	 * @param v - текст для поиска
	 * @return - номер колонки или -1
	 */
	public int findColumn(HSSFRow row, String v) {
		if (row == null || hs.isEmpty(v)) return -1;
		for (int i=row.getFirstCellNum(); i<=row.getLastCellNum(); i++) {
			HSSFCell cell = row.getCell(i);
			if (cell == null) continue;
			String t = cell.toString();
			if (hs.isEmpty(t)) continue;
			if (t.indexOf(v) >= 0) return i;
		}
		return -1;
	}
	/**
	 * Найти строку и колонку на листе, содержащую указанный текст
	 * @param sheet - лист
	 * @param v - текст для поиска
	 * @return - номер строки или -1
	 */
	public int[] findCell(HSSFSheet sheet, String v) {
		int r = findRow(sheet, v), c = -1;
		if (r >= 0) c = findColumn(sheet.getRow(r), v);
		return new int[]{r, c};
	}
	/**
	 * Установка высоты строки
	 * @param sheet
	 * @param rowIndex
	 * @param column
	 */
	public void setAutoHeight(HSSFSheet sheet, int rowIndex, int columnIndex, Integer rh) {
		HSSFRow row = sheet.getRow(rowIndex);
		if (row == null) return;
		int newHeight = getCellHeight(row, columnIndex, null, null, rh), oldHeight = row.getHeight();
		int minRow = -1, maxRow = -1;
		for (int i=0; i<sheet.getNumMergedRegions(); i++) {
            CellRangeAddress c = sheet.getMergedRegion(i);
            if (rowIndex >= c.getFirstRow() && rowIndex <= c.getLastRow()) {
            	for (int j=c.getFirstColumn(); j<=c.getLastColumn(); j++) {
            		if (j == columnIndex) {
            			minRow = c.getFirstRow();
            			maxRow = c.getLastRow();
            		}
            	}
            }
        }
		if (minRow >= 0 && maxRow >= 0) {
			oldHeight = 0;
			for (int i=minRow; i<=maxRow; i++) oldHeight += row.getHeight();
		}
		if (newHeight > 0 && newHeight > oldHeight) {
			if (minRow >= 0 && maxRow >= 0) {
				for (int i=minRow; i<=maxRow; i++) {
					HSSFRow rowT = sheet.getRow(i);
					if (rowT == null) continue;
					rowT.setHeight((short)((double)newHeight / (maxRow - minRow + 1) + 1.));
				}
			}
			else row.setHeight((short)newHeight);
		}
	}
	public void setAutoHeight(HSSFSheet sheet, int rowIndex, int columnIndex) {
		setAutoHeight(sheet, rowIndex, columnIndex, null);
	}
}
