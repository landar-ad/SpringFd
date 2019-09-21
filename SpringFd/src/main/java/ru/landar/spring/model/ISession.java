package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.HelperService;

@Entity
public class ISession {
	
	private Integer rn;
	private String id;
	private String login;
	private String ip;
	private Date start_time;
	private Date end_time;
	
	public ISession() {
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getRn() { return rn; }
    public void setRn(Integer rn) { this.rn = rn; }	
	
    @Column(length=128, nullable=false)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    @Column(length=50)
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    @Column(length=50)
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public Date getStart_time() { return start_time; }
    public void setStart_time(Date start_time) { this.start_time = start_time; }
    
    public Date getEnd_time() { return end_time; }
    public void setEnd_time(Date end_time) { this.end_time = end_time; }
    
    public static String singleTitle() { return "Сессия"; }
	public static String multipleTitle() { return "Сессии"; }
	public static String menuTitle() { return multipleTitle(); }
	public List<ColumnInfo> onListColumn() {
		
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("login", "Пользователь")); 
		ret.add(new ColumnInfo("ip", "Адрес"));
		ret.add(new ColumnInfo("start_time", "Начало"));
		ret.add(new ColumnInfo("end_time", "Окончание"));
		return ret;
	}
	public boolean onListPaginated() { return true; }
	public List<AttributeInfo> onListAttribute() {
		
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("login", "Пользователь", "text", null, false, 4)); 
		ret.add(new AttributeInfo("ip", "Адрес", "text", null, false, 4));
		ret.add(new AttributeInfo("start_time", "Начало", "time", null, false));
		ret.add(new AttributeInfo("end_time", "Окончание", "time", null, false));
		return ret;
	}
	public List<ButtonInfo> onListButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("view", "Просмотреть", "readme'"));
		return ret;
	}
	@Transient public String getName() { return id; }
	@Transient public String getClazz() { return "ISession"; }
	
	@Autowired
	HelperService hs;
	
    public Object onCheckRights(Operation op) { 
     	AutowireHelper.autowire(this);
    	Object ret = null;
    	Class<?> cl = hs.getHandlerClass(getClazz() + "_listeners");
    	if (cl != null) try { ret = hs.invoke(cl.newInstance(), "onCheckRights", this, hs, op); } catch (Exception ex) { }
    	if (ret != null) return ret;
    	
    	return op != Operation.update && op != Operation.delete;
    }
    public Object onCheckExecute(String param) { 
    	AutowireHelper.autowire(this);
     	Object ret = null;
     	Class<?> cl = hs.getHandlerClass(getClazz() + "_listeners");
    	if (cl != null) try { ret = hs.invoke(cl.newInstance(), "onCheckExecute", this, hs, param); } catch (Exception ex) { }
     	if (ret != null) return ret;
     	if (getRn() == null) return false;
		if ("view".equals(param)) return true;
		return false;
    }
}
