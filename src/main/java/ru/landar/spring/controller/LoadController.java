package ru.landar.spring.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.SpFileType;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;

@Controller
public class LoadController {
	@Autowired
	private ObjService objService;
	@Autowired
	private HelperService hs;
	
	@GetMapping(value = "/import")
	public String importGet(HttpServletRequest request, Model model) throws Exception {
		return "importPage";
	}
	@PostMapping(value = "/import")
	public String importPost(@RequestParam("file") MultipartFile file, 
						@RequestParam("filter") Optional<String> paramFilter, 
						Model model) throws Exception {
		List<String> listFilter = new ArrayList<String>();
		String[] filters = paramFilter.orElse("").split(",");
		for (String filter : filters) if (!filter.trim().isEmpty()) listFilter.add(filter.trim());
		List<String> listAdd = new ArrayList<String>();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file.getInputStream());
		for (Node n=doc.getDocumentElement().getFirstChild(); n!=null; n=n.getNextSibling()) {
			if (n.getNodeType() != Node.ELEMENT_NODE) continue;
			IBase obj = (IBase)createObject((Element)n, listAdd, listFilter);
			if (obj != null) listAdd.add("Добавлен " + obj.getClazz() + " " + obj.getRn() + " " + obj.getName());
		}
		model.addAttribute("datetime", new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()));
    	model.addAttribute("listResult", listAdd);
		return "loadResultPage";
	}
	private Object createObject(Element el, List<String> listAdd, List<String> listFilter) throws Exception {
		String clazz = el.getLocalName();
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) {
			listAdd.add(String.format("Не найден класс объекта %s", clazz));
			return null;
		}
		String code = el.getAttribute("code");
		if (hs.isEmpty(code)) {
			NodeList nl = el.getElementsByTagName("code");
			if (nl != null && nl.getLength() > 0) {
				code = ((Element)nl.item(0)).getTextContent();
			}
		}
		boolean bNew = true;
		Object obj = null;
		if (!hs.isEmpty(code)) obj = objService.getObjByCode(cl, code);
		if (obj != null) { 
			bNew = false; 
			return obj; 
		}
		if (listFilter != null && listFilter.size() > 0 && !hs.isEmpty(code) && listFilter.contains(code)) return null;
		obj = cl.newInstance();
		// Атрибуты
		NamedNodeMap nm = el.getAttributes();
		for (int i=0; nm!=null && i<nm.getLength(); i++) {
			Node attr = nm.item(i);
			String value = attr.getNodeValue(), name = attr.getNodeName();
			if (isIgnoreAttr(name)) continue;
			Class<?> clAttr = hs.getAttrType(cl, name);
			if (clAttr == null) {
				listAdd.add(String.format("Не найден атрибут %s объекта %s", name, clazz));
				continue;
			}
			if (IBase.class.isAssignableFrom(clAttr)) {
				if (!hs.isEmpty(value)) {
					Object o = objService.getObjByCode(clAttr, value);
					if (o != null) hs.setProperty(obj, name, o);
					else listAdd.add(String.format("Не найден объект %s по коду %s", clAttr.getSimpleName(), value));
				}
			}
			else hs.setProperty(obj, name, hs.getObjectByString(cl, name, value));
		}
		// Элементы
		for (Node nChild=el.getFirstChild(); nChild!=null; nChild=nChild.getNextSibling()) {
			if (nChild.getNodeType() != Node.ELEMENT_NODE) continue;
			Element elChild = (Element)nChild;
			String name = elChild.getLocalName();
			if (isIgnoreAttr(name)) continue;
			Class<?> clAttr = hs.getAttrType(cl, name);
			if (clAttr == null) {
				listAdd.add(String.format("Не найден атрибут %s объекта %s", name, clazz));
				continue;
			}
			if (IBase.class.isAssignableFrom(clAttr)) {
				Object child = createObject(elChild, listAdd, null);
				if (child != null) hs.setProperty(obj, name, child);
			}
			else if (List.class.isAssignableFrom(clAttr)) {
				List<Object> l = new ArrayList<Object>();
				for (Node nC=elChild.getFirstChild(); nC!=null; nC=nC.getNextSibling()) {
					if (nC.getNodeType() != Node.ELEMENT_NODE) continue;
					Object child = createObject((Element)nC, listAdd, null);
					if (child != null) l.add(child);
				}
				hs.setProperty(obj, name, l);
			}
			else {
				String value = elChild.getTextContent();
				if (IBase.class.isAssignableFrom(clAttr)) {
					if (!hs.isEmpty(value)) {
						Object o = objService.getObjByCode(clAttr, value);
						if (o != null) hs.setProperty(obj, name, o);
						else listAdd.add(String.format("Не найден объект %s по коду %s", clAttr.getSimpleName(), value));
					}
				}
				else hs.setProperty(obj, name, hs.getObjectByString(cl, name, value));
			}
		}
		if (listFilter != null && listFilter.size() > 0 && !listFilter.contains(hs.getProperty(obj, "code"))) return null;
		if (obj instanceof IFile) {
			IFile f = (IFile)obj;
			String fileext = hs.getPropertyString(obj, "fileext"), filename = hs.getPropertyString(obj, "filename"), name = filename;
			if (!hs.isEmpty(fileext) && !hs.isEmpty(filename)) {
				int k = filename.lastIndexOf('.');
				fileext = k > 0 ? filename.substring(k + 1) : "";
				name = k > 0 ? filename.substring(0, k) : filename;
				f.setFileext(fileext);
			}
			if (!hs.isEmpty(fileext)) {
				SpFileType filetype = (SpFileType)objService.getObjByCode(SpFileType.class, fileext.toLowerCase());
				f.setFiletype(filetype);
			}
			String fileuri = hs.getPropertyString(obj, "fileuri");
			byte[] b = null;
			if (!hs.isEmpty(fileuri)) try { b = Base64.getDecoder().decode(fileuri); } catch (Exception ex) { 
				listAdd.add("Исключение при получении содержимого файла");	
			}
			if (b == null) b = new byte[0];
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			String filesDirectory = (String)objService.getSettings("filesDirectory", "string");
			if (hs.isEmpty(filesDirectory)) filesDirectory = System.getProperty("user.dir") + File.separator + "FILES";
			File fd = new File(filesDirectory + new SimpleDateFormat(".yyyy.MM.dd").format(new Date()).replace('.', File.separatorChar));
			fd.mkdirs();
			File ff = new File(fd, new SimpleDateFormat("HHmmss").format(new Date()) + "_" + name + (!fileext.isEmpty() ? "." + fileext : ""));
			f.setFilelength(hs.copyStream(bais, new FileOutputStream(ff), true, true));
			f.setFileuri(ff.getAbsolutePath());
		}
		hs.invoke(obj, bNew ? "onNew" : "onUpdate");
		return objService.saveObj(obj);
	}
	private List<String> listIgnore = null;
	private boolean isIgnoreAttr(String attr) {
		if (listIgnore == null) {
			listIgnore.add("rn");
			listIgnore.add("clazz");
			listIgnore.add("name");
			listIgnore.add("creator");
			listIgnore.add("modifier");
			listIgnore.add("mdate");
			listIgnore.add("cdate");
			listIgnore.add("parent");
		}
		return attr == null || listIgnore.contains(attr);
	}
	@GetMapping(value = "/load")
	public String load(HttpServletRequest request, Model model) throws Exception {
		return "loadPage";
	}
	@PostMapping(value = "/load")
	public String loadPost(@RequestParam("file") MultipartFile file, 
						@RequestParam("filter") Optional<String> paramFilter, 
						Model model) throws Exception {
		List<String> listFilter = new ArrayList<String>();
		String[] filters = paramFilter.orElse("").split(",");
		for (String filter : filters) if (!filter.trim().isEmpty()) listFilter.add(filter.trim());
		List<String> listAdd = new ArrayList<String>();
		InputStream is = file.getInputStream();
		HSSFWorkbook wb = (HSSFWorkbook)WorkbookFactory.create(is);
		for (int n=0; n<wb.getNumberOfSheets(); n++) {
			HSSFSheet sheet = wb.getSheetAt(n);
			String clazz = sheet.getSheetName();
			if (listFilter.size() > 0 && !listFilter.contains(clazz)) continue;
			Class<?> cl = hs.getClassByName(clazz);
			if (cl == null) {
				listAdd.add("Не найден класс по имени '" + clazz + "'");
				continue;
			}
			// Заголовок
			List<String> listAttr = new ArrayList<String>();
			HSSFRow row = sheet.getRow(sheet.getFirstRowNum() + 2);
			for (int j=row.getFirstCellNum(); j<=row.getLastCellNum(); j++) {
	            HSSFCell cell = row.getCell(j);
	            if (cell == null) continue;
	            String v = cell.getStringCellValue();
	            if (!hs.isEmpty(v)) v = v.trim().toLowerCase();
	            listAttr.add(v);
	        }
			// Данные
	    	for (int i=sheet.getFirstRowNum()+3; i<=sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				if (row == null) continue;
				Object o = cl.newInstance();
				if (!(o instanceof IBase)) continue;
				IBase obj = (IBase)o;
				boolean bContinue = false;
				String messageContinue = null;
				boolean empty = true;
				for (int j=row.getFirstCellNum(); j<row.getLastCellNum(); j++) {
				    HSSFCell cell = row.getCell(j);
				    if (cell == null) continue;
				    if (cell.getCellType() != Cell.CELL_TYPE_STRING) cell.setCellType(Cell.CELL_TYPE_STRING);
				    String v = cell.getStringCellValue();
				    if (!hs.isEmpty(v)) v = v.trim();
				    if (!hs.isEmpty(v)) { empty = false; break; } 
				}
				if (empty) continue;
				for (int j=row.getFirstCellNum(); j<row.getLastCellNum(); j++) {
				    HSSFCell cell = row.getCell(j);
				    if (cell == null) continue;
				    if (cell.getCellType() != Cell.CELL_TYPE_STRING) cell.setCellType(Cell.CELL_TYPE_STRING);
				    String v = cell.getStringCellValue();
					if (!hs.isEmpty(v)) v = v.trim();
				    String attr = listAttr.get(j);
				    if ("code".equals(attr) && !hs.isEmpty(v)) {
				    	if (hs.isEmpty(v)) { bContinue = true; break; }
				    	Page<Object> p = objService.findAll(cl, null, new String[] {"code"}, new Object[] {v});
				    	if (p != null && !p.isEmpty()) { messageContinue = "Дублирование кода " + obj.getClazz() + " " + v; bContinue = true; break; }
			    	}
				    String getter = "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
				    String setter = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
					Method mSet = null, mGet = null;
					try { 
						mGet = cl.getMethod(getter);
						mSet = cl.getMethod(setter, mGet.getReturnType()); 
						Class<?> clType = mGet.getReturnType();
						mSet.invoke(obj, IBase.class.isAssignableFrom(clType) ? objService.getObjByCode(clType, v) : hs.getObjectByString(v, clType));
					} 
					catch (Exception e) { }
				}
				if (bContinue) {
					if (messageContinue != null) listAdd.add(messageContinue); 
					continue;
				}
				try  { 
					obj = (IBase)objService.saveObj(obj);
					listAdd.add("Добавлен " + obj.getClazz() + " " + obj.getRn() + " " + obj.getName());
				} 
				catch (Exception ex) { 
					listAdd.add("Не добавлен " + obj.getClazz() + " " + obj.getRn() + " " + obj.getName() + ": " + ex.getClass().getSimpleName() + " " + ex.getMessage());
				}
	    	}
		}
		model.addAttribute("datetime", new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()));
    	model.addAttribute("listResult", listAdd);
		return "loadResultPage";
	}
}
