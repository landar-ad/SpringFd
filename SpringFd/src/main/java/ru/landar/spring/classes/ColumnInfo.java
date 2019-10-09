package ru.landar.spring.classes;

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
		setTitle((String)HelperServiceImpl.getAttrInfo(cl, name.indexOf("__") > 0 ? name.substring(0, name.indexOf("__")) : name));
		setVisible(true);
		setSortable(true);
		setFilter("*");
		setFilterType("text");
		setFilterList(null);
		setFilterAttr(null);
	}
	public ColumnInfo(String name, Class<?> cl, boolean visible) {
		setName(name);
		setTitle((String)HelperServiceImpl.getAttrInfo(cl, name.indexOf("__") > 0 ? name.substring(0, name.indexOf("__")) : name));
		setVisible(visible);
		setSortable(true);
		setFilter("*");
		setFilterType("text");
		setFilterList(null);
		setFilterAttr(null);
	}
	public ColumnInfo(String name, Class<?> cl, boolean visible, boolean sortable, String filter, String filterType) {
		setName(name);
		String attr = name;
		int k = attr.indexOf("__");
		if (k > 0) attr = attr.substring(0, k); 
		setTitle((String)HelperServiceImpl.getAttrInfo(cl, attr));
		setVisible(visible);
		setSortable(sortable);
		setFilter(filter);
		setFilterType(filterType);
		String filterList = null, filterAttr = null;
		if ("select".equals(filterType)) {
			filterList = (String)HelperServiceImpl.getAttrInfo(cl, attr, "list");
			filterAttr = k < 0 ? "code" : "rn";
			if ("*".equals(filter)) setFilter(attr + "__rn");
		}
		setFilterList(filterList);
		setFilterAttr(filterAttr);
	}
	public ColumnInfo(String name, String title) {
		this(name, title, true, true, "*", "text", null, null);
	}
	public ColumnInfo(String name, String title, boolean visible) {
		this(name, title, visible, true, "*", "text", null, null);
	}
	public ColumnInfo(String name, String title, boolean visible, boolean sortable, String filter, String filterType, String filterList) {
		this(name, title, visible, true, filter, filterType, filterList, "rn");
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
