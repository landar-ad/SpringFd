package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
	
    private void updateName() {
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
		ret.add(new ColumnInfo("act_status__name", "Статус акта", true, true, "actstatus__rn", "select", "listActStatus"));
		ret.add(new ColumnInfo("time_status", "Дата изменения статуса"));
		ret.add(new ColumnInfo("create_agent__name", "Создан"));
		ret.add(new ColumnInfo("depart__name", "Структурное подразделение"));
		ret.add(new ColumnInfo("create_time", "Дата создания"));
		ret.add(new ColumnInfo("change_agent__name", "Изменен"));
		ret.add(new ColumnInfo("change_time", "Дата изменения"));
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
    	IAgent agent = user.getPerson();
    	if (agent == null) agent = user.getOrg(); 
    	setCreate_agent(agent);
    	setCreate_time(dt);
    	setChange_agent(agent);
    	setChange_time(dt);
    	if (user.getPerson() != null && user.getPerson().getDepart() != null) setDepart(user.getPerson().getDepart() );
    	setAct_status((SpActStatus)objService.getObjByCode(SpActStatus.class, "0"));
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
    	IAgent agent = user.getPerson();
    	if (agent == null) agent = user.getOrg(); 
    	setChange_agent(agent);
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
			IOrganization org = user.getOrg();
			IPerson person = user.getPerson();
			if (org == null && person == null) throw new SecurityException("У пользователя " + user.getLogin() + " не указан контрагент");
			Integer rnOrg = org != null ? org.getRn() : null;
			Integer rnPerson = person != null ? person.getRn() : null;
			listAttr.add("create_agent__rn");
			String v = "";
			if (rnOrg != null) v += "eq " + rnOrg;
			if (rnPerson != null) {
				if (!v.isEmpty()) v += " or ";
				v += "eq " + rnPerson;
			}
			listValue.add(v);
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
				model.addAttribute("listAgent", objService.findAll(IAgent.class));
				model.addAttribute("listDocument", objService.findAll(Document.class));
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
}
