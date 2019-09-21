package ru.landar.spring.classes;

public class MenuInfo {
	String title;
	String href;
	boolean active;
	
	public MenuInfo(String title, String href, boolean active) {
		setTitle(title);
		setHref(href);
		setActive(active);
	}
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getHref() { return href; }
	public void setHref(String href) { this.href = href; }

	public boolean getActive() { return active; }
	public void setActive(boolean active) { this.active = active; }
}

