package ru.landar.spring.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface ObjNativeRepository {
	List<?> findObject(String clazz, String[] attr, Object[] param);
	List<?> findObject(String clazz, String where, Object[] param);
}
