package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
public class Reestr extends IBase {
	private String reestr_number;
	private Date reestr_date;
	private SpReestrStatus reestr_status;
	private Date time_status;
	private IAgent mol;
	private IAgent agent_from;
	private IAgent agent_to;
	private Integer doc_count;
	private Integer sheet_count;
	private IAgent create_agent;
	private IDepartment depart;
	private Date create_time;
	private IAgent change_agent;
	private Date change_time;
	
    @Column(length=40)
    public String getReestr_number() { return reestr_number; }
    public void setReestr_number(String reestr_number) { this.reestr_number = reestr_number; updateName(); }
    
    @Temporal(TemporalType.DATE)
    public Date getReestr_date() { return reestr_date; }
    public void setReestr_date(Date reestr_date) { this.reestr_date = reestr_date; updateName(); }
    
	@ManyToOne(targetEntity=SpReestrStatus.class, fetch=FetchType.LAZY)
    public SpReestrStatus getReestr_status() { return reestr_status; }
    public void setReestr_status(SpReestrStatus reestr_status) { this.reestr_status = reestr_status; }
	
	public Date getTime_status() { return time_status; }
    public void setTime_status(Date time_status) { this.time_status = time_status; }
	
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getMol() { return mol; }
    public void setMol(IAgent mol) { this.mol = mol; }
	
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getAgent_from() { return agent_from; }
    public void setAgent_from(IAgent agent_from) { this.agent_from = agent_from; }
	
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getAgent_to() { return agent_to; }
    public void setAgent_to(IAgent agent_to) { this.agent_to = agent_to; }
    
    public Integer getDoc_count() { return doc_count; }
    public void setDoc_count(Integer doc_count) { this.doc_count = doc_count; }
    
    public Integer getSheet_count() { return sheet_count; }
    public void setSheet_count(Integer sheet_count) { this.sheet_count = sheet_count; }
	
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
    	if (!hs.isEmpty(getReestr_number())) name = "№ "+ getReestr_number();
    	if (getReestr_date() != null)
		{
    		if (!name.isEmpty()) name += " от ";
    		else name += "От ";
    		name += new SimpleDateFormat("dd.MM.yyyy").format(getReestr_date());
		}
    	setName(name);
    }
    
    @Autowired
	ObjService objService;
    @Autowired
	UserService userService;
    @Autowired
	HelperService hs;
    
	public static String singleTitle() { return "Реестр сдачи документов"; }
	public static String multipleTitle() { return "Реестры сдачи документов"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("reestr_number", "Номер реестра"));
		ret.add(new ColumnInfo("reestr_date", "Дата реестра"));
		ret.add(new ColumnInfo("reestr_status__name", "Статус реестра", true, true, "reestrstatus__rn", "select", "listReestrStatus"));
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
    	setReestr_status((SpReestrStatus)objService.getObjByCode(SpReestrStatus.class, "0"));
    	setTime_status(dt);
    	setDoc_count(0);
		setSheet_count(0);
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
		int doccount = 0, sheetcount = 0;
		Page<Object> p = objService.findAll(Document.class, null, new String[] {"reestr__rn"}, new Object[] {getRn()});
		if (p != null && p.getContent() != null) {
			for (Object o : p.getContent()) {
				Document doc = (Document)o;
				doccount++;
				if (doc.getSheet_count() != null) sheetcount += doc.getSheet_count();
			}
		}
		setDoc_count(doccount);
		setSheet_count(sheetcount);
		objService.saveObj(this);
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
			model.addAttribute("listReestrStatus", objService.findAll(SpReestrStatus.class));
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
