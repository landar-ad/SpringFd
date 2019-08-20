package ru.landar.spring.repository;

import org.springframework.stereotype.Repository;

import ru.landar.spring.classes.Operation;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityTransaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ObjRepositoryCustom {
	EntityTransaction beginTransaction();
	Object createObj(Object obj);
	Object updateObj(Object obj);
	Object saveObj(Object obj);
	void removeObj(Object obj);
	Object getMaxAttr(Class<?> cl, String attr);
	Object getMaxAttr(Class<?> cl, String attr, String[] attrFilter, Object[] valueFilter);
	Object find(Class<?> cl, Object pk);
	Object find(Class<?> cl, String attr, Object value);
	Page<Object> findAll(Class<?> cl, Pageable p, String[] attr, Object[] value);
	Page<Map<String, Object>> findAllResult(Class<?> cl, String[] result, Pageable p, String[] attr, Object[] value);
	List<Object> findAll(Class<?> cl);
	Object findByCode(Class<?> cl, String code);
	String getClassByKey(Object pk);
	Object executeItem(Object obj, String listAttr, String cmd, String clazzItem, Integer rnItem, boolean bNew) throws Exception;
	Object updateItem(Object obj, String listAttr, String clazzItem, Integer rnItemOld, Integer rnItem) throws Exception;
	void writeLog(String user_login, Integer rn, String clazz, Map<String, Object[]> mapChanged, Operation op, String ip, String browser);
}
