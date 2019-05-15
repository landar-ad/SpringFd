package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class ActionLog extends IBase {
	private Date action_time;
	private SpActionType action_type;
	private String user_login;
	private String obj_name;
	private Integer obj_rn;
	private String obj_attr;
	private String obj_value_before;
	private String obj_value_after;
	private String client_ip;
	private String client_browser;
	
    public Date getAction_time() { return action_time; }
    public void setAction_time(Date action_time) { this.action_time = action_time;}
    
	@ManyToOne(targetEntity=SpActionType.class, fetch=FetchType.LAZY)
    public SpActionType getAction_type() { return action_type; }
    public void setAction_type(SpActionType action_type) { this.action_type = action_type; }

	@Column(length=32)
    public String getUser_login() { return user_login; }
    public void setUser_login(String user_login) { this.user_login = user_login; }
	
	@Column(length=32)
    public String getObj_name() { return obj_name; }
    public void setObj_name(String obj_name) { this.obj_name = obj_name; }
	
	public Integer getObj_rn() { return obj_rn; }
    public void setObj_rn(Integer obj_rn) { this.obj_rn = obj_rn; }
	
	@Lob
    public String getObj_attr() { return obj_attr; }
    public void setObj_attr(String obj_attr) { this.obj_attr = obj_attr; }
	
	@Lob
    public String getObj_value_before() { return obj_value_before; }
    public void setObj_value_before(String obj_value_before) { this.obj_value_before = obj_value_before; }
	
	@Lob
    public String getObj_value_after() { return obj_value_after; }
    public void setObj_value_after(String obj_value_after) { this.obj_value_after = obj_value_after; }
	
	@Column(length=32)
    public String getClient_ip() { return client_ip; }
    public void setClient_ip(String client_ip) { this.client_ip = client_ip; }
	
	@Column(length=256)
    public String getClient_browser() { return client_browser; }
    public void setClient_browser(String client_browser) { this.client_browser = client_browser; }
    
    @Autowired
	ObjService objService;
    @Autowired
	UserService userService;
    @Autowired
	HelperService hs;
    
	public static String singleTitle() { return "Журнал регистрации действий"; }
	public static String multipleTitle() { return "Журналы регистрации действий"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("action_time", "Время действия"));
		ret.add(new ColumnInfo("action_type__name", "Тип действия", true, true, "action_type__rn", "select", "listActionType"));
		ret.add(new ColumnInfo("user_login", "Пользователь"));
		ret.add(new ColumnInfo("obj_name", "Объект"));
		ret.add(new ColumnInfo("obj_rn", "Идентификатор"));
		ret.add(new ColumnInfo("obj_attr", "Атрибут"));
		ret.add(new ColumnInfo("obj_value_before", "Значение до"));
		ret.add(new ColumnInfo("obj_value_after", "Значение после"));
		ret.add(new ColumnInfo("client_ip", "IP клиента"));
		ret.add(new ColumnInfo("client_browser", "Браузер клиента"));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	
	@Override
	public Object onListAddFilter(List<String> listAttr, List<Object> listValue) {
 		Object ret = super.onListAddFilter(listAttr, listValue);
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
			model.addAttribute("listActonType", objService.findAll(SpActionType.class));
		}
		catch (Exception ex) { }
		return true;
	}
    @Override
    public Object onCheckRights(Operation op) { 
    	Object ret = invoke("onCheckRights", op);
     	if (ret != null) return ret;
    	return false;
    }
}
