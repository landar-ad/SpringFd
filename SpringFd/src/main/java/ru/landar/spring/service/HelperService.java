package ru.landar.spring.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import javax.servlet.http.Part;

import ru.landar.spring.classes.Operation;

public interface HelperService {
	boolean isEmpty(String v);
	boolean isEmptyTrim(String v);
	Object getObjectByString(Class<?> cl, String attr, String v);
	Object getObjectByString(String v, Class<?> clType);
	Object getObjectByPart(Part part) throws Exception;
	Class<?> getAttrType(Class<?> cl, String attr);
	Field[] getFields(Class<?> cl, boolean persist);
	String getTitleByAttr(String attr);
	void setProperty(Object obj, String attr, Object value);
	Object getProperty(Object obj, String attr);
	void copyProperties(Object src, Object dest, boolean notNull);
	boolean propertyExists(Object obj, String attr);
	Object getPropertyString(Object obj, String attr);
	boolean equals(Object o1, Object o2);
	Object getVariable(Object obj, String attr);
	Object getVariableString(Object obj, String attr);
	long copyStream(InputStream is, OutputStream os, boolean closeIn, boolean closeOut) throws Exception;
	String replaceSpecial(String s);
	String loadTemplate(String template);
	boolean isServerConnected(String url, int timeout);
	boolean checkRights(Object obj, Operation op);
	Object invoke(Object obj, String method, Object... args);
	Object invoke(Class<?> cl, String method, Object... args);
	boolean templateExists(String template);
	String getTemplateSource(String template);
	Class<?> getHandlerClass(String code); 
	ObjService getObjService();
	UserService getUserService();
}
