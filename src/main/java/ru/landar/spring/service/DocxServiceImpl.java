package ru.landar.spring.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
        for (XWPFTable table : docx.getTables()) {
            if (arr.contains(table)) continue;
            for (XWPFTableRow tableRow : table.getRows()) {
            	if (arr.contains(table)) break;
                for (XWPFTableCell tableCell : tableRow.getTableCells()) {
                	if (arr.contains(table)) break;
                    String t = tableCell.getText();
                    if (t == null) continue;
                	int c = 0;
                	for (String text : texts) if (t.contains(text)) c++;
                	if (c == texts.length) arr.add(table); 
                }
            }
        }
		return arr.toArray(new XWPFTable[arr.size()]);
	}
	public void addRow(XWPFTable table, String key, Map<String, String> mapData) {
		if (!key.startsWith("{")) key = "{" + key;
		if (!key.endsWith("}")) key += "}";
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
        for (XWPFTableRow tableRow : table.getRows()) {
        	if (ret != null) break;
         	for (XWPFTableCell tableCell : tableRow.getTableCells()) {
        		if (ret != null) break;
         		String t = tableCell.getText();
        		if (t == null || !t.contains(key)) continue;
        		ret = tableRow;
        	}
        }
		return ret;
	}
	public void deleteRows(XWPFTable table, List<String> arr) {
		if (arr.size() < 1) return;
		List<XWPFTableRow> tableRows = table.getRows();
        for (int i=0; i<tableRows.size(); ) {
        	boolean remove = false;
        	XWPFTableRow tableRow = tableRows.get(i);
        	for (XWPFTableCell tableCell : tableRow.getTableCells()) {
        		if (remove) break;
                String t = tableCell.getText();
                for (int n=0; n<arr.size(); n++) {
                	if (remove) break;
                	if (t != null && t.contains(arr.get(n))) remove = true;
                }
        	}
        	if (remove) table.removeRow(i); else i++;
        }
	}
	public void replace(XWPFDocument docx, Map<String, String> mapData) {
		docx.getParagraphs().forEach(p -> replace(p, mapData));
	}
	public void replace(XWPFParagraph p, Map<String, String> mapData) {
		String text = p.getText();
		if (text == null || text.isEmpty()) return;
		mapData.forEach((key, r) -> {
    		if (!text.contains(key)) return;
    		// Изменить текст параграфа
    		String replacedText = StringUtils.replace(text, key, r);
    		XWPFRun run = p.getRuns().get(0);
    		int fontSize = run.getFontSize();
    		String fontFamily = run.getFontFamily();
    		String color = run.getColor();
    		boolean bold = run.isBold(), italic = run.isItalic();
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
		});
	}
}
