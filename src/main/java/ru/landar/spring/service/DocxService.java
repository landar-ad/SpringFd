package ru.landar.spring.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public interface DocxService {
	XWPFDocument openDocument(String filePath) throws IOException;
	XWPFDocument openDocument(InputStream is) throws IOException;
	XWPFTable[] tableContains(XWPFDocument docx, String[] texts);
	void addRow(XWPFTable table, String key, Map<String, String> mapData);
	XWPFTableRow findRow(XWPFTable table, String key);
	void deleteRows(XWPFTable table, String[] arr);
	void replace(XWPFDocument docx, Map<String, String> mapData);
	void replace(XWPFParagraph p, Map<String, String> mapData);
}
