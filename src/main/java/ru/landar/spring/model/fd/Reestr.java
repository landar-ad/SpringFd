package ru.landar.spring.model.fd;

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
import org.springframework.ui.Model;

import ru.landar.spring.ObjectChanged;
import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IAgent;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.IUser;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.CascadeType;
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
	private List<IFile> list_file;
	
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
	
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.EAGER)
    public IAgent getAgent_from() { return agent_from; }
    public void setAgent_from(IAgent agent_from) { this.agent_from = agent_from; }
	
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.EAGER)
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
    
    @ManyToMany(targetEntity=IFile.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<IFile> getList_file() { return list_file != null ? list_file : new ArrayList<IFile>(); }
    public void setList_file(List<IFile> list_file) { this.list_file = list_file; }
    
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "Реестр ";
    	if (!hs.isEmpty(getReestr_number())) name += " № "+ getReestr_number();
    	if (getReestr_date() != null) name += " от " + new SimpleDateFormat("dd.MM.yyyy").format(getReestr_date());
    	hs.setProperty(this, "name", name);
    }
    
    @Autowired
	ObjService objService;
    @Autowired
	UserService userService;
    @Autowired
	HelperService hs;
    @Resource(name = "getObjectChanged")
    ObjectChanged objectChanged;
    
	public static String singleTitle() { return "Реестр сдачи документов"; }
	public static String multipleTitle() { return "Реестры сдачи документов"; }
	public static String menuTitle() { return multipleTitle(); }
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
	@Override
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("edit", "Редактировать", "edit"));
		ret.add(new ButtonInfo("view", "Просмотреть", "readme"));
		String roles = userService.getRoles(null);
		if (roles != null && (roles.indexOf("ADMIN") >= 0 || roles.indexOf("DF") >= 0)) {
			ret.add(new ButtonInfo("newReestr", "Сформировать новый реестр", "plus-square", "primary"));
			ret.add(new ButtonInfo("sendReestr", "Передать в ФК", null, "primary"));
		}
		ret.add(new ButtonInfo("printReestr", "Печать", "print", "primary"));
		return ret;
	}
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	Date dt = new Date();
    	IUser user = userService.getUser((String)null);
    	if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
    	hs.setProperty(this, "create_agent", user.getPerson());
    	hs.setProperty(this, "create_time", dt);
    	hs.setProperty(this, "change_agent", user.getPerson());
    	hs.setProperty(this, "change_time", dt);
    	hs.setProperty(this, "reestr_date", dt);
    	if (getDepart() == null) hs.setProperty(this, "depart", hs.getDepartment());
    	hs.setProperty(this, "reestr_status", (SpReestrStatus)objRepository.findByCode(SpReestrStatus.class, "1"));
    	hs.setProperty(this, "time_status", dt);
    	hs.setProperty(this, "doc_count", 0);
    	hs.setProperty(this, "sheet_count", 0);
     	return true;
    }
    @Override
    public Object onUpdate() throws Exception {
    	Object ret = super.onUpdate();
    	if (ret != null) return ret;
    	Date dt = new Date();
    	IUser user = userService.getUser((String)null);
    	if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
    	hs.setProperty(this, "change_agent", user.getPerson());
    	hs.setProperty(this, "change_time", dt);
    	if (objectChanged.isAttrChanged(this, "reestr_status")) hs.setProperty(this, "time_status", dt);
		int doccount = 0, sheetcount = 0;
		for (Document doc : getList_doc()) {
			doccount++;
			if (doc.getSheet_count() != null) sheetcount += doc.getSheet_count();
		}
		hs.setProperty(this, "doc_count", doccount);
		hs.setProperty(this, "sheet_count", sheetcount);
		return true;
	}
    public void onUpdateItem(Class<?> clItem, Integer rnItemOld, Integer rnItem) {
    	if (clItem == null || rnItemOld == null || rnItem == null || rnItemOld == rnItem) return;
    	if (Document.class.isAssignableFrom(clItem)) {
    		Document docOld = (Document)objRepository.find(clItem, rnItemOld);
    		Document doc = (Document)objRepository.find(clItem, rnItem);
    		if (docOld != null) {
    			hs.setProperty(docOld, "doc_status", (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "8"));
    			hs.setProperty(docOld, "change_doc", doc);
    			Integer version = docOld.getVersion();
    			if (version == null) version = 1;
    			hs.setProperty(doc, "version", version + 1);
    		}
    		if (doc != null) {
    			hs.setProperty(docOld, "reestr", null);
    			hs.setProperty(docOld, "doc_status", (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "6"));
    		}
    		hs.setProperty(this, "reestr_status", (SpReestrStatus)objRepository.findByCode(SpReestrStatus.class, "4"));
    	}
    }
    @Override
    public Object onRemove() {
    	Object ret = super.onRemove();
    	if (ret != null) return ret;
    	List<Document> l = getList_doc();
    	for (Document doc : l) {
    		hs.setProperty(doc, "reestr", null);
    		hs.setProperty(doc, "doc_status", (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "4"));
    	}
    	return true;
    }
	@Override
	public Object onListAddFilter(List<String> listAttr, List<Object> listValue, Map<String, String[]> mapParam) {
 		Object ret = super.onListAddFilter(listAttr, listValue, mapParam);
		if (ret != null) return ret;
		IUser user = userService.getUser((String)null);
		if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
		String roles = user.getRoles();
		if (roles.indexOf("ADMIN") < 0 && roles.indexOf("DF") < 0) {
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
				model.addAttribute("listPerson", objService.findAll(IPerson.class));
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
     	if (op == Operation.create) return userService.isAdmin(null);
    	if (rn == null) return true;
    	if (op == Operation.update || op == Operation.delete) {
    		if (userService.isAdmin(null)) return true;
    		if (hs.checkPerson(getCreate_agent())) return true;
    		if (hs.checkDepartment(getDepart())) return true;
			return false;
    	}
    	return true;
    }
    @Override
	public Object onCheckUpdateAttribute(String attr) { 
     	Object ret = super.onCheckUpdateAttribute(attr);
    	if (ret != null) return ret;
    	int st = statusCode();
    	if (st == 1) return true;
    	if (userService.isAdmin(null)) return true;
    	if (attr.startsWith("list_file")) return true;
     	return false;
    }
    @Override
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
     	if ("newReestr".equals(param)) return true;
     	else if ("add".equals(param)) return onCheckRights(Operation.create);
     	if (getRn() == null) return false;
     	if ("printReestr".equals(param)) return true;
     	String roles = userService.getRoles(null);
     	if ("edit".equals(param)) return onCheckRights(Operation.update);
		else if ("remove".equals(param)) return onCheckRights(Operation.delete);
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		else if ("sendReestr".equals(param)) {
			if (statusCode() != 1) return false;
			if (roles.indexOf("DF") >= 0) return true;
		}
		return false;
    }
    @Autowired
	ObjRepositoryCustom objRepository;
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void newReestr(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	for (; ;) {
    		Page<?> p = objRepository.findAll(Document.class, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").ascending()), new String[] {"doc_status__code"}, new Object[] {"4"});
    		if (p == null || p.isEmpty())  break;
	    	onNew();
	    	Integer max = (Integer)objRepository.getMaxAttr(Reestr.class, "reestr_num");
	    	if (max == null) max = 0;
	    	hs.setProperty(this, "reestr_num", max + 1); 
	    	hs.setProperty(this, "reestr_number", "" + getReestr_num());
	    	Reestr reestr = (Reestr)objRepository.createObj(this);
	    	SpDocStatus doc_status = (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "6");
	    	List<Document> l = reestr.getList_doc();
	    	for (Object o : p.getContent()) {
	    		Document doc = (Document)o;
	    		hs.setProperty(doc, "doc_status", doc_status);
	    		hs.setProperty(doc, "reestr", reestr);
	    		objRepository.saveObj(doc);
	    		l.add(doc);
	    	}
	    	hs.setProperty(reestr, "list_doc", l);
	    	break;
		}
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void sendReestr(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("sendReestr")) return;
		SpDocStatus doc_status = (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "7");
		for (Document doc : getList_doc()) {
			hs.setProperty(doc, "doc_status", doc_status);
			objRepository.saveObj(doc);
		}
		hs.setProperty(this, "reestr_status", (SpReestrStatus)objRepository.findByCode(SpReestrStatus.class, "2"));
    }
    public String printReestr(HttpServletRequest request) throws Exception {
    	return getRn() != null ? "/printReestr?rn=" + getRn() : null;
    }
    private int statusCode() {
    	int ret = 1; 
    	try { ret = Integer.valueOf(getReestr_status().getCode()); } catch (Exception ex) { }
    	return ret;
    }
}
