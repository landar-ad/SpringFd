package ru.landar.spring.classes;

public class ButtonInfo {
	String name;
	String title;
	String icon;
	String color;
	
	public ButtonInfo(String name, String title) {
		this(name, title, null, "light");
	}
	
	public ButtonInfo(String name, String title, String icon) {
		this(name, title, icon, "light");
	}
	
	public ButtonInfo(String name, String title, String icon, String color) {
		setName(name);
		setTitle(title);
		setIcon(icon);
		setColor(color);
	}
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getIcon() { return icon; }
	public void setIcon(String icon) { this.icon = icon; }

	public String getColor() { return color; }
	public void setColor(String color) { this.color = color; }
}
