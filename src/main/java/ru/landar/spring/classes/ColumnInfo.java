package ru.landar.spring.classes;

public class ColumnInfo {
	String name;
	String title;
	boolean visible;
	boolean sortable;
	String filter;
	String filterType;
	String filterList;
	String filterAttr;
	
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
		setFilterList(filterList);
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
