package ru.landar.spring.classes;

import java.util.Date;

import ru.landar.spring.model.IBase;
import ru.landar.spring.service.HelperServiceImpl;

public class AttributeInfo {
	
	String name;
	String title;
	String type;
	String editList;
	boolean required;
	boolean readOnly;
	int length;
	String editAttr;
	
	public AttributeInfo(String name, Class<?> cl) {
		setName(name);
		String title = (String)HelperServiceImpl.getAttrInfo(cl, name, "nameField");
		if ("*".equals(title)) title = (String)HelperServiceImpl.getAttrInfo(cl, name);
		setTitle(title);
		String type = (String)HelperServiceImpl.getAttrInfo(cl, name, "editType");
		if ("*".equals(type)) {
			type = "text";
			Class<?> clAttr = HelperServiceImpl.s_getAttrType(cl, name);
			if (clAttr != null) {
				if (IBase.class.isAssignableFrom(clAttr)) type = "select";
				else if (Date.class.isAssignableFrom(clAttr)) {
					type = "date";
				}
				else if (Boolean.class.isAssignableFrom(clAttr)) type = "checkbox";
			}
		}
		setType(type);
		String editList = (String)HelperServiceImpl.getAttrInfo(cl, name, "editList");
		if ("*".equals(editList) && "select".equals(type)) editList = (String)HelperServiceImpl.getAttrInfo(cl, name, "list");
		else editList = null;
		setEditList(editList);
		Boolean required = (Boolean)HelperServiceImpl.getAttrInfo(cl, name, "required");
		setRequired(required == null ? false : required);
		Boolean readOnly = (Boolean)HelperServiceImpl.getAttrInfo(cl, name, "readOnly");
		setReadOnly(readOnly == null ? false : required);
		Integer length = (Integer)HelperServiceImpl.getAttrInfo(cl, name, "editLength");
		setLength(length == null ? 0 : length);
		String editAttr = (String)HelperServiceImpl.getAttrInfo(cl, name, "editAttr");
		if (editAttr == null || "*".equals(editAttr)) editAttr = "rn";
		setEditAttr(editAttr);
	}
	
	public AttributeInfo(String name, Class<?> cl, String type, String editList, boolean required, boolean readOnly, int length, String editAttr) {
		setName(name);
		String title = (String)HelperServiceImpl.getAttrInfo(cl, name, "nameField");
		if ("*".equals(title)) title = (String)HelperServiceImpl.getAttrInfo(cl, name);
		setTitle(title);
		if ("*".equals(type)) {
			Class<?> clAttr = HelperServiceImpl.s_getAttrType(cl, name);
			if (clAttr != null && IBase.class.isAssignableFrom(clAttr)) type = "select";
			else type = "text";
		}
		setType(type);
		if ("*".equals(editList) && "select".equals(type)) editList = (String)HelperServiceImpl.getAttrInfo(cl, name, "list");
		setEditList(editList);
		setRequired(required);
		setReadOnly(readOnly);
		setLength(length);
		if (editAttr == null || editAttr.isEmpty()) editAttr = "rn";
		setEditAttr(editAttr);
	}
	public AttributeInfo(String name, String title, String type, String editList, boolean required, boolean readOnly, int length, String editAttr) {
		setName(name);
		setTitle(title);
		setType(type);
		setEditList(editList);
		setRequired(required);
		setReadOnly(readOnly);
		setLength(length);
		setEditAttr(editAttr);
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getEditList() { return editList; }
	public void setEditList(String editList) { this.editList = editList; }
	
	public String getEditAttr() { return editAttr; }
	public void setEditAttr(String editAttr) { this.editAttr = editAttr; }
	
	public boolean getRequired() { return required; }
	public void setRequired(boolean required) { this.required = required; }
	
	public boolean getReadOnly() { return readOnly; }
	public void setReadOnly(boolean readOnly) { this.readOnly = readOnly; }
	
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }
}
