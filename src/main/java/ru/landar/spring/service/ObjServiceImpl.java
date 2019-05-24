package ru.landar.spring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.landar.spring.model.ActionLog;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.SearchContent;
import ru.landar.spring.model.SpActionType;
import ru.landar.spring.model.ISettings;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.repository.solr.ObjSolrRepository;

@Service
@Transactional
public class ObjServiceImpl implements ObjService {

	@Value("${spring.data.solr.host}") String solrURL;
	
	@Autowired
    private ObjRepositoryCustom objRepository;
	
	@Autowired
    private ObjSolrRepository solrRepository;
	
	@Autowired
    private HelperService hs;
	
	@Autowired
    private UserService userService;
	
	@Override
	public Object createObj(Object obj) {
		return objRepository.createObj(obj);
	}
	@Override
	public Object updateObj(Object obj) {
		return objRepository.updateObj(obj);
	}
	@Override
	public Object saveObj(Object obj) {
		Object ret = objRepository.saveObj(obj);
		SearchContent content = null;
		try { content = (SearchContent)hs.invoke(ret, "buildContent"); } catch (Exception ex) { } 
		if (content != null) 
			try { 
				if (hs.isServerConnected(solrURL, 3000)) 
					solrRepository.save(content); 
			} 
			catch (Exception ex) { }
		return ret;
	}
	@Override
    public Object find(Class<?> cl, Object pk) {
		return objRepository.find(cl, pk);
    }
	@Override
    public Object find(String clazz, Object pk) throws Exception {
		if (clazz == null) clazz = objRepository.getClassByKey(pk);
		if (clazz == null) throw new Exception("Не найден класс объекта с идентификатором " + pk);
		Class<?> cl = getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		return find(cl, pk);
	}
	@Override
	public Object find(Class<?> cl, String attr, Object value) {
		return objRepository.find(cl, attr, value);
	}
	@Override
	public Page<Object> findAll(Class<?> cl, Pageable p, String[] attr, Object[] value) {
		return objRepository.findAll(cl, p, attr, value);
	}
	@Override
	public Page<Map<String, Object>> findAllResult(Class<?> cl, String[] result, Pageable p, String[] attr, Object[] value) {
		return objRepository.findAllResult(cl, result, p, attr, value);
	}
	@Override
	public List<Object> findAll(Class<?> cl) throws Exception {
		return findAll(cl, false);
	}
	@Override
	public List<Object> findAll(Class<?> cl, boolean empty) throws Exception {
		List<Object> l = objRepository.findAll(cl);
		if (empty) l.add(0, (Object)cl.newInstance());
		return l;
	}
	@Override
	public Object getObjByCode(Class<?> cl, String code) {
		return objRepository.findByCode(cl, code);
	}
	@Override
	public String getClassByKey(Integer rn) {
		return objRepository.getClassByKey(rn);
	}
	@Override
	public Class<Object> getClassByName(String clazz) {
		Class<Object> ret = null;
		try { ret = (Class<Object>)Class.forName(IBase.class.getName().substring(0, IBase.class.getName().lastIndexOf('.') + 1) + clazz); } catch (Exception ex) { }
		return ret;
	}
	@Override
	public void removeObj(Object obj) {
		objRepository.removeObj(obj);
	}
	@Override
	public void removeObj(String clazz, Integer rn) throws Exception {
		if (clazz == null) clazz = objRepository.getClassByKey(rn);
		if (clazz == null) throw new Exception("Не найдено имя класса объекта с идентификатором " + rn);
		Class<Object> cl = getClassByName(clazz);
		if (cl == null) throw new Exception("Не найден класс по имени '" + clazz + "'");
		Object obj = find(cl, rn);
		if (obj == null) throw new Exception("Не найден объект '" + clazz + "' с идентификатором " + rn);
		removeObj(obj);
	}
	@Override
	public Object getSettings(String code, String type) {
		Object ret = null;
		ISettings s = loadSettings(code, type);
		if (s != null) {
			String v = s.getValue(), sType = s.getType();
			Class<?> clType = String.class;
			if ("int".equalsIgnoreCase(type) || "integer".equalsIgnoreCase(type)) clType = Integer.class;
			else if ("long".equalsIgnoreCase(sType)) clType = Long.class;
			else if ("float".equalsIgnoreCase(sType)) clType = Float.class;
			else if ("double".equalsIgnoreCase(sType)) clType = Double.class;
			else if ("date".equalsIgnoreCase(sType) || "datetime".equalsIgnoreCase(type)) clType = Date.class;
			ret = hs.getObjectByString(v, clType);
		}
		return ret;
	}
	@Override
	public ISettings loadSettings(String code, String type) {
		ISettings ret = null;
		String r = userService.getRoles(null), login = userService.getPrincipal();
		Page<Object> list = findAll(ISettings.class, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("username").descending()), new String[] {"code", "type"}, new Object[] { code, type });
		for (Object base : list.getContent()) {
			ISettings s = (ISettings)base;
			if (!hs.isEmpty(s.getUsername())) {
				if (!s.getUsername().equals(login)) continue;
			}
			else {
				String roles = s.getRoles();
				if (!hs.isEmpty(roles)) {
					boolean bCont = true;
					String[] rrs = roles.split(",");
					for (String rr : rrs) if (r.indexOf(rr) >= 0) { bCont = false; break; }
					if (bCont) continue;
				}
			}
			ret = s;
		}
		return ret;
	}
	@Override
	public Page<SearchContent> search(String text, int off, int page) {
		try { 
			if (hs.isServerConnected(solrURL, 3000)) 
				return solrRepository.find(text, PageRequest.of(off, page)); 
		} 
		catch (Exception ex) { }
		return new PageImpl<SearchContent>(new ArrayList<SearchContent>());
	}
	@Override
	public void writeLog(String user_login, Object obj, Map<String, Object[]> mapChanged, String op, String ip, String browser) {
		Integer obj_rn = (Integer)hs.getProperty(obj, "rn");
		SpActionType action_type = (SpActionType)getObjByCode(SpActionType.class, op);
		Date dt = new Date();
		String obj_name = obj.getClass().getSimpleName();
		if (mapChanged != null) {
			if (!mapChanged.isEmpty()) mapChanged.forEach((attr, o) -> {
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
	    		saveObj(al);
			});
		}
		else {
			ActionLog al = new ActionLog();
			al.setUser_login(user_login);
			al.setAction_type(action_type);
			al.setAction_time(dt);
			al.setClient_ip(ip);
			al.setClient_browser(browser);
			al.setObj_name(obj_name);
			al.setObj_rn(obj_rn);
			saveObj(al);
		}
	}
	@Override
	public void executeItem(Object obj, String listAttr, String cmd, String clazzItem, Integer rnItem) throws Exception {
		Object listObj = hs.getProperty(obj, listAttr);
		if (listObj == null || !(listObj instanceof List)) throw new Exception("Не найден список '" + listAttr + "'");
		List list = (List<?>)listObj;
		Class<?> clItem = getClassByName(clazzItem);
		if (clItem == null) throw new ClassNotFoundException("Не найден класс по имени '" + clazzItem + "'");
		if ("add".equals(cmd)) {
			Object item = rnItem != null ? find(clItem, rnItem) : clItem.newInstance();
			if (rnItem == null) {
				hs.invoke(item, "onNew");
				saveObj(item);
			}
			list.add(item);
			hs.setProperty(obj, listAttr, list);
			saveObj(obj);
		}
		else if ("remove".equals(cmd)) {
			if (rnItem == null) throw new Exception("Не задан идентификатор объекта для удаления из списка");
			for (int i=0; i<list.size(); i++) {
				Object o = list.get(i);
				if (!(o instanceof IBase) || rnItem.compareTo(((IBase)o).getRn()) != 0) continue;
				list.remove(i);
				break;
			}
			hs.setProperty(obj, listAttr, list);
			saveObj(obj);
			
			Object objItem = find(clItem, rnItem);
			if (objItem == null) throw new Exception("Не найден объект " + clazzItem + " по идентификатору " + rnItem);
			removeObj(objItem);
		}
	}
}

