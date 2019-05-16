package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;

import ru.landar.spring.classes.AppClassLoader;
import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.config.AutowireHelper;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class ISettings extends IBase {
	
	private String type;
	private String filetype;
	private String value;
	private String username;
	private String roles;
	
	@Column(length=32)
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    @Column(length=32)
    public String getFiletype() { return filetype; }
    public void setFiletype(String filetype) { this.filetype = filetype; }
    
    @Lob
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    @Column(length=32)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    @Column(length=256)
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    
    @Autowired
    AppClassLoader appClassLoader;
  
    @Override
    public Object onUpdate(Map<String, Object> map, Map<String, Object[]> mapChanged) throws Exception {
    	
    	AutowireHelper.autowire(this);
    	Object ret = super.onUpdate(map, mapChanged);
    	if (ret != null) return ret;
    	
    	if ("java".equals(getType()) && !hs.isEmpty(getCode()) && getCode().endsWith("_listeners"))
    		appClassLoader.removeClass(getCode());

    	return true;
    }
    
	public static String singleTitle() { return "Параметр настройки"; }
	public static String multipleTitle() { return "Параметры настройки"; }
	public static List<ColumnInfo> listColumn()
	{
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("name", "Наименование"));
		ret.add(new ColumnInfo("type", "Тип"));
		ret.add(new ColumnInfo("filetype", "Тип файла"));
		ret.add(new ColumnInfo("value", "Значение"));
		ret.add(new ColumnInfo("username", "Пользователь"));
		ret.add(new ColumnInfo("roles", "Роли"));
		return ret;
	}
	public static List<AttributeInfo> listAttribute()
	{
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("code", "Код", "text", null, false, 4)); 
		ret.add(new AttributeInfo("name", "Наименование", "text", null, false));
		ret.add(new AttributeInfo("type", "Тип", "text", null, false, 4));
		ret.add(new AttributeInfo("filetype", "Тип файла", "text", null, false, 4));
		ret.add(new AttributeInfo("value", "Значение", "textarea", null, false));
		ret.add(new AttributeInfo("username", "Пользователь", "text", null, false, 4));
		ret.add(new AttributeInfo("roles", "Роли", "text", null, false));
		return ret;
	}
}
