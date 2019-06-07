package ru.landar.spring.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List; 
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

public class DocxServiceImpl {
	public XWPFDocument openDocument(String filePath) throws IOException {
		return new XWPFDocument(new FileInputStream(filePath));
	}
	public XWPFDocument openDocument(InputStream is) throws IOException {
		return new XWPFDocument(is);
	}
	public XWPFTable[] tableContains(XWPFDocument docx, String[] texts) {
		List<XWPFTable> arr = new ArrayList<XWPFTable>();
		List<XWPFTable> tables = docx.getTables();
        for (int n=0; n<tables.size(); n++)
        {
            XWPFTable table = tables.get(n);
            if (arr.contains(table)) continue;
            List<XWPFTableRow> tableRows = table.getRows();
            for (int i=0; i<tableRows.size(); i++) {
            	if (arr.contains(table)) break;
                XWPFTableRow tableRow = tableRows.get(i);
                List<XWPFTableCell> tableCells = tableRow.getTableCells();
                for (int j=0; j<tableCells.size(); j++)
                {
                	if (arr.contains(table)) break;
                    XWPFTableCell tableCell = tableCells.get(j);
                    String t = tableCell.getText();
                    if (t != null)
                    {
                    	int c = 0;
                    	for (String text : texts) if (t.contains(text)) c++;
                    	if (c == texts.length) arr.add(table); 
                    }
                }
            }
        }
		return arr.toArray(new XWPFTable[arr.size()]);
	}
	public void addRow(XWPFTable table, String key, Map<String, String> mapData) {
		if (!key.startsWith("[")) key = "[" + key;
		if (!key.endsWith("]")) key += "]";
		XWPFTableRow tableRow = findRow(table, key);
		if (tableRow == null) return;
		mapData.put(key, "");
		CTRow ctRow = CTRow.Factory.newInstance();
		ctRow.set(tableRow.getCtRow());
		XWPFTableRow row2 = new XWPFTableRow(ctRow, table);
		List<XWPFTableCell> tableCells = row2.getTableCells();
		for (int j=0; j<tableCells.size(); j++) {
			XWPFTableCell tableCell = tableCells.get(j);
			List<XWPFParagraph> lp = tableCell.getParagraphs();
		    for (int i=0; i<lp.size(); i++) replace(lp.get(i), mapData);
		}
		table.addRow(row2);
	}
	public XWPFTableRow findRow(XWPFTable table, String key) {
		XWPFTableRow ret = null;
		List<XWPFTableRow> tableRows = table.getRows();
        for (int i=0; i<tableRows.size(); i++) {
        	if (ret != null) break;
        	 XWPFTableRow tableRow = tableRows.get(i);
             List<XWPFTableCell> tableCells = tableRow.getTableCells();
             for (int j=0; j<tableCells.size(); j++) {
            	 if (ret != null) break;
            	 XWPFTableCell tableCell = tableCells.get(j);
            	 String t = tableCell.getText();
            	 if (t != null && t.contains(key)) ret = tableRow;
             }
        }
		return ret;
	}
	public void deleteRows(XWPFTable table, List<String> arr) {
		List<XWPFTableRow> tableRows = table.getRows();
        for (int i=0; i<tableRows.size(); ) {
        	boolean remove = false;
        	XWPFTableRow tableRow = tableRows.get(i);
        	List<XWPFTableCell> tableCells = tableRow.getTableCells();
        	for (int j=0; j<tableCells.size(); j++) {
        		if (remove) break;
        		XWPFTableCell tableCell = tableCells.get(j);
                String t = tableCell.getText();
                for (int n=0; n<arr.size(); n++) {
                	if (remove) break;
                	if (t != null && t.contains(arr.get(n))) remove = true;
                }
        	}
        	if (remove) table.removeRow(i);
        	else i++;
        }
	}
	public void replace(XWPFDocument docx, Map<String, String> mapData) {
		docx.getParagraphs().forEach( p -> replace(p, mapData) );;
	}
	public void replace(XWPFParagraph p, Map<String, String> mapData) {
		Iterator<String> it = mapData.keySet().iterator();
    	while (it.hasNext()) {
    		String key = it.next();
    		String text = p.getText();
    		if (text == null || !text.contains(key)) continue;
    		// Изменить текст параграфа
    		String replacedText = StringUtils.replace(text, key, mapData.get(key));
    		XWPFRun r = p.getRuns().get(0);
    		int fontSize = r.getFontSize();
    		String fontFamily = r.getFontFamily();
    		String color = r.getColor();
    		boolean bold = r.isBold(), italic = r.isItalic();
    		// Удалить все фрагменты
    		while (p.getRuns().size() > 0) p.removeRun(0); 
    		// Добавить фрагменты с измененным текстом
    		String[] rs = StringUtils.split(replacedText, "\n");
    	    for (int j=0; j<rs.length; j++) {
    	        XWPFRun nr = p.insertNewRun(j);
    	        nr.setText(rs[j]);
    	        nr.setFontSize(fontSize);
    	        nr.setFontFamily(fontFamily);
    	        nr.setBold(bold);
    	        nr.setItalic(italic);
    	        nr.setColor(color);;
    	        if (j < rs.length - 1) nr.addCarriageReturn();
    	    }
    	}
	}
}
