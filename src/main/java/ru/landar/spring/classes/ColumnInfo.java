package ru.landar.spring.classes;

import java.util.Date;

import ru.landar.spring.model.IBase;
import ru.landar.spring.service.HelperServiceImpl;

public class ColumnInfo {
	String name;
	String title;
	boolean visible;
	boolean sortable;
	String filter;
	String filterType;
	String filterList;
	String filterAttr;
	
	public ColumnInfo(String name, Class<?> cl) {
		setName(name);
		String attr = name;
		int k = attr.lastIndexOf("__");
		if (k > 0) attr = attr.substring(0, k);
		String tn = name.endsWith("__name") ? attr : name;
		String title = (String)HelperServiceImpl.getAttrInfo(cl, tn, "nameColumn");
		if (title == null || "*".equals(title)) title = (String)HelperServiceImpl.getAttrInfo(cl, tn);
		setTitle(title);
		Boolean visible = (Boolean)HelperServiceImpl.getAttrInfo(cl, attr, "visible");
		setVisible(visible == null ? true : visible);
		Boolean sortable = (Boolean)HelperServiceImpl.getAttrInfo(cl, attr, "sortable");
		setSortable(sortable == null ? true : sortable);
		String filter = (String)HelperServiceImpl.getAttrInfo(cl, attr, "filter"); 
		setFilter(filter);
		String filterType = (String)HelperServiceImpl.getAttrInfo(cl, attr, "filterType");
		if ("*".equals(filterType)) {
			filterType = "text";
			Class<?> clAttr = HelperServiceImpl.s_getAttrType(cl, attr);
			if (clAttr != null) {
				if (IBase.class.isAssignableFrom(clAttr)) filterType = "select";
			}
		}
		setFilterType(filterType);
		String filterList = (String)HelperServiceImpl.getAttrInfo(cl, attr, "filterList"), filterAttr = (String)HelperServiceImpl.getAttrInfo(cl, attr, "filterAttr");
		if ((filterList == null || "*".equals(filterList)) && "select".equals((filterType))) {
			filterList = (String)HelperServiceImpl.getAttrInfo(cl, attr, "list");
			if (filterAttr == null || "*".equals(filterAttr)) filterAttr = k < 0 ? "code" : "rn";
			if ("*".equals(filter) && k >= 0) setFilter(attr + "__rn");
		}
		setFilterList(filterList);
		setFilterAttr(filterAttr);
	}
	public ColumnInfo(String name, String title, boolean visible, boolean sortable, String filter, String filterType, String filterList, String filterAttr) {
		setName(name);
		setTitle(title);
		setVisible(visible);
		setSortable(sortable);
		setFilter(filter);
		setFilterType(filterType);
		setFilterAttr(filterAttr);
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public boolean getSortable() { return sortable; }
	public void setSortable(boolean sortable) { this.sortable = sortable; }
	
	public String getFilter() { return filter; }
	public void setFilter(String filter) { this.filter = "*".equals(filter) ? name : filter; }
	
	public String getFilterType() { return filterType; }
	public void setFilterType(String filterType) { this.filterType = filterType; }
	
	public String getFilterList() { return filterList; }
	public void setFilterList(String filterList) { this.filterList = filterList; }
	
	public String getFilterAttr() { return filterAttr; }
	public void setFilterAttr(String filterAttr) { this.filterAttr = filterAttr; }
	
	public void setVisible(boolean visible) { this.visible = visible; }
	public boolean getVisible() { return visible; }
}
