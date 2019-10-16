package ru.landar.spring.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Voc;
import ru.landar.spring.model.assets.SpObjectLocation;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.HelperServiceImpl;
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
							@RequestParam("p_sz") Optional<String> pSzParam,
							HttpServletRequest request, 
							Model model) throws Exception {
		model.addAttribute("p_title", pTitleParam.orElse("Редактирование"));
		model.addAttribute("p_sz", pSzParam.orElse("lg"));
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
				ColumnInfo ci = new ColumnInfo(name, title, true, true, target, "text", null, null);
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
				a = a.replaceAll("[.]", "__");
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
		listVoc.add(new Voc(cl.getSimpleName(), (String)HelperServiceImpl.getAttrInfo(cl, null, "single")));
		for (Class<?> cls : classes) {
			if (cls.getSimpleName().equals(cl.getSimpleName()) || !cl.isAssignableFrom(cls)) continue;
			listVoc.add(new Voc(cls.getSimpleName(), (String)HelperServiceImpl.getAttrInfo(cls, null, "single")));
		}
		model.addAttribute("listClasses", listVoc);
		model.addAttribute("p_title", "Выберите объект, который Вы хотите добавить");
		model.addAttribute("hs", hs);
		return "popupClasses";
	}
	@RequestMapping(value = "/popupAddress")
	public String popupAddress(@RequestParam("p_title") Optional<String> pTitleParam,
							@RequestParam("addr") Optional<String> addrParam,
							@RequestParam("addr_code") Optional<String> addrCodeParam,
							HttpServletRequest request, 
							Model model) throws Exception {
		String addr = addrParam.orElse(null), addr_code = addrParam.orElse(null);
		if (!hs.isEmpty(addr_code)) {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost("https://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/address");
			List <NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("query", addr_code));
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			CloseableHttpResponse response = httpclient.execute(httpPost);

			try {
			    HttpEntity entity = response.getEntity();
		    	InputStream is = entity.getContent();
		    	
			    EntityUtils.consume(entity);
			} finally {
			    response.close();
			}
		}
		model.addAttribute("listRegion", objService.findAll(SpObjectLocation.class));
		model.addAttribute("p_title", pTitleParam.orElse("Редактирование"));
		model.addAttribute("hs", hs);
		return "popupAddress";
	}
	@RequestMapping(value = "/popupQuestion")
	public String popupQuestion(@RequestParam("p_title") Optional<String> pTitleParam,
							@RequestParam("p_text") Optional<String> pTextParam,
							@RequestParam("p_buttons") Optional<String> pButtonsParam,
							@RequestParam("p_sz") Optional<String> pSzParam,
							HttpServletRequest request, 
							Model model) throws Exception {
		model.addAttribute("p_title", pTitleParam.orElse("Вопрос"));
		model.addAttribute("p_text", pTextParam.orElse(""));
		String buttons = pButtonsParam.orElse("yes=Да;no=Нет");
		List<ButtonInfo> listButton = new ArrayList<ButtonInfo>();
		for (String button : buttons.split(";")) {
			String[] ob = button.split("=");
			listButton.add(new ButtonInfo(ob[0], ob.length > 1 ? ob[1] : null, ob.length > 2 ? ob[2] : null, ob.length > 3 ? ob[3] : null));
		}
		model.addAttribute("p_buttons", listButton);
		model.addAttribute("p_sz", pSzParam.orElse("sm"));
		model.addAttribute("hs", hs);
		return "popupQuestion";
	}
}
