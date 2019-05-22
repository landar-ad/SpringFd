package ru.landar.spring.controller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.ActionLog;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.SearchContent;
import ru.landar.spring.model.SpActStatus;
import ru.landar.spring.model.SpActionType;
import ru.landar.spring.model.SpAgentType;
import ru.landar.spring.model.SpDocStatus;
import ru.landar.spring.model.SpDocType;
import ru.landar.spring.model.SpFileType;
import ru.landar.spring.model.SpReestrStatus;
import ru.landar.spring.model.ISettings;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@PreAuthorize("hasRole('ROLE_USER')")
@Controller
public class ObjController {
	@Autowired
	private ObjService objService;
	@Autowired
	private UserService userService;
	@Autowired
	private HelperService hs;
	@RequestMapping(value = "/listObj")
	public String listObj(@RequestParam("clazz") String clazz,
						  @RequestParam("p_off") Optional<Integer> offParam,
						  @RequestParam("p_page") Optional<Integer> pageParam,
						  @RequestParam("p_block") Optional<Integer> blockParam,
						  @RequestParam("rn") Optional<Integer> rnParam,
						  @RequestParam("p_refreshElement") Optional<String> refreshElementParam,
						  @RequestParam("p_listVisible") Optional<String> listVisibleParam,
						  HttpServletRequest request, 
						  Model model) throws Exception {
		int off = offParam.orElse(0), page = pageParam.orElse(15), block = blockParam.orElse(10);
		Integer rn = rnParam.orElse(null);
		String refreshElement = refreshElementParam.orElse(null), listVisible = listVisibleParam.orElse(null);
		Class<Object> cl = objService.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Object obj = cl.newInstance();
		// Поисковые атрибуты
		List<String> listAttr = new ArrayList<String>();
		List<Object> listValue = new ArrayList<Object>();
		// Данные для сортировки
		Map<String, String> mapSort = new HashMap<String, String>();
		Sort sort = null;
		for (String p : Collections.list((Enumeration<String>)request.getParameterNames())) {
			String v = request.getParameter(p);
			if (p.startsWith("sort__")) {
				String n = p.substring(6), d = v;
				if (hs.isEmpty(d)) d = "NONE";
				mapSort.put(n, d);
				if ("ASC".equalsIgnoreCase(d) || "DESC".equalsIgnoreCase(d)) {
					Sort sortAdd = Sort.by(n);
					sortAdd = "DESC".equalsIgnoreCase(d) ? sortAdd.descending() : sortAdd.ascending();
					if (sort == null) sort = sortAdd;
					else sort.and(sortAdd);
				}
				continue;
			}
			if (hs.isEmpty(v) || "clazz".equals(p) || "rn".equals(p) || "p_off".equals(p) || "p_page".equals(p) || "p_block".equals(p) ||  
				"p_listVisible".equals(p) || "p_refreshElement".equals(p)) continue;
			Class<?> attrType = hs.getAttrType(cl, p);
			if (attrType == null) continue;
			listAttr.add(p);
			listValue.add(v);
		}
		// Сохранить список видимых колонок
		if (!hs.isEmpty(listVisible)) setSettings(clazz + "_listVisible", "string", listVisible);
		// Добавить фильтр, если есть
		hs.invoke(obj, "onListAddFilter", listAttr, listValue);
		// Поисковые атрибуты
		String[] attr = listAttr.size() > 0 ? listAttr.toArray(new String[listAttr.size()]) : null;
		Object[] value = listValue.size() > 0 ? listValue.toArray(new Object[listValue.size()]) : null;
		// Список колонок
		List<ColumnInfo> listColumn = (List<ColumnInfo>)hs.invoke(obj, "onListColumn");
		String visible = (String)objService.getSettings(clazz + "_listVisible", "string");
		if (!hs.isEmpty(visible) && listColumn != null && listColumn.size() > 0) {
			for (ColumnInfo ci : listColumn) ci.setVisible(false);
			visible = visible.trim();
			if (!hs.isEmpty(visible)) {
				String[] vv = visible.split(",");
				for (String v : vv) {
					for (ColumnInfo ci : listColumn) { 
						if (v.equalsIgnoreCase(ci.getName())) { 
							ci.setVisible(true); break; 
						} 
					}
				} 
			}
		}
		List<ColumnInfo> listColumnVisible = new ArrayList<ColumnInfo>();
		for (ColumnInfo ci : listColumn) if (ci.getVisible()) listColumnVisible.add(ci);
		// Видимые колонки и все
		model.addAttribute("listColumn", listColumnVisible);
		model.addAttribute("listColumnAll", listColumn);
		// Есть ли фильтрация?
		boolean p_filtering = false;
		for (ColumnInfo ci : listColumn) { if (!hs.isEmpty(ci.getFilter())) p_filtering = true; break; }
		model.addAttribute("p_filtering", p_filtering);
		// Сортировка
		if (sort == null) sort = Sort.by("rn").ascending();
		// Пейджинг
		boolean p_paging = (Boolean)hs.invoke(cl.newInstance(), "onListPaginated");
		model.addAttribute("p_paging", p_paging);
		// Получить страницу данных
		Page<Object> listObj = objService.findAll(cl, PageRequest.of(off, p_paging ? page : Integer.MAX_VALUE, sort), attr, value);
		model.addAttribute("listObj", listObj);
		// Добавить атрибуты в модель (списки, заголовки, параметры)
		hs.invoke(obj, "onAddAttributes", model, true);
		// Пользователь и его роли
		model.addAttribute("p_login", userService.getPrincipal());
		model.addAttribute("p_roles", userService.getRoles(null));
		// Всего записей
		model.addAttribute("p_totalRows", listObj.getTotalElements());
		if (p_paging) {
			// Число записей на странице
			model.addAttribute("p_page", page);
			// Количество страниц в блоке переходов на страницы (в паджинаторе)
			model.addAttribute("p_block", block);
			// Всего страниц
			int totalPages = listObj.getTotalPages();
			model.addAttribute("p_total", totalPages);
			// Текущая страница
			off = Math.min(listObj.getNumber(), totalPages > 0 ? totalPages - 1 : 0);
			model.addAttribute("p_off", off);
			int n = block, start = (off / n) * n + 1;
			start = start - (n / 2);
			if (start < 1) start = 1;
			int end = start + n;
			if (end > totalPages) end = totalPages;
			// Список номеров страниц
			List<Integer> pageNumbers = IntStream.rangeClosed(start , end).boxed().collect(Collectors.toList());
			model.addAttribute("p_pageNumbers", pageNumbers);
			// Список количества записей на странице
			model.addAttribute("p_countPages", new Integer[]{10, 15, 30, 50, 100, 500, 1000});
		}
		// Запись для выделения
		if (rn != null) model.addAttribute("rn", rn);
		// Класс объектов в списке
		model.addAttribute("clazz", clazz);
		// Атрибуты фильтрации
		if (attr != null && attr.length > 0) {
			for (int i=0; i<attr.length; i++) {
				String a = attr[i];
				Object v = value[i];
				v = v != null ? hs.getObjectByString(cl, a, v.toString()) : null;
				model.addAttribute(a, v);
			}
		}
		// Список сортировки
		model.addAttribute("p_sort", mapSort);
		// Заголовок
		if (!model.containsAttribute("p_title")) {
			String title = "";
			try { title = (String)hs.invoke(obj, "onMultipleTitle"); } catch (Exception ex) { }
			model.addAttribute("p_title", title);
		}
		// Объект вспомогательных сервисов
		model.addAttribute("hs", hs);
		// Неполная прорисовка страницы
		if (!hs.isEmpty(refreshElement) && hs.templateExists("fragments/list")) return "fragments/list::" + refreshElement;
		// Страница по имени класса или страница по умолчанию
		String t = "list" + clazz + "Page";
		return hs.templateExists(t) ? t : "listObjPage";
	}
	@RequestMapping(value = "/detailsObj", method = RequestMethod.GET)
	public String detailsObj(@RequestParam("rn") Optional<Integer> paramRn, 
							 @RequestParam("clazz") String clazz, 
							 @RequestParam("p_tab") Optional<Integer> paramTab, 
							 Model model) throws Exception {
		Class<?> cl = objService.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Object obj = null;
		Integer rn = paramRn.orElse(null);
		if (rn == null) {
			obj = cl.newInstance();
			hs.invoke(obj, "onNew");
		}
		else obj = objService.find(cl, rn);
		if (obj == null) throw new Exception("Не найден объект по имени класса '" + clazz + "' с идентификатором " + rn);
		model.addAttribute("hs", hs);
		setObjModel(obj, model);
		model.addAttribute("p_tab", paramTab.orElse(1));
		String t = "details" + clazz + "Page";
		return hs.templateExists(t) ? t : "detailsObjPage";
	}
	@RequestMapping(value = "/detailsObj", method = RequestMethod.POST)
	public String detailsObjPost(@RequestParam("clazz") String clazz, 
								 @RequestParam("rn") Optional<Integer> paramRn, 
								 @RequestParam("fileInput") Optional<MultipartFile> fileParam, 
								 @RequestParam("list") Optional<String> listParam,
								 @RequestParam("clazzItem") Optional<String> clazzItemParam,
								 @RequestParam("cmdItem") Optional<String> cmdItemParam,
								 @RequestParam("rnItem") Optional<Integer> rnItemParam,
								 HttpServletRequest request,
								 Model model) throws Exception {
		Integer rn = paramRn.orElse(null);
		Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
		Map<String, Object> mapFile = new LinkedHashMap<String, Object>();
		mapFile.put("fileuri", fileParam.orElse(null));
		Class<?> cl = objService.getClassByName(clazz);
		if (cl == null) throw new ClassNotFoundException("Не найден класс по имени '" + clazz + "'");
		List<String> listNames = Collections.list((Enumeration<String>)request.getParameterNames());
		for (String p : listNames) {
			String v = request.getParameter(p);
			if ("clazz".equals(p) || "rn".equals(p)) continue;
			else if (p.startsWith("file__")) {
				p = p.substring(6);
				mapFile.put(p, hs.getObjectByString(IFile.class, p, v));
				continue;
			}
			if (hs.getAttrType(cl, p) != null) mapValue.put(p, hs.getObjectByString(cl, p, v));
		}
		// Изменение объекта
		Object obj = rn == null ? cl.newInstance() : objService.find(cl, rn);
		if (!hs.checkRights(obj, Operation.update)) throw new SecurityException("Вы не имеете право на редактирование объекта " + hs.getProperty(obj, "name"));
		Map<String, Object[]> mapChanged = new LinkedHashMap<String, Object[]>();
		mapValue.forEach((attr, valueNew) -> {
			Object valueOld = hs.getProperty(obj, attr);
			if (!hs.equals(valueOld, valueNew)) mapChanged.put(attr, new Object[]{valueOld, valueNew}); 
			hs.setProperty(obj, attr, valueNew);
		});
		objService.saveObj(obj);
		// Добавление информации об изменении объекта
		hs.invoke(obj, "onUpdate", mapFile);
		// Запись в журнал
		if (mapChanged != null && !mapChanged.isEmpty()) { 
    		Integer obj_rn = (Integer)hs.getProperty(obj, "rn");
    		SpActionType action_type = (SpActionType)objService.getObjByCode(SpActionType.class, obj_rn == null ? "create" : "update");
    		Date dt = new Date();
    		String obj_name = obj.getClass().getSimpleName(), 
    			user_login = userService.getPrincipal(), 
    			ip = (String)request.getSession().getAttribute("ip"), 
    			browser = (String)request.getSession().getAttribute("browser");
    		mapChanged.forEach((attr, o) -> {
    			ActionLog al = new ActionLog();
        		al.setUser_login(user_login);
        		al.setAction_type(action_type);
        		al.setAction_time(dt);
        		al.setClient_ip(ip);
        		al.setClient_browser(browser);
        		al.setObj_name(obj_name);
        		al.setObj_rn(obj_rn);
        		al.setObj_attr(attr);
        		al.setObj_value_before(o[0] != null ? o[0].toString() : null);
        		al.setObj_value_after(o[1] != null ? o[1].toString() : null);
        		objService.saveObj(al);
    		});
    	}
		// Добавление объекта в список
		String listAttr = listParam.orElse(null), clazzItem = clazzItemParam.orElse(null);
		Integer rnItem = rnItemParam.orElse(null);
		if (!hs.isEmpty(listAttr) && !hs.isEmpty(clazzItem)) {
			Object listObj = hs.getProperty(obj, listAttr);
			if (listObj == null || !(listObj instanceof List)) throw new Exception("Не найден список '" + listAttr + "'");
			List list = (List<?>)listObj;
			Class<?> clItem = objService.getClassByName(clazzItem);
			if (clItem == null) throw new ClassNotFoundException("Не найден класс по имени '" + clazzItem + "'");
			String cmd = cmdItemParam.orElse("add");
			if ("add".equals(cmd)) {
				Object item = rnItem != null ? objService.find(clItem, rnItem) : cl.newInstance();
				if (rnItem == null) hs.invoke(item, "onNew");
				list.add(item);
				hs.setProperty(obj, listAttr, list);
			}
			else if ("remove".equals(cmd)) {
				if (rnItem == null) throw new Exception("Не задан идентификатор объекта для удаления из списка");
				Object objItem = objService.find(clItem, rnItem);
				if (objItem == null) throw new Exception("Не найден объект " + clazzItem + " по идентификатору " + rnItem);
				for (Object o : list) {
					if (!(o instanceof IBase)) continue;
					IBase b = (IBase)o;
					if (rnItem == b.getRn()) {
						list.remove(o);
						break;
					}
				}
				hs.setProperty(obj, listAttr, list);
				objService.removeObj(objItem);
			}
			model.addAttribute("hs", hs);
			setObjModel(obj, model);
			String t = "details" + clazz + "Page";
			return hs.templateExists(t) ? t : "detailsObjPage";
		}
		//
		// Переход на страницу
		String redirect = (String)hs.invoke(obj, "onRedirectAfterUpdate");
		if (hs.isEmpty(redirect)) redirect = "mainPage";
		return "redirect:" + redirect;
	}
	@RequestMapping(value = "/removeObj", method = RequestMethod.GET)
	public String removeObj(@RequestParam("rn") Integer rn, 
							@RequestParam("clazz") Optional<String> paramClazz, 
							Model model) throws Exception {
		Object obj = objService.find(paramClazz.orElse(null), rn);
		if (obj == null) throw new Exception("Не найден объект с идентификатором " + rn);
		if (!hs.checkRights(obj, Operation.delete)) throw new Exception("Вы не имеете прав на удаление объекта " + hs.getProperty(obj, "name"));
		setMainModel(model, "Удаление объекта");
		model.addAttribute("obj", obj);
		return "removeObjPage";
	}
	@RequestMapping(value = "/removeObj", method = RequestMethod.POST)
	public String removeObjPost(@RequestParam("rn") Integer rn, 
								@RequestParam("clazz") Optional<String> paramClazz,
								HttpServletRequest request,
								Model model) throws Exception {
		Object obj = objService.find(paramClazz.orElse(null), rn);
		String msg = "", name = "";
		for (; ;) {
			if (obj == null) { msg = String.format("Не найден объект с идентификатором '%s'", rn); break; }
			name = (String)hs.getProperty(obj, "name");
			if (!hs.checkRights(obj, Operation.delete)) { msg = String.format("Вы не имеете прав на удаление объекта '%s'", name); break; }
			Boolean b = (Boolean)hs.invoke(obj, "onRemove");
			if (b != null && !b) { msg = String.format("Отказано в удалении объекта '%s'", name); break; }
			objService.removeObj(paramClazz.orElse(null), rn);
			// Запись в журнал
    		SpActionType action_type = (SpActionType)objService.getObjByCode(SpActionType.class, "remove");
    		Date dt = new Date();
    		String obj_name = obj.getClass().getSimpleName(), 
    			user_login = userService.getPrincipal(), 
    			ip = (String)request.getSession().getAttribute("ip"), 
    			browser = (String)request.getSession().getAttribute("browser");
			ActionLog al = new ActionLog();
    		al.setUser_login(user_login);
    		al.setAction_type(action_type);
    		al.setAction_time(dt);
    		al.setClient_ip(ip);
    		al.setClient_browser(browser);
    		al.setObj_name(obj_name);
    		al.setObj_rn(rn);
    		objService.saveObj(al);
    		//
			msg = String.format("Объект '%s' успешно удален", name);
			break;
		}
		setMainModel(model, "Удаление объекта");
		model.addAttribute("p_message", msg);
		return "removeObjPage";
	}
	@RequestMapping(value = "/addItem", method = RequestMethod.GET)
	public String addItem(@RequestParam("list") String listAttr, @RequestParam("rn") Integer rn, 
						@RequestParam("clazz") Optional<String> paramClazz,
						@RequestParam("clazzItem") String clazzItem,
						HttpServletRequest request,
						Model model) throws Exception {
		Object obj = objService.find(paramClazz.orElse(null), rn);
		if (obj == null) throw new Exception("Не найден объект с идентификатором " + rn);
		String clazz = obj.getClass().getSimpleName();
		Object list = hs.getProperty(obj, listAttr);
		if (list == null || !(list instanceof List)) throw new Exception("Не найден список " + listAttr + " в объекте с идентификатором " + rn);
		Class<?> cl = objService.getClassByName(clazzItem);
		if (cl == null) throw new ClassNotFoundException("Не найден класс по имени '" + clazzItem + "'");
		Object item = cl.newInstance();
		hs.invoke(item, "onNew");
		((List)list).add(item);
		hs.setProperty(obj, listAttr, list);
		model.addAttribute("hs", hs);
		setObjModel(obj, model);
		String t = "details" + clazz + "Page";
		return hs.templateExists(t) ? t : "detailsObjPage";
	}
	@RequestMapping(value = "/listVoc", method = RequestMethod.GET)
	public String listVoc(Model model) throws Exception {
		List<Voc> listVoc = new ArrayList<Voc>();
		listVoc.add(new Voc("SpActStatus", SpActStatus.singleTitle()));
		listVoc.add(new Voc("SpAgentType", SpAgentType.singleTitle()));
		listVoc.add(new Voc("SpDocStatus", SpDocStatus.singleTitle()));
		listVoc.add(new Voc("SpDocType", SpDocType.singleTitle()));
		listVoc.add(new Voc("SpFileType", SpFileType.singleTitle()));
		listVoc.add(new Voc("SpReestrStatus", SpReestrStatus.singleTitle()));
		model.addAttribute("listObj", new PageImpl<Voc>(listVoc));
		setMainModel(model, "Справочники системы");
		return "listVocPage";
	}
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@RequestParam("p_text") Optional<String> textParam,
						 @RequestParam("p_off") Optional<Integer> offParam,
						 @RequestParam("p_page") Optional<Integer> pageParam,
						 @RequestParam("p_block") Optional<Integer> blockParam,
						 Model model) throws Exception {
		String text = textParam.orElse("");
		int off = offParam.orElse(0), page = pageParam.orElse(15), block = blockParam.orElse(10);
		if (off < 0) off = 0;
		Page<SearchContent> p = objService.search(text, off, page);
		String[] ts = text.split("[,;:.!?\\s]+");
		for (SearchContent cs : p.getContent()) {
			String content = cs.getContent();
			for (String t : ts) {
				int s = 0;
				for (; ;) {
					int k = content.indexOf(t, s); 
					if (k < 0) break;
					content = content.substring(0, k) + "<strong>" + t + "</strong>" + content.substring(k + t.length());
					s =  k + ("<strong>" + t + "</strong>").length();
				}
			}
			cs.setContent(content);
		}
		setMainModel(model, "Результаты поиска");
		model.addAttribute("p_text", text);
		model.addAttribute("listItem", p);
		model.addAttribute("p_page", page);
		model.addAttribute("p_block", block);
		int totalPages = p.getTotalPages();
		model.addAttribute("p_total", totalPages);
		model.addAttribute("p_totalRows", p.getTotalElements());
		off = Math.min(p.getNumber(), totalPages > 0 ? totalPages - 1 : 0);
		model.addAttribute("p_off", off);
		int n = block, start = Math.min((off / n) * n + 1, totalPages - n + 1);
		List<Integer> pageNumbers = IntStream.rangeClosed(start , Math.min(start + n - 1, totalPages)).boxed().collect(Collectors.toList());
		model.addAttribute("p_pageNumbers", pageNumbers);
		model.addAttribute("p_countPages", new Integer[]{15, 30, 50, 100});
		return "mainPage";
	}
	@RequestMapping(value = "/settings", method = RequestMethod.POST, produces = "text/plain")
	@ResponseBody
	public String settings(@RequestParam("code") String code, 
						   @RequestParam("type") Optional<String> paramType,
						   HttpServletRequest request, 
						   Model model) throws Exception {
		String type = paramType.orElse("string");
		String value = "";
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			hs.copyStream(request.getInputStream(), baos, true, true);
			value = new String(baos.toByteArray(), "UTF-8");
		}
		catch (Exception ex) { }
		setSettings(code, type, value);
		return "OK";
	}
	private void setSettings(String code, String type, String value) {
		ISettings settings = (ISettings)objService.loadSettings(code, type);
		if (settings == null) {
			settings = new ISettings();
			if (code.endsWith("_listVisible")) settings.setName("Видимые колонки");
		}
		settings.setCode(code);
		settings.setType(type);
		settings.setValue(value);
		settings.setUsername(userService.getPrincipal());
		objService.saveObj(settings);
	}
	private void setObjModel(Object obj, Model model) throws Exception {
		model.addAttribute("obj", obj);
		Integer prn = (Integer)hs.getProperty(obj, "parent__rn");
		if (prn != null) model.addAttribute("prn", prn);
		model.addAttribute("readonly", !hs.checkRights(obj, Operation.update)); 
		List<AttributeInfo> listAttribute = (List<AttributeInfo>)hs.invoke(obj, "onListAttribute");
		if (listAttribute != null) model.addAttribute("listAttribute", listAttribute);
		model.addAttribute("p_login", userService.getPrincipal());
		model.addAttribute("p_roles", userService.getRoles(null));
		hs.invoke(obj, "onAddAttributes", model, false);
		if (!model.containsAttribute("p_title")) {
			String title = (String)hs.getProperty(obj, "name");
			if (hs.isEmpty(title)) try { title = (String)hs.invoke(obj, "onSingleTitle"); } catch (Exception ex) { }
			model.addAttribute("p_title", title);
		}
	}
	private void setMainModel(Model model, String title) {
		model.addAttribute("p_login", userService.getPrincipal());
		model.addAttribute("p_roles", userService.getRoles(null));
		model.addAttribute("p_title", title);
	}
	public class Voc {
		private String name;
		private String title;
		
		public Voc(String name, String title) { 
			setName(name); 
			setTitle(title);
		}
		public String getName() { return name; }
		public void setName(String name) { this.name = name; }
		public String getTitle() { return title; }
		public void setTitle(String title) { this.title = title; }
	}
}
