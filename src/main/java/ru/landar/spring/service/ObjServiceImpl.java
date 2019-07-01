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

import ru.landar.spring.model.SearchContent;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.ISettings;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.repository.solr.ObjSolrRepository;

@Service
@Transactional
public class ObjServiceImpl implements ObjService {
	@Value("${spring.data.solr.host}") String solrURL;
	@Value("${server.servlet.context-path}") String serverContext;
	@Autowired
    private ObjSolrRepository solrRepository;
	@Autowired
    private HelperService hs;
	@Autowired
    private ObjRepositoryCustom objRepository;
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
		return objRepository.saveObj(obj);
	}
	@Override
    public Object find(Class<?> cl, Object pk) {
		return objRepository.find(cl, pk);
    }
	@Override
    public Object find(String clazz, Object pk) throws Exception {
		if (clazz == null) clazz = objRepository.getClassByKey(pk);
		if (clazz == null) throw new Exception("Не найден класс объекта с идентификатором " + pk);
		Class<?> cl = hs.getClassByName(clazz);
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
	public void removeObj(Object obj) {
		objRepository.removeObj(obj);
	}
	@Override
	public void removeObj(String clazz, Integer rn) throws Exception {
		if (clazz == null) clazz = objRepository.getClassByKey(rn);
		if (clazz == null) throw new Exception("Не найдено имя класса объекта с идентификатором " + rn);
		Class<?> cl = hs.getClassByName(clazz);
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
			if (hs.isServerConnected(solrURL, 3000)) {
				return solrRepository.find(text, getServiceContext(), PageRequest.of(off, page));
			}
		} 
		catch (Exception ex) { }
		return new PageImpl<SearchContent>(new ArrayList<SearchContent>());
	}
	@Override
	public void writeLog(String user_login, Integer rn, String clazz, Map<String, Object[]> mapChanged, Operation op, String ip, String browser) {
		objRepository.writeLog(user_login, rn, clazz, mapChanged, op, ip, browser);
	}
	@Override
	public Object executeItem(Object obj, String listAttr, String cmd, String clazzItem, Integer rnItem, boolean bNew) throws Exception {
		return objRepository.executeItem(obj, listAttr, cmd, clazzItem, rnItem, bNew);
	}
	@Override
	public String getServiceContext() {
		if (hs.isEmpty(serverContext)) return "";
		int k = serverContext.lastIndexOf('/');
		return k < 0 ? serverContext : serverContext.substring(k + 1);
	}
}

