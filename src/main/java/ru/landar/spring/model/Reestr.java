package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.LockModeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.repository.ObjRepositoryCustom;
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
	private Integer reestr_num;
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
	private List<Document> list_doc;
	
    @Column(length=40)
    public String getReestr_number() { return reestr_number; }
    public void setReestr_number(String reestr_number) { this.reestr_number = reestr_number; updateName(); }
    
    public Integer getReestr_num() { return reestr_num; }
    public void setReestr_num(Integer reestr_num) { this.reestr_num = reestr_num; }
        
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

    @ManyToMany(targetEntity=Document.class, fetch=FetchType.LAZY)
    public List<Document> getList_doc() { return list_doc != null ? list_doc : new ArrayList<Document>(); }
    public void setList_doc(List<Document> list_doc) { this.list_doc = list_doc; }
    
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "Реестр сдачи документов";
    	if (!hs.isEmpty(getReestr_number())) name = " № "+ getReestr_number();
    	if (getReestr_date() != null) name += " от " + new SimpleDateFormat("dd.MM.yyyy").format(getReestr_date());
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
		ret.add(new ColumnInfo("reestr_status__name", "Статус реестра", true, true, "reestr_status__rn", "select", "listReestrStatus"));
		ret.add(new ColumnInfo("agent_from__name", "Документы сдал"));
		ret.add(new ColumnInfo("agent_to__name", "Документы принял"));
		ret.add(new ColumnInfo("mol__name", "Материально-ответственное лицо"));
		ret.add(new ColumnInfo("time_status", "Дата изменения статуса"));
		ret.add(new ColumnInfo("create_agent__name", "Создан"));
		ret.add(new ColumnInfo("depart__name", "Структурное подразделение"));
		ret.add(new ColumnInfo("create_time", "Дата создания"));
		ret.add(new ColumnInfo("change_agent__name", "Изменен"));
		ret.add(new ColumnInfo("change_time", "Дата изменения"));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	public static List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("newReestr", "Сформировать новый реестр"));
		ret.add(new ButtonInfo("sendReestr", "Передать в ФК"));
		return ret;
	}
	
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
    	setReestr_date(dt);
    	setDepart(hs.getDepartment());
    	setReestr_status((SpReestrStatus)objService.getObjByCode(SpReestrStatus.class, "1"));
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
    	setChange_agent(user.getPerson());
    	setChange_time(dt);
		int doccount = 0, sheetcount = 0;
		for (Document doc : getList_doc()) {
			doccount++;
			if (doc.getSheet_count() != null) sheetcount += doc.getSheet_count();
		}
		setDoc_count(doccount);
		setSheet_count(sheetcount);
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
				listValue.add("= " + rnDep);
			}
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
    	if (op == Operation.update || op == Operation.delete) {
	     	IUser user = userService.getUser((String)null);
			if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
			String roles = user.getRoles();
			if (roles.indexOf("ADMIN") >= 0) return true;
			String reestr_status = "1"; try { reestr_status = getReestr_status().getCode(); } catch (Exception ex) { }
    		if (!"1".equals(reestr_status)) return false;
			IAgent agent = getCreate_agent(), person = user.getPerson();
			if (person != null && agent != null && person.getRn() == agent.getRn()) return true;
			if (hs.checkDepartment(getDepart())) return true;
			return false;
    	}
    	return true;
    }
    @Override
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
     	if ("newReestr".equals(param)) return true;
     	if (getRn() == null) return false;
    	if ("edit".equals(param)) {
    		if (!(Boolean)onCheckRights(Operation.update)) return false;
    		String reestr_status = "1"; try { reestr_status = getReestr_status().getCode(); } catch (Exception ex) { } 
    		if (!"1".equals(reestr_status)) return false;
    		return true;
    	}
		else if ("remove".equals(param)) {
			if (!(Boolean)onCheckRights(Operation.delete)) return false;
    		String reestr_status = "1"; try { reestr_status = getReestr_status().getCode(); } catch (Exception ex) { } 
    		if (!"1".equals(reestr_status)) return false;
    		return true;
		}
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		else if ("sendReestr".equals(param)) {
			String reestr_status = "1"; try { reestr_status = getReestr_status().getCode(); } catch (Exception ex) { } 
    		if (!"1".equals(reestr_status)) return false;
			return true;
		}
		return false;
    }
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
	ObjRepositoryCustom objRepository;
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void newReestr(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
    		Page<?> p = objRepository.findAll(Document.class, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").ascending()), new String[] {"doc_status__code"}, new Object[] {"4"});
    		if (p != null && !p.isEmpty()) {
		    	Reestr reestr = new Reestr();
		    	reestr.onNew();
		    	Integer max = (Integer)objRepository.getMaxAttr(Reestr.class, "reestr_num");
		    	if (max == null) max = 0;
	    		reestr.setReestr_num(max + 1); 
	    		reestr.setReestr_number("" + getReestr_num());
		    	reestr = (Reestr)objRepository.createObj(reestr);
		    	List<Document> l = reestr.getList_doc();
		    	for (Object o : p.getContent()) {
		    		Document doc = (Document)o;
		    		doc.setDoc_status((SpDocStatus)objRepository.findByCode(SpDocStatus.class, "6"));
		    		doc.setReestr(reestr);
		    		objRepository.saveObj(doc);
		    		l.add(doc);
		    	}
		    	reestr.setList_doc(l);
		    	objRepository.saveObj(reestr);
    		}
	    	transactionManager.commit(ts);
    	}
    	catch (Exception ex) {
    		transactionManager.rollback(ts);
    	}
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void sendReestr(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
    		for (; ;) {
    			IDepartment dep = hs.getDepartment();
    			if (dep == null || getDepart() == null || dep.getRn() != getDepart().getRn()) break;
	    		String reestr_status = "1";
	    		try { reestr_status = getReestr_status().getCode(); } catch (Exception ex) { } 
	    		if (!"1".equals(reestr_status)) break;
	    		SpDocStatus doc_status = (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "7");
    			for (Document doc : getList_doc()) {
    				doc.setDoc_status(doc_status);
    				objRepository.saveObj(doc);
    			}
    			setReestr_status((SpReestrStatus)objRepository.findByCode(SpReestrStatus.class, "2"));
    			objRepository.saveObj(this);
	    		break;
    		}
	    	transactionManager.commit(ts);
		}
		catch (Exception ex) {
			transactionManager.rollback(ts);
		}
    }
}
