package ru.landar.spring.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.model.Act;
import ru.landar.spring.model.Act_document;
import ru.landar.spring.model.Document;
import ru.landar.spring.model.IFile;
import ru.landar.spring.service.DocxService;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;

@Controller
public class PrintController {
	@Autowired
	private HelperService hs;
	@Autowired
	private DocxService d;
	@Autowired
	private ObjService objService;
	
	@RequestMapping(value = "/printAct", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> printAct(@RequestParam Integer rn) throws Exception {
	    // Акт
		Act act = (Act)objService.find(Act.class, rn);
		if (act == null) throw new Exception("Не найден акт приема-передачи по идентификатору " + rn);
		// Шаблон
		IFile f = (IFile)objService.find(IFile.class, "filename", "Акт приема-передачи.docx");
		if (f == null || hs.isEmpty(f.getFileuri())) throw new Exception("Не найден шаблон акта приема-передачи");
		File file = new File(f.getFileuri());
		if (!file.exists()) throw new Exception("Не найден файл акта приема-передачи на сервере");
		// Обработка шаблона
		Map<String, String> mapData = new HashMap<String, String>();
		XWPFDocument docx = d.openDocument(f.getFileuri());
		XWPFTable[] ts = d.tableContains(docx, new String[] {"list_doc"});
		if (ts == null || ts.length < 1) throw new Exception("Некорректный шаблон акта приема-передачи");
		// Обработка списка документов
		int i = 1;
		XWPFTable table = ts[0];
		if (act.getList_doc() != null) {
			for (Act_document act_doc : act.getList_doc()) {
				Document doc = act_doc.getDoc();
				if (doc == null) continue;
				i++;
				mapData.clear();
				mapData.put("{#}", "" + i);
				mapData.put("{list_doc}", "");
				mapData.put("{doc_type__name}", hs.getPropertyString(doc, "doc_type__name"));
				mapData.put("{doc_number}", hs.getPropertyString(doc, "doc_number"));
				mapData.put("{doc_date}", hs.getPropertyString(doc, "doc_date"));
				d.addRow(table, "{list_doc}", mapData);
			}
			d.deleteRows(table, new String[] {"list_doc"});
		}
		// Общие атрибуты
		mapData.put("{act_number}", hs.getPropertyString(act, "act_number"));
		mapData.put("{act_date}", hs.getPropertyString(act, "act_date"));
		mapData.put("{depart__name}", hs.getPropertyString(act, "depart__name"));
		mapData.put("{create_agent__post}", hs.getPropertyString(act, "create_agent__post"));
		mapData.put("{create_agent__name}", hs.getPropertyString(act, "create_agent__name"));
		mapData.put("{create_agent__phone}", hs.getPropertyString(act, "create_agent__phone"));
		mapData.put("{create_agent__email}", hs.getPropertyString(act, "create_agent__email"));
		d.replace(docx, mapData);
		// Вывод данных в память
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		docx.write(out);
		out.close();
		// Отправка данных пользователю
		InputStreamResource isr = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
		String ext = f.getFileext();
		if (hs.isEmpty(ext)) ext = "docx";
		String fileName = act.getName() + "." + ext;
		Optional<MediaType> mt = MediaTypeFactory.getMediaType(fileName);
		String content = "attachment; filename*=UTF-8''" + URLEncoder.encode(hs.replaceSpecial(fileName), "UTF-8");
		return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, content)
	                .contentType(mt.orElse(MediaType.ALL))
	                .contentLength(file.length())
	                .body(isr);
	}
	@RequestMapping(value = "/printActRet", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> printActRet(@RequestParam Integer rn) throws Exception {
	    // Акт
		Act act = (Act)objService.find(Act.class, rn);
		if (act == null) throw new Exception("Не найден акт приема-передачи по идентификатору " + rn);
		// Шаблон
		IFile f = (IFile)objService.find(IFile.class, "filename", "Акт возврата.docx");
		if (f == null || hs.isEmpty(f.getFileuri())) throw new Exception("Не найден шаблон акта возврата");
		File file = new File(f.getFileuri());
		if (!file.exists()) throw new Exception("Не найден файл акта возврата на сервере");
		// Обработка шаблона
		Map<String, String> mapData = new HashMap<String, String>();
		XWPFDocument docx = d.openDocument(f.getFileuri());
		XWPFTable[] ts = d.tableContains(docx, new String[] {"list_doc"});
		if (ts == null || ts.length < 1) throw new Exception("Некорректный шаблон акта возврата");
		// Обработка списка документов
		int i = 1;
		XWPFTable table = ts[0];
		if (act.getList_doc() != null) {
			for (Act_document act_doc : act.getList_doc()) {
				boolean b = act_doc.getExclude() != null && act_doc.getExclude(); 
				if (!b) continue;
				Document doc = act_doc.getDoc();
				if (doc == null) continue;
				i++;
				mapData.clear();
				mapData.put("{#}", "" + i);
				mapData.put("{list_doc}", "");
				mapData.put("{doc_type__name}", hs.getPropertyString(doc, "doc_type__name"));
				mapData.put("{doc_number}", hs.getPropertyString(doc, "doc_number"));
				mapData.put("{doc_date}", hs.getPropertyString(doc, "doc_date"));
				String reason = hs.getPropertyString(doc, "act_doc.exclude_reason");
				if (hs.isEmpty(reason)) reason = act.getAct_reason();
				mapData.put("{note}", reason);
				d.addRow(table, "{list_doc}", mapData);
			}
			d.deleteRows(table, new String[] {"list_doc"});
		}
		// Общие атрибуты
		mapData.put("{act_number}", hs.getPropertyString(act, "act_number"));
		mapData.put("{act_date}", hs.getPropertyString(act, "act_date"));
		mapData.put("{depart__name}", hs.getPropertyString(act, "depart__name"));
		mapData.put("{create_agent__post}", hs.getPropertyString(act, "create_agent__post"));
		mapData.put("{create_agent__name}", hs.getPropertyString(act, "create_agent__name"));
		mapData.put("{create_agent__phone}", hs.getPropertyString(act, "create_agent__phone"));
		mapData.put("{create_agent__email}", hs.getPropertyString(act, "create_agent__email"));
		d.replace(docx, mapData);
		// Вывод данных в память
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		docx.write(out);
		out.close();
		// Отправка данных пользователю
		InputStreamResource isr = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
		String ext = f.getFileext();
		if (hs.isEmpty(ext)) ext = "docx";
		String fileName = act.getName() + "." + ext;
		Optional<MediaType> mt = MediaTypeFactory.getMediaType(fileName);
		String content = "attachment; filename*=UTF-8''" + URLEncoder.encode(hs.replaceSpecial(fileName), "UTF-8");
		return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, content)
	                .contentType(mt.orElse(MediaType.ALL))
	                .contentLength(file.length())
	                .body(isr);
	}
}
