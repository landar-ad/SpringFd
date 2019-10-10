package ru.landar.spring.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.Part;

import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;

public interface HelperService {
	Object ai(String clazz, String attr, String info);
	boolean isEmpty(String v);
	boolean isEmptyTrim(String v);
	Object getObjectByString(Class<?> cl, String attr, String v);
	Object getObjectByString(String v, Class<?> clType);
	Object getObjectByPart(Part part) throws Exception;
	Class<?> getAttrType(Class<?> cl, String attr);
	Class<?> getListAttrType(Class<?> cl, String attr);
	Class<?> getItemType(Class<?> cl, String attr);
	Field[] getFields(Class<?> cl, boolean persist);
	String getTitleByAttr(String attr);
	void setProperty(Object obj, String attr, Object value);
	Object getProperty(Object obj, String attr);
	Object copyProperty(Object obj, String attr);
	Object getPropertyJson(Object obj, String attr);
	void copyProperties(Object src, Object dest, boolean notNullSrc, boolean noNullDest);
	Map<String, Object> getMapProperties(Object obj, boolean persist);
	Map<String, Object> getMapProperties(Object obj);
	Map<String, Object[]> getMapChanged(Map<String, Object> mapOld, Map<String, Object> mapNew);
	boolean propertyExists(Object obj, String attr);
	String getPropertyString(Object obj, String attr);
	String getObjectString(Object obj);
	boolean equals(Object o1, Object o2);
	Object getVariable(Object obj, String attr);
	Object getVariableString(Object obj, String attr);
	long copyStream(InputStream is, OutputStream os, boolean closeIn, boolean closeOut) throws Exception;
	String replaceSpecial(String s);
	String loadTemplate(String template);
	String getDefaultObjectTemplate(String clazz);
	boolean isServerConnected(String url, int timeout);
	boolean checkRights(Object obj, Operation op);
	Object invoke(Object obj, String method, Object... args);
	Object invokePure(Object obj, String method, Object... args) throws Exception;
	Object invoke(Class<?> cl, String method, Object... args);
	boolean templateExists(String template);
	String getTemplateSource(String template);
	Class<?> getHandlerClass(String code); 
	ObjService getObjService();
	UserService getUserService();
	IDepartment getDepartment();
	Integer getDepartmentKey();
	boolean checkDepartment(IDepartment depart);
	boolean checkPerson(IBase base);
	Class<?> getClassByName(String clazz);
	Class<?>[] getAllClasses() throws Exception;
	String getFullDate(Date date);
	String getMonthDate(Date date);
	String getJsonString(Object obj) throws Exception;
}
