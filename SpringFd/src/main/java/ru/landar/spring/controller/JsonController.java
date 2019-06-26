package ru.landar.spring.controller;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
