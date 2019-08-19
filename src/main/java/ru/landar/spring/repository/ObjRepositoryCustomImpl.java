package ru.landar.spring.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.landar.spring.ObjectChanged;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.ActionLog;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.ISession;
import ru.landar.spring.model.SearchContent;
import ru.landar.spring.model.SpActionType;
import ru.landar.spring.repository.solr.ObjSolrRepository;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.UserService;

@Repository
public class ObjRepositoryCustomImpl implements ObjRepositoryCustom {
	@Value("${spring.data.solr.host}") String solrURL;
	@Autowired
    private ObjSolrRepository solrRepository;
	@Autowired
	UserService userService;
	@Autowired
	HelperService hs;
	@Resource(name = "getObjectChanged")
    ObjectChanged objectChanged;
	@PersistenceContext
    private EntityManager em;
	@Override
	public 
	EntityTransaction beginTransaction() {
		return em.getTransaction();
	}
	@Override
	public Object createObj(Object obj) {
		if (obj instanceof IBase) {
			String principal = userService.getPrincipal();
			((IBase)obj).setCreator(principal);
			((IBase)obj).setModifier(principal);
		}
		em.persist(obj);
		em.flush();
		Object ret = em.find(obj.getClass(), hs.getProperty(obj, "rn"));
		objectChanged.addObject(ret, Operation.create);
		return ret;
	}
	@Override
	public Object updateObj(Object obj) {
		if (obj instanceof IBase) {
			Date d = new Date();
			((IBase)obj).setMdate(d);
			((IBase)obj).setModifier(userService.getPrincipal());
		}
		Object ret = em.merge(obj);
		em.flush();
		em.close();
		return ret;
	}
	@Override
	public Object saveObj(Object obj) {
		Object ret = null;
		if (obj instanceof IBase) ret = ((IBase)obj).getRn() != null ? updateObj(obj) : createObj(obj);
		if (obj instanceof ISession) ret = ((ISession)obj).getId() != null ? updateObj(obj) : createObj(obj);
		SearchContent content = null;
		try { content = (SearchContent)hs.invoke(ret, "onBuildContent"); } catch (Exception ex) { } 
		if (content != null) 
			try { 
				if (hs.isServerConnected(solrURL, 3000)) 
					solrRepository.save(content); 
			} 
			catch (Exception ex) { }
		return ret;
	}
	@Override
	public void removeObj(Object obj) { 
		objectChanged.addObject(obj, Operation.delete);
		Integer rn = (Integer)hs.getProperty(obj, "rn");
		em.remove(obj);
		try { 
			if (hs.isServerConnected(solrURL, 3000)) 
				solrRepository.deleteById("fd_" + rn); 
		} 
		catch (Exception ex) { }
	}
	@Override
	public Object find(Class<?> cl, Object pk) { return em.find(cl, pk); }
	@Override
	public Object find(Class<?> cl, String attr, Object value) {
		Object ret = null;
		Page<Object> p = findAll(cl, null, new String[] { attr }, new Object[] { value });
		if (p != null && !p.isEmpty()) ret = p.getContent().get(0);
		return ret;
	}
	@Override
	public Object getMaxAttr(Class<?> cl, String attr) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<?> query = cb.createQuery(hs.getAttrType(cl, attr));
		Root<?> root = query.from(cl);
		query.select(cb.max(root.get(attr)));
		return em.createQuery(query).getSingleResult();
	}
	@Override
	public Page<Object> findAll(Class<?> cl, Pageable p, String[] attr, Object[] value) {
		boolean paged = p != null && p.isPaged() && p.getPageSize() != Integer.MAX_VALUE;
		List<Object> l = new ArrayList<Object>();
		long count = findAllCount(cl, attr, value);
		if (count > 0) {
			CriteriaQuery<Object> query = (CriteriaQuery<Object>)createAllQuery(cl, null, attr, value, p != null ? p.getSort() : null, false);
	        TypedQuery<Object> q = em.createQuery(query);
	        if (paged) {
	        	int off = p.getPageNumber() * p.getPageSize();
	        	if (off > count) {
	        		off = 0;
	        		p = PageRequest.of(0, p.getPageSize(), p.getSort());
	        	}
	        	q = q.setFirstResult(off);
	        	q = q.setMaxResults(p.getPageSize());
	        }
	        l = q.getResultList();
		}
        return new PageImpl<Object>(l, paged ? p : Pageable.unpaged(), count);
	}
	@Override
	public Page<Map<String, Object>> findAllResult(Class<?> cl, String[] result, Pageable p, String[] attr, Object[] value) {
		boolean paged = p != null && p.isPaged() && p.getPageSize() != Integer.MAX_VALUE;
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		long count = findAllCount(cl, attr, value);
		if (count > 0) {
			CriteriaQuery<Object[]> query = (CriteriaQuery<Object[]>)createAllQuery(cl, result, attr, value, p != null ? p.getSort() : null, false);
	        TypedQuery<Object[]> q = em.createQuery(query);
	        if (paged) {
	        	int off = p.getPageNumber() * p.getPageSize();
	        	if (off > count) {
	        		off = 0;
	        		p = PageRequest.of(0, p.getPageSize(), p.getSort());
	        	}
	        	q = q.setFirstResult(off);
	        	q = q.setMaxResults(p.getPageSize());
	        }
	        List<Object[]> l = q.getResultList();
	        for (Object[] row : l) {
	        	Map<String, Object> mapRow = new LinkedHashMap<String, Object>();
	        	for (int i=0; i<result.length; i++) {
	        		String r  = result[i];
	        		mapRow.put(r, row[i]);
	    		}
	        	listResult.add(mapRow);
	        }
		}
        return new PageImpl<Map<String, Object>>(listResult, paged ? p : Pageable.unpaged(), count);
	}
	private Long findAllCount(Class<?> cl, String[] attr, Object[] value) {
		CriteriaQuery<Long> query = (CriteriaQuery<Long>)createAllQuery(cl, null, attr, value, null, true);
        return em.createQuery(query).getSingleResult();
	}
	private CriteriaQuery<?> createAllQuery(Class<?> cl, String[] result, String[] attr, Object[] value, Sort sort, boolean count) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<?> query = null;
		if (count) query = cb.createQuery(Long.class);
		else if (result != null) query = cb.createQuery(Object[].class);
		else query = cb.createQuery(cl);
		Root<?> f = query.from(cl);
		if (count) ((CriteriaQuery<Long>)query).select(cb.count(f)).distinct(true);
		else if (result != null) {
			List<Selection<?>> list = new ArrayList<Selection<?>>();
			for (String r : result) {
				String[] joinList = r.split("__");
				Path<?> p = f;
				for (String join : joinList) p = p.get(join);
				list.add(p);
			}
			query.multiselect(list).distinct(true);
		}
		else query.select((Path)f).distinct(true);
		
		Predicate prTotal = null;
		if (attr != null && attr.length > 0) {
			for (int i=0; i<attr.length; i++) {
				String a = attr[i];
				String[] joinList = a.split("__");
				Path p = f;
				for (String join : joinList) p = p.get(join); 
				/*
				Class<?> clType = cl;
				for (String join : joinList) {
					Class<?> clAttr = hs.getAttrType(clType, join);
					if (List.class.isAssignableFrom(clAttr)) {
						p = ((Root<?>)p).join(join);
						try {
							String getter = "get" + a.substring(0, 1).toUpperCase() + join.substring(1);
							Method mGet = clType.getMethod(getter);
							clType = mGet.getAnnotation(ManyToMany.class).targetEntity();
						}
						catch (Exception ex) {}
					}
					else {
						p = p.get(join);
						clType = clAttr;
					}
				}
				*/
				Class<?> clAttr = hs.getAttrType(cl, a);
	        	if (clAttr == null) continue;
	        	Predicate pr = null;
	        	boolean and = true;
	        	if (value[i] == null) pr = cb.isNull(p);
	        	else if (value[i] instanceof String && isExpression((String)value[i])) {
	        		String[] es = parseExpression(((String)value[i]).trim());
	        		if (es.length == 0) continue;
	        		int j = 0;
	        		for (; j<es.length; j+=3) {
	        			Predicate prT = null;
	        			String c = es[j], op = es[j + 1], va = es[j + 2];
	        			if (va.isEmpty() && "=".equals(op)) prT = cb.isNull(p);
	        			else if (va.isEmpty() && "<>".equals(op)) prT = cb.isNotNull(p);
	        			else {
		        			Object o = hs.getObjectByString(va, clAttr);
		        			if (o instanceof BigDecimal) o = ((BigDecimal)o).doubleValue();
		        			if (o instanceof Date) {
		        				Date d = (Date)o;
		        				if (">".equals(op)) prT = cb.greaterThan(p, d);
		        				else if (">=".equals(op)) prT = cb.greaterThanOrEqualTo(p, d);
			        			else if ("<".equals(op)) prT = cb.lessThan(p, d);
			        			else if ("<=".equals(op)) prT = cb.lessThanOrEqualTo(p, d);
			        			else if ("=".equals(op)) prT = cb.equal(p, d);
			        			else if ("<>".equals(op)) prT = cb.notEqual(p, d);
		        			}
		        			else if (o instanceof Number) {
			        			if (">".equals(op)) prT = cb.gt(p, (Number)o);
			        			else if (">=".equals(op)) prT = cb.ge(p, (Number)o);
			        			else if ("<".equals(op)) prT = cb.lt(p, (Number)o);
			        			else if ("<=".equals(op)) prT = cb.le(p, (Number)o);
			        			else if ("=".equals(op)) prT = cb.equal(p, (Number)o);
			        			else if ("<>".equals(op)) prT = cb.notEqual(p, (Number)o);
		        			}
		        			else if (o instanceof String) {
		        				String s = (String)o;
		        				if (">".equals(op)) prT = cb.greaterThan(p, s);
		        				else if (">=".equals(op)) prT = cb.greaterThanOrEqualTo(p, s);
			        			else if ("<".equals(op)) prT = cb.lessThan(p, s);
			        			else if ("<=".equals(op)) prT = cb.lessThanOrEqualTo(p, s);
			        			else if ("=".equals(op)) prT = s.indexOf("%") >= 0 ? cb.like(p, s) : cb.equal(p, s);
			        			else if ("<>".equals(op)) prT = s.indexOf("%") >= 0 ? cb.notLike(p, s) : cb.notEqual(p, s);
			        			else if ("like".equals(op)) {
			        				if (s.indexOf("%") < 0) s = "%" + s + "%";
			        				prT = cb.like(p, s);
			        			}
		        			}
	        			}
	        			if (prT != null) {
	        				pr = pr == null ? prT : ("or".equalsIgnoreCase(c) || "|".equals(c) || "||".equals(c))? cb.or(pr, prT) : cb.and(pr, prT);
	        			}
	        		}
	        		if ("or".equalsIgnoreCase(es[0])) and = false;
	        	}
	        	else
	        	{
	        		Object val = value[i];
	        		if (value[i] instanceof String)
	        		{
	        			String v = ((String)val).trim();
	        			if (v.startsWith("'")) v = v.substring(1);
	        			if (v.endsWith("'")) v = v.substring(0, v.length() - 1);
	        			val = hs.getObjectByString(v, clAttr);
	        		}
		        	pr = val instanceof String && ((String)val).indexOf('%') >= 0 
		        			? cb.like(p, (String)val) 
		        			: cb.equal(p, val);
	        	}
	        	if (pr != null) {
	        		if (prTotal == null && !and) prTotal = cb.and();
	        		prTotal = prTotal == null ? pr : (and ? cb.and(prTotal, pr) : cb.or(prTotal, pr));
	        	}
			}
		}
		if (prTotal != null) query.where(cb.and(prTotal));
		
		if (sort != null && !count) {
			List<Order> lo = new ArrayList<Order>();
    		Stream<Sort.Order> stream = StreamSupport.stream(sort.spliterator(), false);
    		Iterator<Sort.Order> it = stream.iterator();
			while (it.hasNext()) {
				Sort.Order order = it.next();
				String a = order.getProperty();
				String[] joinList = a.split("__");
				Path p = f;
				if (joinList.length > 1) for (int j=0; j<joinList.length-1; j++) f.join(joinList[j], JoinType.LEFT);
				for (String join : joinList) p = p.get(join);
				Order o = order.getDirection() == Direction.ASC ? cb.asc(p) : cb.desc(p);
				lo.add(o);
			}
			if (lo.size() > 0) query.orderBy(lo);
    	}
		return query;
	}
	private boolean isExpression(String v) {
		if (v == null || v.trim().isEmpty()) return false;
		v = v.trim().toLowerCase();
		return v.startsWith(">") || 
			   v.startsWith("<") ||
			   v.startsWith("=") ||
			   v.startsWith("like") ||
			   v.startsWith("or") ||
			   v.startsWith("and");
	}
	private boolean isPredicate(String op) {
		return ">".equals(op) || 
				">=".equals(op) ||
				"<".equals(op) ||
				"<=".equals(op) ||
				"=".equals(op) ||
				"<>".equals(op) ||
				"like".equalsIgnoreCase(op);
	}
	private String[] parseExpression(String v) {
		if (v == null || v.trim().isEmpty()) return new String[] {};
		v = v.trim();
		List<String> l = new ArrayList<String>();
		int j = 0;
		boolean q = false;
		String term = "";
		for (; j<v.length(); j++) {
			char a = v.charAt(j);
			if (a == '\'') { 
				if (!q) { 
					if (!term.isEmpty()) {
						l.add(term);
						term = "";
					}
					q = true; 
					continue; 
				}
				if ((j + 1) < v.length() && v.charAt(j + 1) == '\'') {
					term += a;
					j++;
					continue;
				}
				q = false;
				l.add(term);
				term = "";
			}
			else if (a == ' ') {
				if (q) term += a;
				else {
					if (!term.isEmpty()) {
						if (isPredicate(term) && l.size() % 3 != 1) l.add("AND");
						l.add(term);
						term = "";
					}
				}
			}
			else {
				term += a;
				if (isPredicate(term)) {
					for (int n=j+1; n<v.length(); n++) {
						a = v.charAt(n);
						if (!isPredicate(term + a)) break;
						term += a;
						j++;
					}
					if (l.size() % 3 != 1) l.add("AND");
					l.add(term);
					term = "";
				}
			}
		}
		if (!term.isEmpty()) {
			l.add(term);
			term = "";
		}
		return l.toArray(new String[l.size()]);
	}
	@Override
	public List<Object> findAll(Class<?> cl) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object> query = (CriteriaQuery<Object>)cb.createQuery(cl);
		Root<Object> f = (Root<Object>) query.from(cl);
		query.select(f);
		return em.createQuery(query).getResultList();
	}
	@Override
	public Object findByCode(Class<?> cl, String code) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Object> query = (CriteriaQuery<Object>)cb.createQuery(cl);
		Root<Object> f = (Root<Object>)query.from(cl);
		query.select(f).where(cb.equal(f.get("code"), code));
		try { return em.createQuery(query).getSingleResult(); } catch (Exception ex) { }
		return null;
	}
	@Override
	public String getClassByKey(Object pk) {
		if (!(pk instanceof Integer)) return null;
		Integer rn = (Integer)pk;
		Class<IBase> cl = IBase.class;
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<IBase> query = (CriteriaQuery<IBase>)cb.createQuery(cl);
		Root<IBase> f = (Root<IBase>)query.from(cl);
		query.select(f).where(cb.equal(f.get("rn"), rn));
		IBase obj = null;
		try { obj = em.createQuery(query).getSingleResult(); } catch (Exception ex) { }
		return obj != null ? obj.getClazz() : null;
	}
	@Override
	public Object executeItem(Object obj, String listAttr, String cmd, String clazzItem, Integer rnItem, boolean bNew) throws Exception {
		Object listObj = hs.copyProperty(obj, listAttr);
		if (listObj == null || !(listObj instanceof List)) throw new Exception("Не найден список '" + listAttr + "'");
		List list = (List<?>)listObj;
		Class<?> clItem = hs.getClassByName(clazzItem);
		if (clItem == null) throw new ClassNotFoundException("Не найден класс по имени '" + clazzItem + "'");
		if ("add".equals(cmd)) {
			if (!bNew &&  rnItem == null) throw new Exception("Не задан объект для добавления в список '" + listAttr + "'");
			Object item = rnItem != null ? find(clItem, rnItem) : clItem.newInstance();
			if (rnItem == null) {
				hs.invoke(item, "onNew");
				hs.setProperty(item, "parent", obj);
				item = createObj(item);
			}
			else if (item == null) throw new Exception("Не найден объект " + clazzItem + " по идентификатору " + rnItem + " для добавления в список '" + listAttr + "'");
			list.add(item);
			hs.setProperty(obj, listAttr, list);
			saveObj(obj);
			return item;
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
			if (bNew) {
				Object objItem = find(clItem, rnItem);
				if (objItem == null) throw new Exception("Не найден объект " + clazzItem + " по идентификатору " + rnItem);
				hs.invoke(objItem, "onRemove");
				removeObj(objItem);
			}
		}
		return null;
	}
	@Override
	public Object updateItem(Object obj, String listAttr, String clazzItem, Integer rnItemOld, Integer rnItem) throws Exception {
		Object listObj = hs.getProperty(obj, listAttr);
		if (listObj == null || !(listObj instanceof List)) throw new Exception("Не найден список '" + listAttr + "'");
		List list = (List<?>)listObj;
		Class<?> clItem = hs.getClassByName(clazzItem);
		if (clItem == null || rnItem == null) return null;
		Object item = find(clItem, rnItem);
		if (item == null) return null;
		if (rnItemOld != null) {
			for (int i=0; i<list.size(); i++) {
				Object o = list.get(i);
				if (!(o instanceof IBase) || rnItemOld.compareTo(((IBase)o).getRn()) != 0) continue;
				list.set(i, item);
				break;
			}
		}
		return item;
	}
	@Override
	public void writeLog(String user_login, Integer rn, String clazz, Map<String, Object[]> mapChanged, Operation op, String ip, String browser) {
		String opCode = "";
		if (op == Operation.create) opCode = "create";
		else if (op == Operation.update) opCode = "update";
		else if (op == Operation.delete) opCode = "remove";
		SpActionType action_type = (SpActionType)findByCode(SpActionType.class, opCode);
		Date dt = new Date();
		String attr = "";
		if (mapChanged != null && !mapChanged.isEmpty()) {
			Iterator<String> it = mapChanged.keySet().iterator();
			while (it.hasNext()) {
				String a = it.next();
				if (!attr.isEmpty()) attr += ',';
				attr += a;
				Object[] o = mapChanged.get(a);
				if (o != null && o.length >= 2) {
					o[0] = hs.getObjectString(o[0]);
					o[1] = hs.getObjectString(o[1]);
				}
			}
		}	
		ObjectMapper mapper = new ObjectMapper();
		String v = null;
		try { v = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapChanged); } catch (Exception ex) { }
		ActionLog al = new ActionLog();
		al.setUser_login(user_login);
		al.setAction_type(action_type);
		al.setAction_time(dt);
		al.setClient_ip(ip);
		al.setClient_browser(browser);
		al.setObj_name(clazz);
		al.setObj_rn(rn);
		al.setObj_attr(attr);
		al.setObj_value(v);
		saveObj(al);
	}
}
