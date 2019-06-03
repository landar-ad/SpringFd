package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Act extends IBase {
	private String act_number;
	private Date act_date;
	private SpActStatus act_status;
	private Date time_status;
	private IAgent create_agent;
	private IDepartment depart;
	private Date create_time;
	private IAgent change_agent;
	private Date change_time;
	private List<Act_document> list_doc;
	
    @Column(length=40)
    public String getAct_number() { return act_number; }
    public void setAct_number(String act_number) { this.act_number = act_number; updateName(); }
    
    @Temporal(TemporalType.DATE)
    public Date getAct_date() { return act_date; }
    public void setAct_date(Date act_date) { this.act_date = act_date; updateName(); }
    
	@ManyToOne(targetEntity=SpActStatus.class, fetch=FetchType.LAZY)
    public SpActStatus getAct_status() { return act_status; }
    public void setAct_status(SpActStatus act_status) { this.act_status = act_status; }
	
	public Date getTime_status() { return time_status; }
    public void setTime_status(Date time_status) { this.time_status = time_status; }
	
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getCreate_agent() { return create_agent; }
    public void setCreate_agent(IAgent create_agent) { this.create_agent = create_agent; }
	
	@ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getDepart() { return depart; }
    public void setDepart(IDepartment depart) { this.depart = depart; }
	
    public Date getCreate_time() { return create_time; }
    public void setCreate_time(Date create_time) { this.create_time = create_time; }
	
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getChange_agent() { return change_agent; }
    public void setChange_agent(IAgent change_agent) { this.change_agent = change_agent; }
	
    public Date getChange_time() { return change_time; }
    public void setChange_time(Date change_time) { this.change_time = change_time; }
    
    @ManyToMany(targetEntity=Act_document.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Act_document> getList_doc() { return list_doc != null ? list_doc : new ArrayList<Act_document>(); }
    public void setList_doc(List<Act_document> list_doc) { this.list_doc = list_doc; }
	
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "";
    	if (!hs.isEmpty(getAct_number())) name = "№ " + getAct_number();
    	if (getAct_date() != null) name += (!name.isEmpty() ? " от " : "От ") + new SimpleDateFormat("dd.MM.yyyy").format(getAct_date());
    	setName(name);
    }
    
    @Autowired
	ObjService objService;
    @Autowired
	UserService userService;
    @Autowired
	HelperService hs;
    
	public static String singleTitle() { return "Акт приема-передачи"; }
	public static String multipleTitle() { return "Акты приема-передачи"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("act_number", "Номер акта"));
		ret.add(new ColumnInfo("act_date", "Дата акта"));
		ret.add(new ColumnInfo("act_status__name", "Статус акта", true, true, "act_status__rn", "select", "listActStatus"));
		ret.add(new ColumnInfo("time_status", "Дата изменения статуса"));
		ret.add(new ColumnInfo("create_agent__name", "Создан"));
		ret.add(new ColumnInfo("depart__name", "Структурное подразделение"));
		ret.add(new ColumnInfo("create_time", "Дата создания"));
		ret.add(new ColumnInfo("change_agent__name", "Изменен"));
		ret.add(new ColumnInfo("change_time", "Дата изменения"));
		return ret;
	}
	public static List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("newAct", "Сформировать новый акт"));
		ret.add(new ButtonInfo("sendAct", "Отправить"));
		ret.add(new ButtonInfo("acceptAct", "Принять"));
		ret.add(new ButtonInfo("confirmAct", "Утвердить"));
		ret.add(new ButtonInfo("refuseAct", "Отказать"));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
     	Date dt = new Date();
    	IUser user = userService.getUser((String)null);
    	if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
     	setCreate_agent(user.getPerson());
    	setCreate_time(dt);
    	setChange_agent(user.getPerson());
    	setChange_time(dt);
    	setAct_date(dt);
    	setDepart(hs.getDepartment());
    	setAct_status((SpActStatus)objService.getObjByCode(SpActStatus.class, "1"));
    	setTime_status(dt);
     	return true;
    }
	@Override
    public Object onUpdate(Map<String, Object> map, Map<String, Object[]> mapChanged) throws Exception { 
    	Object ret = super.onUpdate(map, mapChanged);
    	if (ret != null) return ret;
     	Date dt = new Date();
    	IUser user = userService.getUser((String)null);
    	if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
    	setChange_agent(user.getPerson());
    	setChange_time(dt);
    	return true;
	}
	@Override
	public Object onListAddFilter(List<String> listAttr, List<Object> listValue) {
 		Object ret = super.onListAddFilter(listAttr, listValue);
		if (ret != null) return ret;
		IUser user = userService.getUser((String)null);
		if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
		String roles = user.getRoles();
		if (roles.indexOf("ADMIN") < 0) {
			Integer rnDep = hs.getDepartmentKey();
			if (rnDep != null) {
				listAttr.add("depart__rn");
				listValue.add("eq " + rnDep);
			}
		}
		return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listActStatus", objService.findAll(SpActStatus.class));
			if (!list) {
				model.addAttribute("listDepartment", objService.findAll(IDepartment.class));
				model.addAttribute("listAgent", objService.findAll(IOrganization.class));
			}	
		}
		catch (Exception ex) { }
		return true;
	}
    @Override
    public Object onCheckRights(Operation op) { 
    	Object ret = invoke("onCheckRights", op);
     	if (ret != null) return ret;
    	
    	Integer rn = getRn();
    	if (rn == null) return true;
    	if (op == Operation.update || op == Operation.delete)
    	{
	     	IUser user = userService.getUser((String)null);
			if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
			String roles = user.getRoles();
			if (roles.indexOf("ADMIN") >= 0) return true;
			IOrganization org = user.getOrg();
			IPerson person = user.getPerson();
			if (org == null && person == null) throw new SecurityException("У пользователя " + user.getLogin() + " не указан контрагент");
			IAgent cragent = getCreate_agent();
			if (cragent != null && ((org != null && org.getRn().compareTo(cragent.getRn()) == 0) || (person != null && person.getRn().compareTo(cragent.getRn()) == 0))) return true;
			return false;
    	}
    	return true;
    }
    @Override
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
    	if ("newAct".equals(param)) return true; 
    	if (getRn() == null) return false;
    	if ("edit".equals(param)) return onCheckRights(Operation.update);
		else if ("remove".equals(param)) return onCheckRights(Operation.delete);
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		else if ("sendAct".equals(param)) {
			return true;
		}
		else if ("acceptAct".equals(param)) {
			return true;
		}
		else if ("confirmAct".equals(param)) {
			return true;
		}
		else if ("refuseAct".equals(param)) {
			return true;
		}
		return false;
    }
    public static void newAct(HttpServletRequest request) throws Exception {
    	
    }
    public void sendAct(HttpServletRequest request) throws Exception {
    	
    }
    public void acceptAct(HttpServletRequest request) throws Exception {
    	
    }
    public void confirmAct(HttpServletRequest request) throws Exception {
    	
    }
    public void refuseAct(HttpServletRequest request) throws Exception {
    	
    }
}
