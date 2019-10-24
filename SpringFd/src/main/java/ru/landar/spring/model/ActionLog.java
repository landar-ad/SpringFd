package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Зарегистрированное действие", multi="Журнал регистрации действий", system=true)
public class ActionLog extends IBase {
	private Date action_time;
	private SpCommon action_type;
	private String user_login;
	private String obj_name;
	private Integer obj_rn;
	private String obj_attr;
	private String obj_value;
	private String client_ip;
	private String client_browser;
	
	@FieldTitle(name="Время действия", editLength=4)
    public Date getAction_time() { return action_time; }
    public void setAction_time(Date action_time) { this.action_time = action_time;}
    
    @FieldTitle(name="Тип действия", sp="sp_typd", editLength=2)
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getAction_type() { return action_type; }
    public void setAction_type(SpCommon action_type) { this.action_type = action_type; }
    
    @FieldTitle(name="Пользователь", editLength=2)
	@Column(length=32)
    public String getUser_login() { return user_login; }
    public void setUser_login(String user_login) { this.user_login = user_login; }
	
    @FieldTitle(name="Объект", editLength=2)
	@Column(length=32)
    public String getObj_name() { return obj_name; }
    public void setObj_name(String obj_name) { this.obj_name = obj_name; }
	
    @FieldTitle(name="Идентификатор объекта", editLength=2)
	public Integer getObj_rn() { return obj_rn; }
    public void setObj_rn(Integer obj_rn) { this.obj_rn = obj_rn; }
	
    @FieldTitle(name="Атрибуты")
	@Lob
    public String getObj_attr() { return obj_attr; }
    public void setObj_attr(String obj_attr) { this.obj_attr = obj_attr; }
	
    @FieldTitle(name="Данные", editType="textarea")
	@Lob
    public String getObj_value() { return obj_value; }
    public void setObj_value(String obj_value) { this.obj_value = obj_value; }
	
    @FieldTitle(name="IP клиента", editLength=4)
	@Column(length=32)
    public String getClient_ip() { return client_ip; }
    public void setClient_ip(String client_ip) { this.client_ip = client_ip; }
	
    @FieldTitle(name="Браузер клиента")
	@Column(length=256)
    public String getClient_browser() { return client_browser; }
    public void setClient_browser(String client_browser) { this.client_browser = client_browser; }
    
    @Autowired
	ObjService objService;
    @Autowired
	UserService userService;
    @Autowired
	HelperService hs;
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = ActionLog.class;
		ret.add(new ColumnInfo("action_time", cl));
		ret.add(new ColumnInfo("action_type__name", cl));
		ret.add(new ColumnInfo("user_login", cl));
		ret.add(new ColumnInfo("obj_name", cl));
		ret.add(new ColumnInfo("obj_rn", cl));
		ret.add(new ColumnInfo("obj_attr", cl));
		ret.add(new ColumnInfo("obj_value", cl));
		ret.add(new ColumnInfo("client_ip", cl));
		ret.add(new ColumnInfo("client_browser", cl));
		return ret;
	}
	public Object onListPaginated() { return true; }
	public List<AttributeInfo> onListAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		Class<?> cl = ActionLog.class;
		ret.add(new AttributeInfo("action_time", cl));
		ret.add(new AttributeInfo("action_type", cl));
		ret.add(new AttributeInfo("user_login", cl));
		ret.add(new AttributeInfo("obj_name", cl));
		ret.add(new AttributeInfo("obj_rn", cl));
		ret.add(new AttributeInfo("obj_attr", cl));
		ret.add(new AttributeInfo("obj_value", cl));
		ret.add(new AttributeInfo("client_ip", cl));
		ret.add(new AttributeInfo("client_browser", cl));
		return ret;
	}
	@Override
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("view", "Просмотреть", "readme"));
		return ret;
	}
	@Override
	public Object onListAddFilter(List<String> listAttr, List<Object> listValue, Map<String, String[]> mapParam) {
 		Object ret = super.onListAddFilter(listAttr, listValue, mapParam);
		if (ret != null) return ret;
		
		IUser user = userService.getUser((String)null);
		if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
		String roles = user.getRoles();
		if (roles.indexOf("ADMIN") < 0) {
			listAttr.add("user_login");
			listValue.add(user.getLogin());
		}
		return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listSp_typd", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_typd"}));
		}
		catch (Exception ex) { }
		return true;
	}
    @Override
    public Object onCheckRights(Operation op) { 
    	Object ret = invoke("onCheckRights", op);
     	if (ret != null) return ret;
     	if (op == Operation.load) return true;
    	return false;
    }
}
