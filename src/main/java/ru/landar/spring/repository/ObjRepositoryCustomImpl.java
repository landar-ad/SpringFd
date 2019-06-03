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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ru.landar.spring.model.IBase;
import ru.landar.spring.model.ISession;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.UserService;

@Repository
public class ObjRepositoryCustomImpl implements ObjRepositoryCustom {
	@Autowired
	UserService userService;
	@Autowired
	HelperService hs;
	@PersistenceContext
	private EntityManager em;
	@Override
	public Object createObj(Object obj) {
		if (obj instanceof IBase) {
			String principal = userService.getPrincipal();
			((IBase)obj).setCreator(principal);
			((IBase)obj).setModifier(principal);
		}
		em.persist(obj);
		em.flush();
		return em.find(obj.getClass(), hs.getProperty(obj, "rn"));
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
		return ret;
	}
	@Override
	public Object saveObj(Object obj) {
		if (obj instanceof IBase) return ((IBase)obj).getRn() != null ? updateObj(obj) : createObj(obj);
		if (obj instanceof ISession) return ((ISession)obj).getId() != null ? updateObj(obj) : createObj(obj);
		return updateObj(obj);
	}
	@Override
	public void removeObj(Object obj) { em.remove(obj); }
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
		if (count) ((CriteriaQuery<Long>)query).select(cb.count(f));
		else if (result != null) {
			List<Selection<?>> list = new ArrayList<Selection<?>>();
			for (String r : result) {
				String[] joinList = r.split("__");
				Path<?> p = f;
				for (String join : joinList) p = p.get(join);
				list.add(p);
			}
			query.multiselect(list);
		}
		else query.select((Path)f);
		
		Predicate prTotal = null;
		if (attr != null && attr.length > 0) {
			for (int i=0; i<attr.length; i++) {
				String a = attr[i];
				String[] joinList = a.split("__");
				Path p = f;
				for (String join : joinList) p = p.get(join);
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
}
