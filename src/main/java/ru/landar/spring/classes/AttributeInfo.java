package ru.landar.spring.classes;

public class AttributeInfo {
	
	String name;
	String title;
	String type;
	String nameList;
	boolean required;
	int length;
	
	public AttributeInfo(String name, String title, String type, String nameList, boolean required) {
		setName(name);
		setTitle(title);
		setType(type);
		setNameList(nameList);
		setLength(0);
	}
	public AttributeInfo(String name, String title, String type, String nameList, boolean required, int length) {
		
		setName(name);
		setTitle(title);
		setType(type);
		setNameList(nameList);
		setLength(length);
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	public String getNameList() { return nameList; }
	public void setNameList(String nameList) { this.nameList = nameList; }
	
	public boolean getRequired() { return required; }
	public void setRequired(boolean required) { this.required = required; }
	
	public int getLength() { return length; }
	public void setLength(int length) { this.length = length; }
}
