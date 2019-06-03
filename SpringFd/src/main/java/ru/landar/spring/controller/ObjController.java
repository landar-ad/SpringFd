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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

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

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.SearchContent;
import ru.landar.spring.model.SpActStatus;
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
						  @RequestParam("p_listVisible") Optional<String> listVisibleParam,
						  HttpServletRequest request, 
						  Model model) throws Exception {
		int off = offParam.orElse(0), page = pageParam.orElse(15), block = blockParam.orElse(10);
		Integer rn = rnParam.orElse(null);
		String listVisible = listVisibleParam.orElse(null);
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
				"p_listVisible".equals(p)) continue;
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
		// Страница по имени класса или страница по умолчанию
		String t = "list" + clazz + "Page";
		return hs.templateExists(t) ? t : "listObjPage";
	}
	@RequestMapping(value = "/detailsObj", method = RequestMethod.GET)
	public String detailsObj(@RequestParam("rn") Optional<Integer> paramRn, 
							 @RequestParam("clazz") String clazz,
							 @RequestParam("prn") Optional<Integer> paramPrn,
							 @RequestParam("p_tab") Optional<Integer> paramTab, 
							 @RequestParam("readonly") Optional<Integer> paramReadonly, 
							 Model model) throws Exception {
		Class<?> cl = objService.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Integer rn = paramRn.orElse(null);
		Integer prn = paramPrn.orElse(null);
		Object obj = rn == null ? cl.newInstance() : objService.find(cl, rn);
		if (rn == null) hs.invoke(obj, "onNew");
		if (obj == null) throw new Exception("Не найден объект по имени класса '" + clazz + "' с идентификатором " + rn);
		model.addAttribute("hs", hs);
		if (prn != null) model.addAttribute("prn", prn);
		if (paramReadonly.orElse(0) == 1) model.addAttribute("readonly", true);
		setObjModel(obj, model);
		model.addAttribute("p_tab", paramTab.orElse(1));
		String t = "details" + clazz + "Page";
		return hs.templateExists(t) ? t : "detailsObjPage";
	}
	@RequestMapping(value = "/detailsObj", method = RequestMethod.POST)
	public String detailsObjPost(@RequestParam("clazz") String clazz, 
								 @RequestParam("rn") Optional<Integer> paramRn, 
								 HttpServletRequest request,
								 Model model) throws Exception {
		String ip = (String)request.getSession().getAttribute("ip"), browser = (String)request.getSession().getAttribute("browser");
		Integer rn = paramRn.orElse(null);
		Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
		Class<?> cl = objService.getClassByName(clazz);
		if (cl == null) throw new ClassNotFoundException("Не найден класс по имени '" + clazz + "'");
		Map<String, Object> mapItems = new LinkedHashMap<String, Object>();
		// Все кроме файлов
		List<String> listNames = Collections.list((Enumeration<String>)request.getParameterNames());
		for (String p : listNames) {
			String[] vs = request.getParameterValues(p);
			if ("clazz".equals(p) || "rn".equals(p)) continue;
			int k = p.indexOf("__");
			if (k > 0)
			{
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
				// Берем только файлы
				if (part.getSubmittedFileName() == null) continue;
				if ("clazz".equals(p) || "rn".equals(p)) continue;
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
					l.add(hs.getObjectByPart(part));
				}
				else if (hs.getAttrType(cl, p) != null) {
					Object o = hs.getObjectByPart(part);
					if (o != null) { 
						mapValue.put(p, hs.getProperty(o, p));
						if (o instanceof IFile && "fileuri".equals(p)) {
							IFile f = (IFile)o;
							if (f.getFilename() != null) mapValue.put("filename", f.getFilename());
							if (f.getFileext() != null) mapValue.put("fileext", f.getFileext());
							if (f.getFiletype() != null) mapValue.put("filetype", "" + f.getFiletype().getRn());
							if (f.getFilelength() != null) mapValue.put("filelength", "" + f.getFilelength());
						}
					}
				}
			}
		}
		// Изменение объекта
		Object obj = rn == null ? cl.newInstance() : objService.find(cl, rn);
		if (rn == null) hs.invoke(obj, "onNew");
		if (!hs.checkRights(obj, Operation.update)) throw new SecurityException("Вы не имеете право на редактирование объекта " + hs.getProperty(obj, "name"));
		Map<String, Object[]> mapChanged = new LinkedHashMap<String, Object[]>();
		mapValue.forEach((attr, valueNew) -> {
			Object valueOld = hs.getProperty(obj, attr);
			if (!hs.equals(valueOld, valueNew)) {
				mapChanged.put(attr, new Object[]{valueOld, valueNew});
				hs.setProperty(obj, attr, valueNew);
			}
		});
		// list - атрибут списка
		mapItems.forEach((list, o) -> {
			Map<String, Object> map = (Map<String, Object>)o;
			List<Object> lcmd = (List<Object>)map.get("p_cmd");
			if (lcmd == null) return;
			List<Object> lrn = (List<Object>)map.get("rn");
			List<Object> lclazz = (List<Object>)map.get("clazz");
			List<Object> ladd = (List<Object>)map.get("p_add");
			for (int i=0; i<lcmd.size(); i++) {
				String cmd = (String)lcmd.get(i);
				if (hs.isEmpty(cmd)) continue;
				String add = (String)ladd.get(i);
				boolean bNew = !"exists".equals(add);
				Integer rnItem = null;
				try { rnItem = Integer.valueOf((String)lrn.get(i)); } catch (Exception ex) { }
				String clazzItem = (String)lclazz.get(i); 
				Class<?> clItem = objService.getClassByName(clazzItem);
				if (clItem == null) continue;
				Object item = null;
				if ("remove".equals(cmd) && rnItem != null) {
					try { objService.executeItem(obj, list, cmd, clazzItem, rnItem, bNew); } catch (Exception ex) { }
				}
				else if ("add".equals(cmd) && rnItem == null) {
					try { 
						item = objService.executeItem(obj, list, cmd, clazzItem, null, bNew);
					} catch (Exception ex) { }
				}
				else if (rnItem != null && ("add".equals(cmd) || "update".equals(cmd))) {
					item = objService.find(clItem, rnItem);
				}
				if (item != null) {
					Map<String, Object[]> mapChangedItem = new LinkedHashMap<String, Object[]>();
					Object f = null;
					Iterator<String> it = map.keySet().iterator();
					while (it.hasNext()) {
						String ap = it.next();
						if ("p_cmd".equals(ap) || "clazz".equals(ap) || "rn".equals(ap) || "p_add".equals(ap)) continue;
						List<Object> lvalue = (List<Object>)map.get(ap);
						Object v = lvalue.get(i);
						if (v != null && v instanceof IBase) {
							f = v;
							v = hs.getProperty(v, ap); 
						}
						else v = hs.getObjectByString(clItem, ap, (String)v);
						if (v != null) {
							Object valueOld = hs.getProperty(item, ap);
							if (!hs.equals(valueOld, v)) {
								mapChangedItem.put(ap, new Object[]{valueOld, v});
								hs.setProperty(obj, ap, v);
							}
						}
					}
					if (f != null) hs.copyProperties(f, item, true);
					objService.saveObj(item);
					// Добавление информации об изменении объекта
					hs.invoke(item, "onUpdate", mapItems, mapChangedItem);
					// Запись в журнал
					objService.writeLog(userService.getPrincipal(), 
										item, 
										mapChangedItem, 
										"add".equals(cmd) ? "create" : "update", 
										ip, 
										browser);
				}
			}
		});
		objService.saveObj(obj);
		// Добавление информации об изменении объекта
		hs.invoke(obj, "onUpdate", mapItems, mapChanged);
		// Запись в журнал
		objService.writeLog(userService.getPrincipal(), 
							obj, 
							mapChanged, 
							rn == null ? "create" : "update", 
							ip, 
							browser);
		// Переход на страницу
		String redirect = (String)hs.invoke(obj, "onRedirectAfterUpdate");
		if (hs.isEmpty(redirect)) redirect = "mainPage";
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
		Object obj = objService.find(paramClazz.orElse(null), rn);
		if (obj == null) throw new Exception("Не найден объект с идентификатором " + rn);
		String clazz = obj.getClass().getSimpleName();
		String cmd = cmdItemParam.orElse("add");
		Integer rnItem = rnItemParam.orElse(null);
		objService.executeItem(obj, listAttr, cmd, clazzItem, rnItem, "new".equals(paramAdd.orElse("new")));
		model.addAttribute("hs", hs);
		setObjModel(obj, model);
		String t = "details" + clazz + "Page";
		return hs.templateExists(t) ? t : "detailsObjPage";
	}
	@RequestMapping(value = "/executeObj", method = RequestMethod.GET)
	public String executeObj(@RequestParam("rn") Optional<Integer> rnParam, 
							 @RequestParam("clazz") String clazz,
							 @RequestParam("param") String param,
							 HttpServletRequest request,
							 Model model) throws Exception {
		Class<?> cl = objService.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Integer rn = rnParam.orElse(null);
		Object obj = rn != null ? objService.find(cl, rn) : null;
		if (rn != null && obj == null) throw new Exception("Не найден объект по имени класса '" + clazz + "' с идентификатором " + rn);
		if (obj == null) obj = cl.newInstance();
		if (!(Boolean)hs.invoke(obj, "onCheckExecute", param)) throw new Exception("Вам запрещено выполнение функции " + param + " для объека по имени класса '" + clazz + "' с идентификатором " + rn);
		if (rn != null) hs.invoke(obj, param, request); else hs.invoke(cl, param, request);
		// Переход на страницу
		String redirect = (String)hs.invoke(obj, "onRedirectAfterUpdate");
		if (hs.isEmpty(redirect)) redirect = "mainPage";
		return "redirect:" + redirect;
	}
	@RequestMapping(value = "/checkExecuteObj", method = RequestMethod.GET, produces = "text/plain")
	@ResponseBody
	public String checkExecuteObj(@RequestParam("rn") Optional<Integer> rnParam, 
							 @RequestParam("clazz") String clazz,
							 @RequestParam("param") String param,
							 Model model) throws Exception {
		boolean b = false;
		for (; ;) {
			Class<?> cl = objService.getClassByName(clazz);
			if (cl == null) break;
			Integer rn = rnParam.orElse(null);
			Object obj = rn != null ? objService.find(cl, rn) : cl.newInstance();
			if (obj == null) break;
			b = (Boolean)hs.invoke(obj, "onCheckExecute", param);
			break;
		}
		return b ? "1" : "0";
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
			objService.writeLog(userService.getPrincipal(), 
								obj, 
								null, 
								"remove", 
								(String)request.getSession().getAttribute("ip"), 
								(String)request.getSession().getAttribute("browser"));
			msg = String.format("Объект '%s' успешно удален", name);
			break;
		}
		setMainModel(model, "Удаление объекта");
		model.addAttribute("p_message", msg);
		return "removeObjPage";
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
		if (!model.containsAttribute("prn")) {
			Integer prn = (Integer)hs.getProperty(obj, "parent__rn");
			if (prn != null) model.addAttribute("prn", prn);
		}
		if (!model.containsAttribute("readonly")) model.addAttribute("readonly", !hs.checkRights(obj, Operation.update)); 
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
