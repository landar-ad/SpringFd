package ru.landar.spring.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;

@PreAuthorize("hasRole('ROLE_USER')")
@Controller
public class JsonController {
	@Autowired
	private ObjService objService;
	@Autowired
	private HelperService hs;

	@RequestMapping(value = "/json/detailsObj", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String detailsObj(@RequestParam("rn") Optional<Integer> paramRn, 
							 @RequestParam("clazz") Optional<String> paramClazz) throws Exception {
		String ret = "";
		try {
			String clazz = paramClazz.orElse(null);
			Integer rn = paramRn.orElse(null);
			if (hs.isEmpty(clazz) && rn != null) clazz = objService.getClassByKey(rn);
			Class<?> cl = hs.getClassByName(clazz);
			if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
			Object obj = rn == null ? cl.newInstance() : objService.find(cl, rn);
			if (rn == null) hs.invoke(obj, "onNew");
			if (obj == null) throw new Exception("Не найден объект по имени класса '" + clazz + "' с идентификатором " + rn);
			ret = hs.getJsonString(obj);
		}
		catch (Throwable ex) {
			throw ex;
		}
		return ret;
	}
	@RequestMapping(value = "/json/listObj", produces = "application/json")
	@ResponseBody
	public String listObj(@RequestParam("clazz") String clazz,
						  @RequestParam("p_off") Optional<Integer> offParam,
						  @RequestParam("p_page") Optional<Integer> pageParam,
						  HttpServletRequest request) throws Exception {
		String ret = "";
		try {
			int off = offParam.orElse(0), page = pageParam.orElse(15);
			Class<?> cl = hs.getClassByName(clazz);
			if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
			Object obj = cl.newInstance();
			Map<String, String[]> mapParam = request.getParameterMap();
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
				if (hs.isEmpty(v) || "clazz".equals(p) || "p_off".equals(p) || "p_page".equals(p) || "p_block".equals(p)) continue;
				Class<?> attrType = hs.getAttrType(cl, p);
				if (attrType == null) continue;
				listAttr.add(p);
				listValue.add(v);
			}
			// Добавить фильтр, если есть
			hs.invoke(obj, "onListAddFilter", listAttr, listValue, mapParam);
			// Поисковые атрибуты
			String[] attr = listAttr.size() > 0 ? listAttr.toArray(new String[listAttr.size()]) : null;
			Object[] value = listValue.size() > 0 ? listValue.toArray(new Object[listValue.size()]) : null;
			// Список колонок
			List<ColumnInfo> listColumn = (List<ColumnInfo>)hs.invoke(obj, "onListColumn");
			// Получить страницу данных
			Page<Object> listObj = objService.findAll(cl, PageRequest.of(off, page, sort), attr, value);
			int totalPages = listObj.getTotalPages();
			off = Math.min(listObj.getNumber(), totalPages > 0 ? totalPages - 1 : 0);
			// Сформировать JSON
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("totalCount", listObj.getTotalElements());
			map.put("page", off);
			map.put("limit", page);
			List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
			map.put("data", arr);
			for (Object o : listObj.getContent()) {
				Map<String, Object> mapData = new LinkedHashMap<String, Object>();
				mapData.put("rn", hs.getProperty(o, "rn"));
				mapData.put("clazz", hs.getProperty(o, "clazz"));
				for (ColumnInfo ci : listColumn) {
					if (!ci.getVisible()) continue;
					mapData.put(ci.getName(), hs.getPropertyJson(o, ci.getName()));
				}
				arr.add(mapData);
			}
			ObjectMapper mapper = new ObjectMapper();
			ret = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		}
		catch (Throwable ex) {
			throw ex;
		}
		return ret;
	}
	@CrossOrigin
	@RequestMapping(value = "/json/listObjVue", produces = "application/json")
	@ResponseBody
	public String listObjVue(@RequestParam("clazz") String clazz,
						  @RequestParam("page") Optional<Integer> pageParam,
						  @RequestParam("limit") Optional<Integer> limitParam,
						  HttpServletRequest request) throws Exception {
		String ret = "";
		try {
			int off = pageParam.orElse(0), page = limitParam.orElse(15);
			Class<?> cl = hs.getClassByName(clazz);
			if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
			Object obj = cl.newInstance();
			Map<String, String[]> mapParam = request.getParameterMap();
			// Поисковые атрибуты
			List<String> listAttr = new ArrayList<String>();
			List<Object> listValue = new ArrayList<Object>();
			// Данные для фильтрации
			Map<String, Object> mapFilter = new HashMap<String, Object>();
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
				if (hs.isEmpty(v) || "clazz".equals(p) || "page".equals(p) || "limit".equals(p)) continue;
				Class<?> attrType = hs.getAttrType(cl, p);
				if (attrType == null) continue;
				listAttr.add(p);
				listValue.add(v);
			}
			// Добавить фильтр, если есть
			hs.invoke(obj, "onListAddFilter", listAttr, listValue, mapParam);
			// Поисковые атрибуты
			String[] attr = listAttr.size() > 0 ? listAttr.toArray(new String[listAttr.size()]) : null;
			Object[] value = listValue.size() > 0 ? listValue.toArray(new Object[listValue.size()]) : null;
			if (attr != null && value != null) for (int i=0; i<attr.length; i++) mapFilter.put(attr[i], value[i]);
			// Список колонок
			List<ColumnInfo> listColumn = (List<ColumnInfo>)hs.invoke(obj, "onListColumn");
			// Получить страницу данных
			Page<Object> listObj = objService.findAll(cl, PageRequest.of(off, page, sort), attr, value);
			int totalPages = listObj.getTotalPages();
			off = Math.min(listObj.getNumber(), totalPages > 0 ? totalPages - 1 : 0);
			// Сформировать JSON
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("totalCount", listObj.getTotalElements());
			map.put("page", off);
			map.put("limit", page);
			List<Map<String, Object>> arr = new ArrayList<Map<String, Object>>();
			// Кнопки
			map.put("buttons", arr);
			List<ButtonInfo> listButton = (List<ButtonInfo>)hs.invoke(obj, "onListButton");
			for (ButtonInfo bi : listButton) {
				Map<String, Object> mapData = new LinkedHashMap<String, Object>();
				mapData.put("name", bi.getName());
				mapData.put("title", bi.getTitle());
				if (bi.getIcon() != null) mapData.put("icon", bi.getIcon());
				mapData.put("color", bi.getColor());
				arr.add(mapData);
			}
			// Заголовок таблицы
			arr = new ArrayList<Map<String, Object>>();
			map.put("headers", arr);
			for (ColumnInfo ci : listColumn) {
				Map<String, Object> mapData = new LinkedHashMap<String, Object>();
				String key = ci.getName();
				mapData.put("name", key);
				mapData.put("title", ci.getTitle());
				mapData.put("visible", ci.getVisible());
				String sortData = null;
				if (ci.getSortable()) {
					sortData = "NONE";
					if (mapSort.containsKey(key)) sortData = mapSort.get(key);
				}
				mapData.put("sort", sortData);
				Map<String, Object> filterData = null;
				if (ci.getFilter() != null) {
					filterData = new LinkedHashMap<String, Object>();
					key = ci.getFilter();
					filterData.put("name", key);
					filterData.put("type", ci.getFilterType());
					if (ci.getFilterList() != null) {
						
						filterData.put("list", ci.getFilterList());
					}
					if (mapFilter.containsKey(key)) filterData.put("value", mapFilter.get(key));
				}
				mapData.put("filter", filterData);
				arr.add(mapData);
			}
			arr = new ArrayList<Map<String, Object>>();
			map.put("items", arr);
			for (Object o : listObj.getContent()) {
				Map<String, Object> mapData = new LinkedHashMap<String, Object>();
				for (ColumnInfo ci : listColumn) {
					mapData.put(ci.getName(), hs.getPropertyJson(o, ci.getName()));
				}
				arr.add(mapData);
			}
			ObjectMapper mapper = new ObjectMapper();
			ret = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		}
		catch (Throwable ex) {
			throw ex;
		}
		return ret;
	}
}
