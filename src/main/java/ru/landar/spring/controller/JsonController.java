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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

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
						  @RequestParam("p_block") Optional<Integer> blockParam,
						  HttpServletRequest request) throws Exception {
		String ret = "";
		try {
			int off = offParam.orElse(0), page = pageParam.orElse(15), block = blockParam.orElse(10);
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
			hs.invoke(obj, "onListAddFilter", listAttr, listValue);
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
}
