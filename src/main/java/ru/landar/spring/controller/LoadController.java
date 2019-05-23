package ru.landar.spring.controller;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import ru.landar.spring.model.IBase;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;

@Controller
public class LoadController {
	@Autowired
	private ObjService objService;
	@Autowired
	private HelperService hs;
	@GetMapping(value = "/load")
	public String load(HttpServletRequest request, Model model) throws Exception {
		return "loadPage";
	}
	@PostMapping(value = "/load")
	public String loadPost(@RequestParam("file") MultipartFile file, Model model) throws Exception {
		List<String> listAdd = new ArrayList<String>();
		InputStream is = file.getInputStream();
		HSSFWorkbook wb = (HSSFWorkbook)WorkbookFactory.create(is);
		for (int n=0; n<wb.getNumberOfSheets(); n++) {
			HSSFSheet sheet = wb.getSheetAt(n);
			String clazz = sheet.getSheetName();
			Class<Object> cl = objService.getClassByName(clazz);
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
				for (int j=row.getFirstCellNum(); j<row.getLastCellNum(); j++) {
				    HSSFCell cell = row.getCell(j);
				    if (cell == null) continue;
				    if (cell.getCellType() != Cell.CELL_TYPE_STRING) cell.setCellType(Cell.CELL_TYPE_STRING);
				    String v = cell.getStringCellValue();
					if (!hs.isEmpty(v)) v = v.trim();
				    String attr = listAttr.get(j);
				    if ("code".equals(attr)) {
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
