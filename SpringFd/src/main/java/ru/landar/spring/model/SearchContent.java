package ru.landar.spring.model;

import org.springframework.data.annotation.Id;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(collection = "content")
public class SearchContent {
	
	@Id
	@Field
	String id;
	@Field
	String clazz;
	@Field
	String name;
	@Field
	String context;
	@Field
	String content;

	public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getClazz() { return clazz; }
    public void setClazz(String clazz) { this.clazz = clazz; }
	
	public String getName() { return name; }
    public void setName(String name) { this.name = name; }
	
    public String getContext() { return content; }
    public void setContext(String context) { this.context = context; }
    
	public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    @Override
    public String toString() {
    	
    	return "id=" + id + ", name=" + name + ", content=" + content;
	}
}
