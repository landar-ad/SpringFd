package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;

import ru.landar.spring.classes.AppClassLoader;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Параметр настройки", multi="Параметры настройки", menu="Настройки")
public class ISettings extends IBase {
	private String type;
	private String filetype;
	private String value;
	private IBase value_obj;
	private String username;
	private String roles;
	
	@FieldTitle(name="Тип")
	@Column(length=32)
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    @FieldTitle(name="Тип файла")
    @Column(length=32)
    public String getFiletype() { return filetype; }
    public void setFiletype(String filetype) { this.filetype = filetype; }
    
    @FieldTitle(name="Значение")
    @Lob
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    @FieldTitle(name="Значение (объект)")
    @ManyToOne(targetEntity=IBase.class, fetch=FetchType.LAZY)
    public IBase getValue_obj() { return value_obj; }
    public void setValue_obj(IBase value_obj) { this.value_obj = value_obj; }
    
    @FieldTitle(name="Пользователь")
    @Column(length=32)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    @FieldTitle(name="Роли")
    @Column(length=256)
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    
    @Autowired
    AppClassLoader appClassLoader;
  
    @Override
    public Object onUpdate() throws Exception {
    	Object ret = super.onUpdate();
    	if (ret != null) return ret;
    	
    	if ("java".equals(getType()) && !hs.isEmpty(getCode()) && getCode().endsWith("_listeners"))
    		appClassLoader.removeClass(getCode());

    	return true;
    }
    
	public static String singleTitle() { return "Параметр настройки"; }
	public static String multipleTitle() { return "Параметры настройки"; }
	public static String menuTitle() { return "Настройки"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = ISettings.class;
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("name", cl));
		ret.add(new ColumnInfo("type", cl));
		ret.add(new ColumnInfo("filetype", cl));
		ret.add(new ColumnInfo("value", cl));
		ret.add(new ColumnInfo("value_obj__name", cl));
		ret.add(new ColumnInfo("username", cl));
		ret.add(new ColumnInfo("roles", cl));
		return ret;
	}
}
