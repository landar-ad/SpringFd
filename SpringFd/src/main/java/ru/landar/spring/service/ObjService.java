package ru.landar.spring.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.landar.spring.model.SearchContent;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.ISettings;

public interface ObjService {
	Object createObj(Object obj);
    Object saveObj(Object obj);
    Object updateObj(Object obj);
    void removeObj(Object obj);
    void removeObj(String clazz, Integer rn) throws Exception;
    Page<Object> findAll(Class<?> cl, Pageable p, String[] attr, Object[] value);
    Page<Object> findAll(Class<?> cl, Pageable p, String[] attr, Object[] value, Integer rn);
    Page<Map<String, Object>> findAllResult(Class<?> cl, String[] result, Pageable p, String[] attr, Object[] value);
    String findListResult (Class<?> cl, String result, String[] attr, Object[] value);
    List<Object> findAll(Class<?> cl) throws Exception;
    List<Object> findAll(Class<?> cl, boolean empty) throws Exception;
    Object find(Class<?> cl, Object pk);
    Object find(String clazz, Object pk) throws Exception;
    Object find(Class<?> cl, String[] attr, Object[] value);
    Object find(Class<?> cl, String attr, Object value);
    Object getObjByCode(Class<?> cl, String code);
    String getClassByKey(Integer rn);
    Object getSettings(String code, String type);
    ISettings loadSettings(String code, String type);
    Page<SearchContent> search(String text, int off, int page);
    void writeLog(String user_login, Integer rn, String clazz, Map<String, Object[]> mapChanged, Operation op, String ip, String browser);
    Object executeItem(Object obj, String listAttr, String cmd, String clazzItem, Integer rnItem, boolean bNew) throws Exception;
    String getServiceContext();
}
