package ru.landar.spring.controller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.landar.spring.ObjectChanged;
import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ChangeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.classes.Voc;
import ru.landar.spring.model.ActionLog;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.ISession;
import ru.landar.spring.model.SearchContent;
import ru.landar.spring.repository.ObjRepositoryCustom;
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
	@Autowired
    private PlatformTransactionManager transactionManager;
	@Autowired
	ObjRepositoryCustom objRepository;
	@Resource(name = "getObjectChanged")
    ObjectChanged objectChanged;
	// Список объектов		
	@RequestMapping(value = "/listObj")
	public String listObj(@RequestParam("clazz") String clazz,
						  @RequestParam("p_off") Optional<Integer> offParam,
						  @RequestParam("p_page") Optional<Integer> pageParam,
						  @RequestParam("p_block") Optional<Integer> blockParam,
						  @RequestParam("rn") Optional<Integer> rnParam,
						  @RequestParam("p_listVisible") Optional<String> listVisibleParam,
						  @RequestParam("p_ret") Optional<String> retParam,
						  @RequestParam("p_all") Optional<String> allParam,
						  HttpServletRequest request, 
						  Model model) throws Exception {
		int off = offParam.orElse(0), page = pageParam.orElse(15), block = blockParam.orElse(10);
		Integer rn = rnParam.orElse(null);
		String listVisible = listVisibleParam.orElse(null);
		boolean p_all = (Boolean)hs.getObjectByString(allParam.orElse(""), Boolean.class);  
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Object obj = cl.newInstance();
		// Последние используемые параметры
		String p_ret = retParam.orElse("");
		HttpSession session = request.getSession();
		Map<String, String[]> mapParam = null;
		if ("1".equals(p_ret)) mapParam = (Map<String, String[]>)session.getAttribute("listObj_" + clazz);
		if (mapParam == null) mapParam = request.getParameterMap();
		else {
			if (rn == null) try { rn = Integer.valueOf(mapParam.get("rn")[0]); } catch (Exception ex) { rn = null; }
			try { off = Integer.valueOf(mapParam.get("p_off")[0]); } catch (Exception ex) { off = 0; }
			try { page = Integer.valueOf(mapParam.get("p_page")[0]); } catch (Exception ex) { page = 15; }
			try { block = Integer.valueOf(mapParam.get("p_block")[0]); } catch (Exception ex) { block = 10; }
			try { listVisible = mapParam.get("p_listVisible")[0]; } catch (Exception ex) { listVisible = null; }
		}
		// Поисковые атрибуты
		List<String> listAttr = new ArrayList<String>();
		List<Object> listValue = new ArrayList<Object>();
		// Данные для сортировки
		Map<String, String> mapSort = new HashMap<String, String>();
		Sort sort = null;
		for (String p : mapParam.keySet()) {
			String v = "";
			String[] vs = mapParam.get(p);
			if (vs != null && vs.length > 0) for (String t : vs) { 
				if (hs.isEmpty(t)) continue;
				v = t; 
				break; 
			}
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
			if (hs.isEmpty(v) || "clazz".equals(p) || "rn".equals(p) || "p_ret".equals(p) || "p_listVisible".equals(p) || "p_off".equals(p) || "p_page".equals(p) || "p_block".equals(p) || "p_all".equals(p)) continue;
			Class<?> attrType = hs.getAttrType(cl, p);
			if (attrType == null) continue;
			listAttr.add(p);
			listValue.add(v);
		}
		// Сохранить список видимых колонок
		if (!hs.isEmpty(listVisible)) setSettings(clazz + "_listVisible", "string", listVisible);
		// Добавить фильтр, если есть
		hs.invoke(obj, "onListAddFilter", listAttr, listValue, mapParam);
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
		// Дополнительные кнопки
		List<ButtonInfo> listButton = (List<ButtonInfo>)hs.invoke(obj, "onListButton");
		if (listButton != null && listButton.size() > 0) model.addAttribute("listButton", listButton);
		// Есть ли фильтрация?
		boolean p_filtering = false;
		for (ColumnInfo ci : listColumn) { if (!hs.isEmpty(ci.getFilter())) p_filtering = true; break; }
		model.addAttribute("p_filtering", p_filtering);
		// Сортировка
		if (sort == null) sort = Sort.by("rn").ascending();
		// Пейджинг
		boolean p_paging = (Boolean)hs.invoke(cl.newInstance(), "onListPaginated");
		model.addAttribute("p_paging", p_paging);
		// Получить страницу данных (если страница указана явно, переходим на нее, если нет, то ищем страницу с указанным объектом) 
		Page<Object> listObj = objService.findAll(cl, PageRequest.of(off, p_paging ? page : Integer.MAX_VALUE, sort), attr, value, !offParam.isPresent() ? rn : null);
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
			model.addAttribute("p_min", pageNumbers.size() > 0 ? pageNumbers.get(0) : 0);
			model.addAttribute("p_max", pageNumbers.size() > 0 ? pageNumbers.get(pageNumbers.size() - 1) : 0);
			// Список количества записей на странице
			model.addAttribute("p_countPages", new Integer[]{10, 15, 30, 50, 100, 500, 1000});
		}
		// Запись для выделения
		if (rn != null) model.addAttribute("rn", rn);
		// Класс объектов в списке
		model.addAttribute("clazz", clazz);
		// Показывать все версии документов (или в другой интерпретации)
		model.addAttribute("p_all", p_all);
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
		// Страница по имени класса или страница по умолчанию
		String t = "list" + clazz + "Page";
		return hs.templateExists(t) ? t : "listObjPage";
	}
	// Данные объекта
	@RequestMapping(value = "/detailsObj", method = RequestMethod.GET)
	public String detailsObj(@RequestParam("rn") Optional<Integer> paramRn, 
							 @RequestParam("clazz") Optional<String> paramClazz,
							 @RequestParam("prn") Optional<Integer> paramPrn,
							 @RequestParam("p_tab") Optional<Integer> paramTab, 
							 @RequestParam("readonly") Optional<Integer> paramReadonly,
							 @RequestParam("p_ret") Optional<String> paramRet,
							 HttpServletRequest request,
							 Model model) throws Exception {
		String clazz = paramClazz.orElse(null);
		Integer rn = paramRn.orElse(null);
		if (hs.isEmpty(clazz) && rn != null) clazz = objService.getClassByKey(rn);
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Integer prn = paramPrn.orElse(null);
		Object obj = rn == null ? cl.newInstance() : objService.find(cl, rn);
		if (rn == null) hs.invoke(obj, "onNew");
		if (obj == null) throw new Exception("Не найден объект по имени класса '" + clazz + "' с идентификатором " + rn);
		model.addAttribute("hs", hs);
		if (prn != null) model.addAttribute("prn", prn);
		int ro = paramReadonly.orElse(0);
		model.addAttribute("readonly", ro == 1 ? true : !(Boolean)hs.invoke(obj, "onCheckRights", Operation.update));
		List<String> listNames = Collections.list((Enumeration<String>)request.getParameterNames());
		for (String p : listNames) {
			String v = request.getParameter(p);
			if ("rn".equals(p) || "clazz".equals(p) || "readonly".equals(p) || "prn".equals(p)) continue;
			Class<?> clType =  hs.getAttrType(cl, p);
			if (clType == null) {
				if (!p.startsWith("p_")) model.addAttribute(p, v);
				continue;
			}
			hs.setProperty(obj, p, hs.getObjectByString(v, clType));
		}
		setObjModel(obj, model);
		model.addAttribute("p_tab", paramTab.orElse(1));
		model.addAttribute("p_ret", paramRet.orElse(""));
		String t = "details" + clazz + "Page";
		return hs.templateExists(t) ? t : "detailsObjPage";
	}
	@RequestMapping(value = "/detailsObj", method = RequestMethod.POST)
	public String detailsObjPost(@RequestParam("clazz") Optional<String> paramClazz, 
								 @RequestParam("rn") Optional<Integer> paramRn, 
								 @RequestParam("p_ret") Optional<String> paramRet,
								 HttpServletRequest request,
								 Model model) throws Exception {
		String ip = (String)request.getSession().getAttribute("ip"), browser = (String)request.getSession().getAttribute("browser");
		String p_ret = paramRet.orElse("");
		Integer rn = paramRn.orElse(null);
		String clazz = rn != null ? objService.getClassByKey(rn) : null;
		if (hs.isEmpty(clazz)) clazz = paramClazz.orElse(null);
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) throw new ClassNotFoundException("Не найден класс по имени '" + clazz + "'");
		Object obj = rn == null ? cl.newInstance() : objRepository.find(cl, rn);
		Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
		Map<String, Object> mapItems = new LinkedHashMap<String, Object>();
		// Все кроме файлов
		List<String> listNames = Collections.list((Enumeration<String>)request.getParameterNames());
		for (String p : listNames) {
			String[] vs = request.getParameterValues(p);
			if ("clazz".equals(p) || "rn".equals(p)) continue;
			Object ob = hs.invoke(obj, "onCheckUpdateAttribute", p);
			if (ob != null && !(Boolean)ob) continue;
			int k = p.indexOf("__");
			if (k > 0) {
				String a = p.substring(0, k);
				Class<?> clItem = hs.getAttrType(cl, a);
				if (clItem == null) continue;
				Object o = mapItems.get(a);
				if (o == null) {
					o = new LinkedHashMap<String, Object>();
					mapItems.put(a, o);
				}
				String attr = p.substring(k + 2);
				Map<String, Object> m = (Map<String, Object>)o;
				m.put(attr, Arrays.asList(vs));
			}
			else if (hs.getAttrType(cl, p) != null) mapValue.put(p, hs.getObjectByString(cl, p, vs.length > 0 ? vs[0] : null));
		}
		// Файлы
		Collection<Part> colParts = null;
		try { colParts = request.getParts(); } catch (Exception ex) { }
		if (colParts != null) {
			for (Part part : colParts) {
				String p = part.getName();
				if ("clazz".equals(p) || "rn".equals(p)) continue;
				// Берем только файлы
				if (part.getSubmittedFileName() == null) continue;
				Object op = hs.getObjectByPart(part);
				if (op == null)  continue;
				int k = p.indexOf("__");
				if (k > 0) {
					String a = p.substring(0, k);
					Class<?> clItem = hs.getAttrType(cl, a);
					if (clItem == null) continue;
					Object o = mapItems.get(a);
					if (o == null) {
						o = new LinkedHashMap<String, Object>();
						mapItems.put(a, o);
					}
					String attr = p.substring(k + 2);
					Map<String, Object> m = (Map<String, Object>)o;
					List<Object> l = (List<Object>)m.get(attr);
					if (l == null) {
						l = new ArrayList<Object>();
						m.put(attr, l);
					}
					l.add(op);
				}
				else if (hs.getAttrType(cl, p) != null) {
					mapValue.put(p, hs.getProperty(op, p));
					if (op instanceof IFile && "fileuri".equals(p)) {
						IFile f = (IFile)op;
						mapValue.put("filename", f.getFilename());
						if (f.getFileext() != null) mapValue.put("fileext", f.getFileext());
						if (f.getFiletype() != null) mapValue.put("filetype", "" + f.getFiletype().getRn());
						if (f.getFilelength() != null) mapValue.put("filelength", "" + f.getFilelength());
						if (f.getComment() != null) mapValue.put("comment", "" + f.getComment());
					}
				}
			}
		}
		String redirect = null;
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
			if (rn == null) {
				hs.invoke(obj, "onNew");
			}
			if (!hs.checkRights(obj, Operation.update)) throw new SecurityException("Вы не имеете право на редактирование объекта " + hs.getProperty(obj, "name"));
			mapValue.forEach((attr, valueNew) -> hs.setProperty(obj, attr, valueNew));
			objRepository.saveObj(obj);
			// Добавление информации об изменении объекта
			hs.invoke(obj, "onUpdate");
			// list - атрибут списка
			mapItems.forEach((list, o) -> {
				Map<String, Object> map = (Map<String, Object>)o;
				List<Object> lcmd = (List<Object>)map.get("p_cmd");
				if (lcmd == null) {
					Class<?> clAttr = hs.getAttrType(cl, list);
					if (clAttr != null) {
						Object objAttr = hs.getProperty(obj, list);
						if (objAttr == null) {
							try { objAttr = clAttr.newInstance(); } catch (Exception e) { }
						}
						if (objAttr != null) {
							Object f = null;
							Iterator<String> it = map.keySet().iterator();
							while (it.hasNext()) {
								String ap = it.next();
								List<Object> lvalue = (List<Object>)map.get(ap);
								Object v = lvalue.get(0);
								if (v != null && v instanceof IBase) {
									f = v;
									v = hs.getProperty(v, ap); 
								}
								else v = hs.getObjectByString(clAttr, ap, (String)v);
								hs.setProperty(objAttr, ap, v);
							}
							if (f != null) hs.copyProperties(f, objAttr, true, false);
							objRepository.saveObj(objAttr);
							hs.invoke(objAttr, "onUpdate");
							hs.setProperty(obj, list, objAttr);
						}
					}
				}
				else {
					Class<?> clItem = hs.getItemType(cl, list);
					String clazzItem = clItem.getSimpleName();
					List<Object> lrn = (List<Object>)map.get("rn");
					List<Object> ladd = (List<Object>)map.get("p_add");
					List<Object> lrnOld = (List<Object>)map.get("rnOld");
					for (int i=0; i<lcmd.size(); i++) {
						String cmd = (String)lcmd.get(i);
						if (hs.isEmpty(cmd)) continue;
						String add = (String)ladd.get(i);
						boolean bNew = !"exists".equals(add);
						Integer rnItem = null;
						try { rnItem = Integer.valueOf((String)lrn.get(i)); } catch (Exception ex) { }
						Integer rnItemOld = null;
						try { rnItemOld = Integer.valueOf((String)lrnOld.get(i)); } catch (Exception ex) { }
						Object item = null;
						if ("remove".equals(cmd) && rnItem != null) {
							try { objRepository.executeItem(obj, list, cmd, clazzItem, rnItem, bNew); } catch (Exception ex) { }
						}
						else if ("add".equals(cmd) && rnItem == null) {
							try { item = objRepository.executeItem(obj, list, cmd, clazzItem, null, bNew); } catch (Exception ex) { }
						}
						else if (rnItem != null && ("add".equals(cmd) || "update".equals(cmd))) {
							cmd = "update";
							try {
								item = objRepository.updateItem(obj, list, clazzItem, rnItemOld, rnItem);
								if (item != null && rnItemOld != rnItem) hs.invoke(obj, "onUpdateItem", clItem, rnItemOld, rnItem);
							}
							catch (Exception ex) { }
						}
						if (item != null) {
							Object f = null;
							Iterator<String> it = map.keySet().iterator();
							while (it.hasNext()) {
								String ap = it.next();
								if ("p_cmd".equals(ap) || "clazz".equals(ap) || "rn".equals(ap) || "rnOld".equals(ap) || "p_add".equals(ap)) continue;
								List<Object> lvalue = (List<Object>)map.get(ap);
								Object v = lvalue.get(i);
								if (v != null && v instanceof IBase) {
									f = v;
									v = hs.getProperty(v, ap); 
								}
								else v = hs.getObjectByString(clItem, ap, (String)v);
								/*if (v != null) */hs.setProperty(item, ap, v);
							}
							if (f != null) hs.copyProperties(f, item, true, false);
							objRepository.saveObj(item);
							// Добавление информации об изменении объекта
							hs.invoke(item, "onUpdate");
						}
					}
				}
			});
			// Запись в журнал
			List<ChangeInfo> lci = objectChanged.getObjectChanges();
			for (ChangeInfo ci : lci) objRepository.writeLog(userService.getPrincipal(), ci.getRn(), ci.getClazz(), ci.getValue(), ci.getOp(), ip, browser);
			transactionManager.commit(ts);
			// Переход на страницу
			if ("search".equals(p_ret)) redirect = "/search?p_ret=1";
			else redirect = (String)hs.invoke(obj, "onRedirectAfterUpdate", request);
    	}
    	catch (Exception ex) {
    		transactionManager.rollback(ts);
    		throw ex;
    	}
		if (hs.isEmpty(redirect)) redirect = "/main";
		return "redirect:" + redirect;
	}
	@RequestMapping(value = "/executeItem")
	public String executeItem(@RequestParam("list") String listAttr, 
						@RequestParam("rn") Integer rn, 
						@RequestParam("clazz") Optional<String> paramClazz,
						@RequestParam("add") Optional<String> paramAdd,
						@RequestParam("clazzItem") String clazzItem,
						@RequestParam("cmdItem") Optional<String> cmdItemParam,
						@RequestParam("rnItem") Optional<Integer> rnItemParam,
						HttpServletRequest request,
						Model model) throws Exception {
		String ip = (String)request.getSession().getAttribute("ip"), browser = (String)request.getSession().getAttribute("browser");
		Object obj = objService.find(paramClazz.orElse(null), rn);
		if (obj == null) throw new Exception("Не найден объект с идентификатором " + rn);
		String clazz = obj.getClass().getSimpleName();
		String cmd = cmdItemParam.orElse("add");
		Integer rnItem = rnItemParam.orElse(null);
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
    		objRepository.executeItem(obj, listAttr, cmd, clazzItem, rnItem, !"exists".equals(paramAdd.orElse("new")));
    		// Запись в журнал
			List<ChangeInfo> lci = objectChanged.getObjectChanges();
			for (ChangeInfo ci : lci) objRepository.writeLog(userService.getPrincipal(), ci.getRn(), ci.getClazz(), ci.getValue(), ci.getOp(), ip, browser);
	    	transactionManager.commit(ts);
		}
		catch (Exception ex) {
			transactionManager.rollback(ts);
			throw ex;
		}
		model.addAttribute("hs", hs);
		setObjModel(obj, model);
		String t = "details" + clazz + "Page";
		return hs.templateExists(t) ? t : "detailsObjPage";
	}
	@RequestMapping(value = "/executeObj", method = RequestMethod.GET)
	public String executeObj(@RequestParam("rn") Optional<Integer> rnParam, 
							 @RequestParam("clazz") Optional<String> clazzParam,
							 @RequestParam("param") String param,
							 HttpServletRequest request,
							 Model model) throws Exception {
		String ip = (String)request.getSession().getAttribute("ip"), browser = (String)request.getSession().getAttribute("browser");
		Integer rn = rnParam.orElse(null);
		String clazz = rn != null ? objService.getClassByKey(rn) : null;
		if (hs.isEmpty(clazz)) clazz = clazzParam.orElse(null);
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Object obj = rn != null ? objService.find(cl, rn) : null;
		if (rn != null && obj == null) throw new Exception("Не найден объект по имени класса '" + clazz + "' с идентификатором " + rn);
		if (obj == null) obj = cl.newInstance();
		if (!(Boolean)hs.invoke(obj, "onCheckExecute", param)) throw new Exception("Вам запрещено выполнение функции " + param + " для объекта по имени класса '" + clazz + "' с идентификатором " + rn);
		String redirect = null;
		// Выполнение операции через транзакцию
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
			Object rd = hs.invoke(obj, "onExecute", param, request);
			// Возврат - редирект
			if (rd != null && rd instanceof String) redirect = (String)rd;
			if (rn != null) {
				obj = objRepository.find(obj.getClass(), hs.getProperty(obj, "rn"));
				hs.invoke(obj, "onUpdate");
				objRepository.saveObj(obj);
				// Запись в журнал
				List<ChangeInfo> lci = objectChanged.getObjectChanges();
				for (ChangeInfo ci : lci) objRepository.writeLog(userService.getPrincipal(), ci.getRn(), ci.getClazz(), ci.getValue(), ci.getOp(), ip, browser);
			}
			transactionManager.commit(ts);
		}
		catch (Exception ex) {
			transactionManager.rollback(ts);
			throw ex;
		}
		// Переход на страницу
    	if (hs.isEmpty(redirect)) redirect = (String)hs.invoke(obj, "onRedirectAfterUpdate", request);
		if (hs.isEmpty(redirect)) redirect = "/main";
		return "redirect:" + redirect;
	}
	@RequestMapping(value = "/checkExecuteObj", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String checkExecuteObj(@RequestParam("rn") Optional<Integer> rnParam, 
							 @RequestParam("clazz") Optional<String> clazzParam,
							 @RequestParam("param") String param,
							 Model model) throws Exception {
		String ret = "";
		for (; ;) {
			Integer rn = rnParam.orElse(null);
			String clazz = rn != null ? objService.getClassByKey(rn) : null;
			if (hs.isEmpty(clazz)) clazz = clazzParam.orElse(null);
			Class<?> cl = hs.getClassByName(clazz);
			if (cl == null) break;
			Object obj = rn != null ? objService.find(cl, rn) : cl.newInstance();
			if (obj == null) break;
			String[] ps = param.split(",");
			for (String p : ps) {
				boolean b = (Boolean)hs.invoke(obj, "onCheckExecute", p.trim());
				if (!hs.isEmpty(ret)) ret += ","; 
				ret += (b ? "1" : "0");
			}
			break;
		}
		return ret;
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
		String ip = (String)request.getSession().getAttribute("ip"), browser = (String)request.getSession().getAttribute("browser");
		String msg = "", name = "", clazz = "";
		for (; ;) {
			Object obj = objService.find(paramClazz.orElse(null), rn);
			if (obj == null) { msg = String.format("Не найден объект с идентификатором '%s'", rn); break; }
			clazz = hs.getPropertyString(obj, "clazz");
			name = (String)hs.getProperty(obj, "name");
			if (!hs.checkRights(obj, Operation.delete)) { msg = String.format("Вы не имеете прав на удаление объекта '%s'", name); break; }
			TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
	    	try {
				obj = objRepository.find(obj.getClass(), rn);
				Boolean b = (Boolean)hs.invoke(obj, "onRemove");
				if (b != null && !b) throw new Exception(String.format("Отказано в удалении объекта '%s'", name));
				objRepository.removeObj(obj);
				// Запись в журнал
				List<ChangeInfo> lci = objectChanged.getObjectChanges();
				for (ChangeInfo ci : lci) objRepository.writeLog(userService.getPrincipal(), ci.getRn(), ci.getClazz(), ci.getValue(), ci.getOp(), ip, browser);
				// Сообщение
				msg = String.format("Объект '%s' успешно удален", name);
				transactionManager.commit(ts);
			}
			catch (Exception ex) {
				msg = ex.getMessage();
				transactionManager.rollback(ts);
			}
			break;
		}
		setMainModel(model, "Удаление объекта");
		model.addAttribute("clazz", clazz);
		model.addAttribute("p_message", msg);
		return "removeObjPage";
	}
	@RequestMapping(value = "/removeLog", method = RequestMethod.GET)
	public String removeLog(Model model) throws Exception {
		if (!userService.isAdmin(null)) throw new Exception("Вам запрещено выполнять эту операцию");
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
			List<?> l = objRepository.findAll(ISession.class);
			if (l != null) for (Object o : l) objRepository.removeObj(o);
			l = objRepository.findAll(ActionLog.class);
			if (l != null) for (Object o : l) objRepository.removeObj(o);
			transactionManager.commit(ts);
		}
		catch (Exception ex) {
			transactionManager.rollback(ts);
		}
		return "redirect:/main";
	}
	@RequestMapping(value = "/listVoc", method = RequestMethod.GET)
	public String listVoc(Model model) throws Exception {
		List<Voc> listVoc = new ArrayList<Voc>();
		Class<?>[] classes = hs.getAllClasses();
		for (Class<?> cl : classes) {
			Object o = null;
			try { o = hs.invoke(cl, "isVoc"); } catch (Exception ex) { }
			if (o == null || !(o instanceof Boolean) || !((Boolean)o)) continue;
			listVoc.add(new Voc(cl.getSimpleName(), (String)hs.invoke(cl, "singleTitle")));
		}
		model.addAttribute("listObj", new PageImpl<Voc>(listVoc));
		setMainModel(model, "Справочники системы");
		return "listVocPage";
	}
	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@RequestParam("p_text") Optional<String> textParam,
						 @RequestParam("p_off") Optional<Integer> offParam,
						 @RequestParam("p_page") Optional<Integer> pageParam,
						 @RequestParam("p_block") Optional<Integer> blockParam,
						 @RequestParam("p_ret") Optional<String> retParam,
						 HttpServletRequest request,
						 Model model) throws Exception {
		String text = textParam.orElse("");
		int off = offParam.orElse(0), page = pageParam.orElse(15), block = blockParam.orElse(10);
		if (off < 0) off = 0;
		String p_ret = retParam.orElse("");
		if ("1".equals(p_ret)) {
			HttpSession session = request.getSession();
			Map<String, String[]> mapParam = (Map<String, String[]>)session.getAttribute("search");
			if (mapParam != null) {
				try { text = mapParam.get("p_text")[0]; } catch (Exception ex) { }
				try { off = Integer.valueOf(mapParam.get("p_off")[0]); } catch (Exception ex) { off = 0; }
				try { page = Integer.valueOf(mapParam.get("p_page")[0]); } catch (Exception ex) { page = 15; }
				try { block = Integer.valueOf(mapParam.get("p_block")[0]); } catch (Exception ex) { block = 10; }
			}
		}
		Page<SearchContent> p = objService.search(text, off, page);
		String[] ts = text.split("[,;:.!?\\s]+");
		for (SearchContent cs : p.getContent()) {
			String id = cs.getId();
			int k = id.indexOf("_");
			if (k >= 0) id = id.substring(k + 1);
			cs.setId(id);
			String content = cs.getContent();
			for (String t : ts) {
				int s = 0;
				for (; ;) {
					k = content.indexOf(t, s); 
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
		// Текущая страница
		off = Math.min(p.getNumber(), totalPages > 0 ? totalPages - 1 : 0);
		model.addAttribute("p_off", off);
		int n = block, start = (off / n) * n + 1;
		start = start - (n / 2);
		if (start < 1) start = 1;
		int end = start + n;
		if (end > totalPages) end = totalPages;
		// Список номеров страниц
		List<Integer> pageNumbers = IntStream.rangeClosed(start , end).boxed().collect(Collectors.toList());
		model.addAttribute("p_pageNumbers", pageNumbers);
		model.addAttribute("p_min", pageNumbers.size() > 0 ? pageNumbers.get(0) : 0);
		model.addAttribute("p_max", pageNumbers.size() > 0 ? pageNumbers.get(pageNumbers.size() - 1) : 0);
		// Список количества записей на странице
		model.addAttribute("p_countPages", new Integer[]{10, 15, 30, 50, 100, 500, 1000});
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
		if (!model.containsAttribute("prn")) {
			Integer prn = (Integer)hs.getProperty(obj, "parent__rn");
			if (prn != null) model.addAttribute("prn", prn);
		}
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
		List<ButtonInfo> lb = (List<ButtonInfo>)hs.invoke(obj, "onDetailsButton");
		if (lb != null && lb.size() > 0) model.addAttribute("listButton", lb);
	}
	private void setMainModel(Model model, String title) {
		model.addAttribute("p_login", userService.getPrincipal());
		model.addAttribute("p_roles", userService.getRoles(null));
		model.addAttribute("p_title", title);
	}
}
