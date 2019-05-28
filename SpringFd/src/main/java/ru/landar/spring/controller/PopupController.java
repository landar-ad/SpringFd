package ru.landar.spring.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@Controller
public class PopupController {
	@Autowired
	private ObjService objService;
	@Autowired
	private HelperService hs;
	@RequestMapping(value = "/popupEdit")
	public String popupEdit(@RequestParam("p_title") Optional<String> pTitleParam,
							HttpServletRequest request, 
							Model model) throws Exception {
		model.addAttribute("p_title", pTitleParam.orElse("Редактирование"));
		return "popupEdit";
	}
	@RequestMapping(value = "/popupSelect")
	public String popupSelect(@RequestParam("clazz") String clazz,
							@RequestParam("p_title") Optional<String> pTitleParam,
							@RequestParam("p_column") Optional<String> pColumnParam,
							@RequestParam("p_filter") Optional<String> pFilterParam,
							HttpServletRequest request,
							Model model) throws Exception {
		String column = pColumnParam.orElse("name=Наименование");
		String[] ss = column.split(";");
		List<ColumnInfo> listColumn = new ArrayList<ColumnInfo>();
		for (String s : ss) {
			int k = s.indexOf('=');
			if (k < 0) continue;
			String name = s.substring(0, k), title = s.substring(k + 1);
			listColumn.add(new ColumnInfo(name, title));
		}
		model.addAttribute("listColumn", listColumn);
		Class<?> cl = objService.getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени + '" + clazz + "'");
		String[] attr = null;
		Object[] value = null; 
		String filter = pFilterParam.orElse(null);
		if (!hs.isEmpty(filter)) {
			List<String> listAttr = new ArrayList<String>();
			List<Object> listValue = new ArrayList<Object>();
			String[] fs = filter.split(",");
			for (String f : fs) {
				int k = f.indexOf(' ');
				if (k < 0) continue;
				String a = f.substring(0, k).trim();
				if (hs.isEmpty(a)) continue;
				String v = f.substring(k + 1).trim();
				if (hs.isEmpty(v)) continue;
				listAttr.add(a);
				listValue.add(v);
			}
			if (listAttr.size() > 0) attr = listAttr.toArray(new String[listAttr.size()]);
			if (listValue.size() > 0) value = listValue.toArray(new String[listValue.size()]);
		}
		Page<Object> listObj = objService.findAll(cl, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").ascending()), attr, value);
		model.addAttribute("listObj", listObj);
		model.addAttribute("p_title", pTitleParam.orElse("Выбор"));
		model.addAttribute("hs", hs);
		return "popupSelect";
	}
}
