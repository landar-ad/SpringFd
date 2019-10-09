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
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.HelperService;

@Entity
@ObjectTitle(single="Сессия", multi="Сессии")
public class ISession {
	private Integer rn;
	private String id;
	private String login;
	private String ip;
	private Date start_time;
	private Date end_time;
	
	public ISession() {
	}
	
	@FieldTitle(name="Идентификатор")
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getRn() { return rn; }
    public void setRn(Integer rn) { this.rn = rn; }	
	
    @FieldTitle(name="Идентификатор сессии")
    @Column(length=128, nullable=false)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    @FieldTitle(name="Пользователь", editLength=4)
    @Column(length=50)
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }
    
    @FieldTitle(name="Адрес", editLength=4)
    @Column(length=50)
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    @FieldTitle(name="Начало", editType="time")
    public Date getStart_time() { return start_time; }
    public void setStart_time(Date start_time) { this.start_time = start_time; }
    
    @FieldTitle(name="Окончание", editType="time")
    public Date getEnd_time() { return end_time; }
    public void setEnd_time(Date end_time) { this.end_time = end_time; }
    
	public List<ColumnInfo> onListColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = ISession.class;
		ret.add(new ColumnInfo("login", cl)); 
		ret.add(new ColumnInfo("ip", cl));
		ret.add(new ColumnInfo("start_time", cl));
		ret.add(new ColumnInfo("end_time", cl));
		return ret;
	}
	public boolean onListPaginated() { return true; }
	public List<AttributeInfo> onListAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		Class<?> cl = ISession.class;
		ret.add(new AttributeInfo("login", cl)); 
		ret.add(new AttributeInfo("ip", cl));
		ret.add(new AttributeInfo("start_time", cl));
		ret.add(new AttributeInfo("end_time", cl));
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
