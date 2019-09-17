package ru.landar.spring.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Voc;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@Controller
public class PopupController {
	@Autowired
	private ObjService objService;
	@Autowired
	private UserService userService;
	@Autowired
	private HelperService hs;
	@RequestMapping(value = "/popupEdit")
	public String popupEdit(@RequestParam("p_title") Optional<String> pTitleParam,
							HttpServletRequest request, 
							Model model) throws Exception {
		model.addAttribute("p_title", pTitleParam.orElse("Редактирование"));
		return "popupEdit";
	}
	@RequestMapping(value = "/popupVisible")
	public String popupVisible(@RequestParam("clazz") String clazz,
							HttpServletRequest request, 
							Model model) throws Exception {
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Object obj = cl.newInstance();
		model.addAttribute("listColumnAll", (List<ColumnInfo>)hs.invoke(obj, "onListColumn"));
		return "popupVisible";
	}
	@RequestMapping(value = "/popupSelect")
	public String popupSelect(@RequestParam("clazz") String clazz,
							@RequestParam("rn") Optional<String> rnParam,
							@RequestParam("p_title") Optional<String> pTitleParam,
							@RequestParam("p_column") Optional<String> pColumnParam,
							@RequestParam("p_filter") Optional<String> pFilterParam,
							HttpServletRequest request,
							Model model) throws Exception {
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени + '" + clazz + "'");
		String rn = rnParam.orElse(null);
		String column = pColumnParam.orElse("rn;name=Наименование");
		String[] ss = column.split(";");
		String columnId = null, sortId = null, sortType = "A";;
		List<ColumnInfo> listColumn = new ArrayList<ColumnInfo>();
		for (int i=0; i<ss.length; i++) {
			String[] cs = ss[i].split("=");
			if (i == 0) {
				columnId = cs[0];
				model.addAttribute("columnId", columnId);
			}
			else {
				String name = cs[0], title = cs.length > 1 ? cs[1] : "", cv = cs.length > 2 ? cs[2] : "";
				if (title.isEmpty()) {
					List<ColumnInfo> listC = (List<ColumnInfo>)hs.invoke(cl, "listColumn");
					if (listC != null) {
						for (ColumnInfo ci : listC) {
							if (name.equals(ci.getName())) {
								title = ci.getTitle();
								break;
							}
						}
					}
				}
				if (cv.length() < 1) cv += "Y";
				if (cv.length() < 2) cv += "Y";
				if (cv.length() < 3) cv += "N";
				sortType = cv.substring(2, 3);
				String visible = cv.substring(0, 1), target = cv.substring(1, 2);
				if ("A".equals(sortType) || "D".equals(sortType)) sortId = name;
				if (!"Y".equals(visible)) continue;
				ColumnInfo ci = new ColumnInfo(name, title);
				ci.setFilter(target);
				listColumn.add(ci);
			}
		}
		if (sortId == null) { sortId = "rn"; sortType = "A"; }
		model.addAttribute("listColumn", listColumn);
		String[] attr = null;
		Object[] value = null; 
		String filter = pFilterParam.orElse(null);
		if (!hs.isEmpty(filter)) {
			Integer rnDep = hs.getDepartmentKey();
			String roles = userService.getRoles(null);
			List<String> listAttr = new ArrayList<String>();
			List<Object> listValue = new ArrayList<Object>();
			String[] fs = filter.split(";");
			for (String f : fs) {
				int k = f.indexOf(' ');
				if (k < 0) continue;
				String a = f.substring(0, k).trim();
				if (hs.isEmpty(a)) continue;
				String v = f.substring(k + 1).trim();
				if (hs.isEmpty(v)) continue;
				if (v.indexOf("#d#") > 0) {
					if (rnDep == null) continue;
					if (roles.indexOf("DF") > 0) continue;
					v = v.replaceAll("#d#", "" + rnDep);
				}
				else if (v.indexOf("$") >= 0) {
					k = v.indexOf("$");
					int e = v.indexOf("$", k + 1);
					if (e > 0) { 
						String r = request.getParameter(v.substring(k + 1, e));
						if (hs.isEmpty(r)) continue;
						v = v.substring(0, k) + r + v.substring(e + 1);
					}
				}
				a = a.replaceAll(".", "__");
				listAttr.add(a);
				listValue.add(v);
			}
			if (listAttr.size() > 0) attr = listAttr.toArray(new String[listAttr.size()]);
			if (listValue.size() > 0) value = listValue.toArray(new String[listValue.size()]);
		}
		Page<Object> listObj = objService.findAll(cl, PageRequest.of(0, Integer.MAX_VALUE, "A".equals(sortType) ? Sort.by(sortId).ascending() : ("D".equals(sortType) ? Sort.by(sortId).descending() : Sort.unsorted())), attr, value);
		if (columnId != null && !"rn".equals(columnId)) {
			Set<String> setId = new HashSet<String>();
			List<Object> l = new ArrayList<Object>();
			for (Object o : listObj.getContent()) {
				String id = hs.getPropertyString(o, columnId);
				if (setId.contains(id)) continue;
				setId.add(id);
				l.add(o);
			}
			listObj = new PageImpl<Object>(l, Pageable.unpaged(), l.size());
		}
		model.addAttribute("rn", rn);
		model.addAttribute("listObj", listObj);
		model.addAttribute("p_title", pTitleParam.orElse("Выбор"));
		model.addAttribute("hs", hs);
		return "popupSelect";
	}
	@RequestMapping(value = "/popupClasses")
	public String popupClasses(@RequestParam("clazz") String clazz,
							HttpServletRequest request,
							Model model) throws Exception {
		Class<?> cl = hs.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени + '" + clazz + "'");
		Class<?>[] classes = hs.getAllClasses();
		List<Voc> listVoc = new ArrayList<Voc>();
		listVoc.add(new Voc(cl.getSimpleName(), (String)hs.invoke(cl, "singleTitle")));
		for (Class<?> cls : classes) {
			if (cls.getSimpleName().equals(cl.getSimpleName()) || !cl.isAssignableFrom(cls)) continue;
			listVoc.add(new Voc(cls.getSimpleName(), (String)hs.invoke(cls, "singleTitle")));
		}
		model.addAttribute("listClasses", listVoc);
		model.addAttribute("p_title", "Выберите объект, который Вы хотите добавить");
		model.addAttribute("hs", hs);
		return "popupClasses";
	}
}
