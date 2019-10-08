package ru.landar.spring.classes;

import ru.landar.spring.service.HelperServiceImpl;

public class AttributeInfo {
	
	String name;
	String title;
	String type;
	String nameList;
	boolean required;
	int length;
	String attrList;
	
	public AttributeInfo(String name, Class<?> cl) {
		setName(name);
		setTitle((String)HelperServiceImpl.getAttrInfo(cl, name));
		setType("text");
		setNameList(null);
		setRequired(false);
		setLength(0);
		setAttrList("rn");
	}
	
	public AttributeInfo(String name, Class<?> cl, String type, String nameList, boolean required, int length, String attrList) {
		setName(name);
		setTitle((String)HelperServiceImpl.getAttrInfo(cl, name));
		setType(type);
		if ("*".equals(nameList) && "select".equals(type)) nameList = (String)HelperServiceImpl.getAttrInfo(cl, name, "list");
		setNameList(nameList);
		setRequired(required);
		setLength(length);
		if (attrList == null) attrList = "rn";
		setAttrList(attrList);
	}
	
	public AttributeInfo(String name, String title, String type, String nameList, boolean required) {
		setName(name);
		setTitle(title);
		setType(type);
		setNameList(nameList);
		setRequired(required);
		setLength(0);
		setAttrList("rn");
	}
	public AttributeInfo(String name, String title, String type, String nameList, boolean required, int length) {
		setName(name);
		setTitle(title);
		setType(type);
		setNameList(nameList);
		setRequired(required);
		setLength(length);
		setAttrList("rn");
	}
	
	public AttributeInfo(String name, String title, String type, String nameList, boolean required, int length, String attrList) {
		setName(name);
		setTitle(title);
		setType(type);
		setNameList(nameList);
		setRequired(required);
		setLength(length);
		setAttrList(attrList);
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getNameList() { return nameList; }
	public void setNameList(String nameList) { this.nameList = nameList; }
	
	public String getAttrList() { return attrList; }
	public void setAttrList(String attrList) { this.attrList = attrList; }
	
	public boolean getRequired() { return required; }
	public void setRequired(boolean required) { this.required = required; }
	
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }
}
