package ru.landar.spring.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ObjRepositoryCustom {
	
	Object createObj(Object obj);
	Object updateObj(Object obj);
	Object saveObj(Object obj);
	void removeObj(Object obj);
	Object find(Class<?> cl, Object pk);
	Object find(Class<?> cl, String attr, Object value);
	Page<Object> findAll(Class<?> cl, Pageable p, String[] attr, Object[] value);
	Page<Map<String, Object>> findAllResult(Class<?> cl, String[] result, Pageable p, String[] attr, Object[] value);
	List<Object> findAll(Class<?> cl);
	Object findByCode(Class<?> cl, String code);
	String getClassByKey(Object pk);
}
