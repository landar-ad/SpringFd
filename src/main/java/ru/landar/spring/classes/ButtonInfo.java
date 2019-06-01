package ru.landar.spring.classes;

public class ButtonInfo {
	String name;
	String title;
	String icon;
	String url;
	boolean visible;
	boolean active;
	
	public ButtonInfo(String name, String title, String url, String icon) {
		this(name, title, icon, url, true, true);
	}
	
	public ButtonInfo(String name, String title, String url) {
		this(name, title, null, url, true, true);
	}
	
	public ButtonInfo(String name, String title, String url, String icon, boolean visible, boolean active) {
		setName(name);
		setTitle(title);
		setUrl(url);
		setIcon(icon);
		setVisible(visible);
		setActive(active);
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getIcon() { return icon; }
	public void setIcon(String icon) { this.icon = icon; }
	
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
	
	public void setVisible(boolean visible) { this.visible = visible; }
	public boolean getVisible() { return visible; }
	
	public void setActive(boolean active) { this.active = active; }
	public boolean getActive() { return active; }
}
