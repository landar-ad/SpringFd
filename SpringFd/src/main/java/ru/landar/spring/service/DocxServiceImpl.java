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
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.springframework.stereotype.Service;

@Service
public class DocxServiceImpl implements DocxService {
	@Override
	public XWPFDocument openDocument(String filePath) throws IOException {
		return new XWPFDocument(new FileInputStream(filePath));
	}
	@Override
	public XWPFDocument openDocument(InputStream is) throws IOException {
		return new XWPFDocument(is);
	}
	@Override
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
	@Override
	public void addRow(XWPFTable table, String key, Map<String, String> mapData) {
		if (!key.startsWith("{")) key = "{" + key;
		if (!key.endsWith("}")) key += "}";
		XWPFTableRow tableRow = findRow(table, key);
		if (tableRow == null) return;
		mapData.put(key, "");
		CTRow ctRow = CTRow.Factory.newInstance();
		ctRow.set(tableRow.getCtRow());
		XWPFTableRow row2 = new XWPFTableRow(ctRow, table);
		for (XWPFTableCell tableCell : row2.getTableCells())
		    for (XWPFParagraph p : tableCell.getParagraphs()) replace(p, mapData);
		table.addRow(row2);
	}
	@Override
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
	@Override
	public void deleteRows(XWPFTable table, String[] arr) {
		if (arr == null || arr.length < 1) return;
		List<XWPFTableRow> tableRows = table.getRows();
        for (int i=0; i<tableRows.size(); ) {
        	boolean remove = false;
        	XWPFTableRow tableRow = tableRows.get(i);
        	for (XWPFTableCell tableCell : tableRow.getTableCells()) {
        		if (remove) break;
                String t = tableCell.getText();
                for (String a : arr) {
                	if (remove) break;
                	if (t != null && t.contains(a)) remove = true;
                }
        	}
        	if (remove) table.removeRow(i); else i++;
        }
	}
	@Override
	public void replace(XWPFDocument docx, Map<String, String> mapData) {
		docx.getParagraphs().forEach(p -> replace(p, mapData));
	}
	@Override
	public void replace(XWPFParagraph p, Map<String, String> mapData) {
		String text = p.getText();
		if (text == null || text.isEmpty()) return;
		boolean changed = false;
		Iterator<String> it = mapData.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next(), r = mapData.get(key);
			if (r == null) r = "";
    		if (!text.contains(key)) continue;
    		text = StringUtils.replace(text, key, r);
    		changed = true;
		}
		if (!changed) return;
		for (int j=p.getRuns().size() - 1; j>0; j--) 
			p.removeRun(j);
		p.getRuns().get(0).setText(text, 0);
	}
}
