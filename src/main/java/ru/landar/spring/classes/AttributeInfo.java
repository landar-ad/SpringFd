package ru.landar.spring.classes;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	List<AttributeInfo> listAttr;
	boolean listAddExists;
	
	public AttributeInfo(String name, Class<?> cl) {
		setName(name);
		String title = (String)HelperServiceImpl.getAttrInfo(cl, name, "nameField");
		if ("*".equals(title)) title = (String)HelperServiceImpl.getAttrInfo(cl, name);
		setTitle(title);
		Class<?> clAttr = HelperServiceImpl.s_getAttrType(cl, name);
		String type = (String)HelperServiceImpl.getAttrInfo(cl, name, "editType");
		if ("*".equals(type)) {
			type = "text";
			if (clAttr != null) {
				if (IBase.class.isAssignableFrom(clAttr)) {
					boolean voc = (Boolean)HelperServiceImpl.getAttrInfo(clAttr, null, "voc");
					type = voc ? "select" : "choose";
				}
				else if (List.class.isAssignableFrom(clAttr)) {
					type = "list";
					Class<?> clItem = HelperServiceImpl.s_getItemType(cl, name);
					listAttr = HelperServiceImpl.getListAttribute(clItem, false);
					CascadeType[] c = null;
					String getter = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
					try { c = cl.getMethod(getter).getAnnotation(OneToMany.class).cascade(); } catch (Exception ex) { }
					if (c == null) try { c = cl.getMethod(getter).getAnnotation(ManyToMany.class).cascade(); } catch (Exception ex) { }
					listAddExists = true;
					if (c != null) for (CascadeType ci : c) {
						if (ci != CascadeType.ALL && ci != CascadeType.REMOVE) continue;
						listAddExists = false;
						break;
					}
					editList = clItem.getSimpleName();
				}
				else if (Date.class.isAssignableFrom(clAttr)) {
					type = "date";
					String getter = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
					Temporal t = null;
					try { t = cl.getMethod(getter).getAnnotation(Temporal.class); } catch (Exception ex) { }
					if (t == null || t.value() != TemporalType.DATE) type = "time";
				}
				else if (Boolean.class.isAssignableFrom(clAttr)) type = "checkbox";
			}
		}
		setType(type);
		if (getEditList() == null) {
			String editList = (String)HelperServiceImpl.getAttrInfo(cl, name, "editList");
			if ("*".equals(editList) && ("select".equals(type) || "choose".equals(type) || "list".equals(type))) {
				if ("select".equals(type)) editList = (String)HelperServiceImpl.getAttrInfo(cl, name, "list");
				else if ("choose".equals(type)) editList = clAttr.getSimpleName();
			}
			else editList = null;
			setEditList(editList);
		}
		Boolean required = (Boolean)HelperServiceImpl.getAttrInfo(cl, name, "required");
		setRequired(required == null ? false : required);
		Boolean readOnly = (Boolean)HelperServiceImpl.getAttrInfo(cl, name, "readOnly");
		setReadOnly(readOnly == null ? false : readOnly);
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
	
	public List<AttributeInfo> getListAttr() { return listAttr; }
	public void setListAttr(List<AttributeInfo> listAttr) { this.listAttr = listAttr; }
	
	public boolean getListAddExists() { return listAddExists; }
	public void setListAddExists(boolean listAddExists) { this.listAddExists = listAddExists; }
}
