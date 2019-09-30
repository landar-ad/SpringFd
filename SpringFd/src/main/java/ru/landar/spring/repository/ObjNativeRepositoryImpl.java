package ru.landar.spring.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

public class ObjNativeRepositoryImpl implements ObjNativeRepository {
	@PersistenceContext
	private EntityManager em;
	
	@Override
	public List<?> findObject(String clazz, String[] attr, Object[] param) {
		String v = "select object(c) from " + clazz + " c";
		for (int i=1; attr != null && i<=attr.length && param != null && i<=param.length; i++) {
			if (i == 1) v += " where ";	else if (i > 1) v += " and ";
			v += attr[i - 1] + "=?" + i;
		}
		Query q = em.createQuery(v);
		for (int i=1; attr != null && i<=attr.length && param != null && i<=param.length; i++) {
			q.setParameter(i, param[i - 1]);
		}
		return q.getResultList();
	}
	@Override
	public List<?> findObject(String clazz, String where, Object[] param) {
		String v = "select object(c) from " + clazz + " c";
		if (where != null && !where.isEmpty()) {
			if (!where.startsWith(" ")) v += " ";
			v += where;
		}
		Query q = em.createQuery(v);
		for (int i=1; param != null && i<=param.length; i++) q.setParameter(i, param[i - 1]);
		return q.getResultList();
	}
}
