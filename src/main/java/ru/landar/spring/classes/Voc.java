package ru.landar.spring.classes;

public class Voc {
	private String name;
	private String title;
	
	public Voc(String name, String title) { 
		setName(name); 
		setTitle(title);
	}
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
}
