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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import ru.landar.spring.ObjectChanged;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SpCommon;
import ru.landar.spring.model.SpFileType;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.repository.UserRepositoryCustomImpl;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.HelperServiceImpl;
import ru.landar.spring.service.ObjService;

@Controller
public class LoadController {
	@Autowired
	private ObjService objService;
	@Autowired
	private HelperService hs;
	@Autowired
    private PlatformTransactionManager transactionManager;
	@Autowired
	ObjRepositoryCustom objRepository;
	@Resource(name = "getObjectChanged")
    ObjectChanged objectChanged;
	
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
		
		String filesDirectory = (String)objService.getSettings("filesDirectory", "string");
		if (hs.isEmpty(filesDirectory)) filesDirectory = System.getProperty("user.dir") + File.separator + "FILES";
		File fd = new File(filesDirectory + new SimpleDateFormat(".yyyy.MM.dd").format(new Date()).replace('.', File.separatorChar));
		fd.mkdirs();
		
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file.getInputStream());
			for (Node n=doc.getDocumentElement().getFirstChild(); n!=null; n=n.getNextSibling()) {
				if (n.getNodeType() != Node.ELEMENT_NODE) continue;
				IBase obj = (IBase)createObject((Element)n, null, listAdd, listFilter, fd);
				if (obj != null) listAdd.add("Добавлен " + obj.getClazz() + " " + obj.getRn() + " " + obj.getName());
			}
			transactionManager.commit(ts);
    	}
    	catch (Exception ex) {
    		transactionManager.rollback(ts);
    		throw ex;
    	}
		model.addAttribute("datetime", new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()));
    	model.addAttribute("listResult", listAdd);
		return "loadResultPage";
	}
	private Object createObject(Element el, String paramClazz, List<String> listAdd, List<String> listFilter, File fd) throws Exception {
		Map<String, String> mapValue = new LinkedHashMap<String, String>();
		boolean empty = true;
		NamedNodeMap nm = el.getAttributes();
		for (int i=0; nm!=null && i<nm.getLength(); i++) {
			Node attr = nm.item(i);
			String value = attr.getNodeValue(), name = attr.getNodeName();
			if (isIgnoreAttr(name)) continue;
			if (!"clazz".equals(name) && !"code".equals(name)) empty = false;
			mapValue.put(name, value);
		}
		for (Node nChild=el.getFirstChild(); nChild!=null; nChild=nChild.getNextSibling()) {
			if (nChild.getNodeType() != Node.ELEMENT_NODE) continue;
			Element elChild = (Element)nChild;
			String name = elChild.getLocalName();
			if (hs.isEmpty(name)) name = elChild.getNodeName();
			if (isIgnoreAttr(name)) continue;
			if (elChild.getElementsByTagName("*").getLength() > 0) continue;
			if (!"clazz".equals(name) && !"code".equals(name)) empty = false;
			mapValue.put(name, elChild.getTextContent());
		}
		
		String clazz = mapValue.get("clazz");
		if (hs.isEmpty(clazz)) clazz = paramClazz;
		if (hs.isEmpty(clazz)) clazz = el.getLocalName();
		if (hs.isEmpty(clazz)) clazz = el.getNodeName();
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) {
			listAdd.add(String.format("Не найден класс объекта %s", clazz));
			return null;
		}
		String code = mapValue.get("code");
		boolean bNew = true;
		Object obj = null;
		if (!hs.isEmpty(code)) obj = objRepository.findByCode(cl, code);
		if (obj != null) { 
			bNew = false; 
			return obj; 
		}
		if (listFilter != null && listFilter.size() > 0 && !hs.isEmpty(code) && listFilter.contains(code)) return null;
		if (!hs.isEmpty(code) && empty) return null;
		obj = cl.newInstance();
		// Атрибуты
		nm = el.getAttributes();
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
					Object o = null;
					String sp_code = (String)HelperServiceImpl.getAttrInfo(cl, name, "sp");
					if (!hs.isEmpty(sp_code)) o = objRepository.find(clAttr, new String[] {"code", "sp_code"}, new Object[] { value, sp_code });
					else o = objRepository.findByCode(clAttr, value);
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
			if (hs.isEmpty(name)) name = elChild.getNodeName();
			if (isIgnoreAttr(name)) continue;
			Class<?> clAttr = hs.getAttrType(cl, name);
			if (clAttr == null) {
				listAdd.add(String.format("Не найден атрибут %s объекта %s", name, clazz));
				continue;
			}
			if (IBase.class.isAssignableFrom(clAttr)) {
				Object child = createObject(elChild, clAttr.getSimpleName(), listAdd, null, fd);
				if (child != null) hs.setProperty(obj, name, child);
			}
			else if (List.class.isAssignableFrom(clAttr)) {
				List<Object> l = new ArrayList<Object>();
				for (Node nC=elChild.getFirstChild(); nC!=null; nC=nC.getNextSibling()) {
					if (nC.getNodeType() != Node.ELEMENT_NODE) continue;
					Class<?> clType = hs.getListAttrType(cl, name);
					Object child = createObject((Element)nC, clType != null ? clType.getSimpleName() : null, listAdd, null, fd);
					if (child != null) l.add(child);
				}
				hs.setProperty(obj, name, l);
			}
			else {
				String value = elChild.getTextContent();
				if (IBase.class.isAssignableFrom(clAttr)) {
					if (!hs.isEmpty(value)) {
						Object o = null;
						String sp_code = (String)HelperServiceImpl.getAttrInfo(cl, name, "sp");
						if (!hs.isEmpty(sp_code)) o = objRepository.find(clAttr, new String[] {"code", "sp_code"}, new Object[] { value, sp_code });
						else o = objRepository.findByCode(clAttr, value);
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
			if (hs.isEmpty(fileext) && !hs.isEmpty(filename)) {
				int k = filename.lastIndexOf('.');
				fileext = k > 0 ? filename.substring(k + 1) : "";
				name = k > 0 ? filename.substring(0, k) : filename;
				f.setFileext(fileext);
			}
			if (!hs.isEmpty(fileext)) {
				SpFileType filetype = (SpFileType)objRepository.findByCode(SpFileType.class, fileext.toLowerCase());
				f.setFiletype(filetype);
			}
			String fileuri = hs.getPropertyString(obj, "fileuri");
			byte[] b = null;
			if (!hs.isEmpty(fileuri)) try { b = Base64.getDecoder().decode(fileuri); } catch (Exception ex) { 
				listAdd.add("Исключение при получении содержимого файла");	
			}
			if (b == null) b = new byte[0];
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			File ff = new File(fd, new SimpleDateFormat("HHmmss").format(new Date()) + "_" + name + (!hs.isEmpty(fileext) ? "." + fileext : ""));
			f.setFilelength(hs.copyStream(bais, new FileOutputStream(ff), true, true));
			f.setFileuri(ff.getAbsolutePath());
		}
		hs.invoke(obj, bNew ? "onNew" : "onUpdate");
		return objRepository.saveObj(obj);
	}
	private List<String> listIgnore = null;
	private boolean isIgnoreAttr(String attr) {
		if (listIgnore == null) {
			listIgnore = new ArrayList<String>();
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
		try {
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
		            if (!hs.isEmpty(v)) listAttr.add(v);
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
					for (int j=row.getFirstCellNum(); j<row.getLastCellNum() && j<listAttr.size(); j++) {
					    HSSFCell cell = row.getCell(j);
					    if (cell == null) continue;
					    if (cell.getCellType() != Cell.CELL_TYPE_STRING) cell.setCellType(Cell.CELL_TYPE_STRING);
					    String v = cell.getStringCellValue();
						if (!hs.isEmpty(v)) v = v.trim();
					    String attr = listAttr.get(j);
					    String getter = "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
					    String setter = "set" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
						Method mSet = null, mGet = null;
						try { 
							mGet = cl.getMethod(getter);
							mSet = cl.getMethod(setter, mGet.getReturnType()); 
							Class<?> clType = mGet.getReturnType();
							Object ot = null;
							if (IBase.class.isAssignableFrom(clType)) {
								if (!"SpCommon".equals(clType.getSimpleName())) ot = objService.getObjByCode(clType, v);
								else {
									String sp_code = (String)HelperServiceImpl.getAttrInfo(cl, attr, "sp");
									ot = objService.find(clType, new String[] {"code", "sp_code"}, new Object[] { v, sp_code});
								}
							}
							else if (List.class.isAssignableFrom(clType)) {
								clType = hs.getListAttrType(cl, attr);
								ot = hs.getProperty(obj, attr); 
								String[] vs = v.split(",");
								for (String vl : vs) {
									Object item = objService.getObjByCode(clType, vl);
									if (item != null) ((List)ot).add(item);
								}
							}
							else {
								ot = hs.getObjectByString(v, clType);
								if (IUser.class.isAssignableFrom(cl) && "password".equals(attr) && !((String)ot).startsWith("{bcrypt}"))  
									ot = UserRepositoryCustomImpl.encoder.encode((String)ot);
							}
							mSet.invoke(obj, ot);
						} 
						catch (Exception e) { }
					}
					// Проверка объекта obj по code + (sp_code)
					String code = (String)hs.getProperty(obj, "code"), sp_code = (String)hs.getProperty(obj, "sp_code");
					if (!hs.isEmpty(code)) {
						List<String> la = new ArrayList<String>();
				    	la.add("code");
				    	List<Object> lv = new ArrayList<Object>();
				    	lv.add(code);
				    	if (SpCommon.class.isAssignableFrom(cl)) {
				    		la.add("sp_code");
				    		lv.add(sp_code);
				    	}
				    	Page<Object> p = objService.findAll(cl, null, la.toArray(new String[la.size()]), lv.toArray(new Object[lv.size()]));
				    	if (p != null && !p.isEmpty()) { messageContinue = "Дублирование кода " + obj.getClazz() + " " + code + (!hs.isEmpty(sp_code) ? " " + sp_code : ""); bContinue = true; }
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
		}
		catch (Exception ex) {
			listAdd.add("Исключение " + ex.getClass().getSimpleName() + " " + ex.getMessage());
			ex.printStackTrace();
		}
		model.addAttribute("datetime", new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").format(new Date()));
    	model.addAttribute("listResult", listAdd);
		return "loadResultPage";
	}
}
