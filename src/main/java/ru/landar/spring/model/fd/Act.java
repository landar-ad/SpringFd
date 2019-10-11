package ru.landar.spring.model.fd;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.LockModeType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IAgent;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SpCommon;
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
@ObjectTitle(single="Акт приема-передачи", multi="Акты приема-передачи")
public class Act extends IBase {
	private String act_number;
	private Integer act_num;
	private Date act_date;
	private SpCommon act_status;
	private String act_reason;
	private Date time_status;
	private IAgent create_agent;
	private IDepartment depart;
	private Date create_time;
	private IAgent change_agent;
	private Date change_time;
	private List<Act_document> list_doc;
	private List<IFile> list_file;
	
	@FieldTitle(name="Номер акта")
    @Column(length=40)
    public String getAct_number() { return act_number; }
    public void setAct_number(String act_number) { this.act_number = act_number; updateName(); }
    
    @FieldTitle(name="Порядковый номер")
    public Integer getAct_num() { return act_num; }
    public void setAct_num(Integer act_num) { this.act_num = act_num; }
    
    @FieldTitle(name="Дата акта")
    @Temporal(TemporalType.DATE)
    public Date getAct_date() { return act_date; }
    public void setAct_date(Date act_date) { this.act_date = act_date; updateName(); }
    
    @FieldTitle(name="Статус акта", sp="sp_sa")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getAct_status() { return act_status; }
    public void setAct_status(SpCommon act_status) { this.act_status = act_status; }
	
    @FieldTitle(name="Причина отказа")
    @Column(length=256)
    public String getAct_reason() { return act_reason; }
    public void setAct_reason(String act_reason) { this.act_reason = act_reason; }
    
    @FieldTitle(name="Дата изменения статуса")
	public Date getTime_status() { return time_status; }
    public void setTime_status(Date time_status) { this.time_status = time_status; }
	
    @FieldTitle(name="Создан")
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.EAGER)
    public IAgent getCreate_agent() { return create_agent; }
    public void setCreate_agent(IAgent create_agent) { this.create_agent = create_agent; }
	
    @FieldTitle(name="Структурное подразделение")
	@ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getDepart() { return depart; }
    public void setDepart(IDepartment depart) { this.depart = depart; }
	
    @FieldTitle(name="Дата создания")
    public Date getCreate_time() { return create_time; }
    public void setCreate_time(Date create_time) { this.create_time = create_time; }
	
    @FieldTitle(name="Изменен")
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.EAGER)
    public IAgent getChange_agent() { return change_agent; }
    public void setChange_agent(IAgent change_agent) { this.change_agent = change_agent; }
	
    @FieldTitle(name="Дата изменения")
    public Date getChange_time() { return change_time; }
    public void setChange_time(Date change_time) { this.change_time = change_time; }
    
    @FieldTitle(name="Документы, включенные в акт")
    @OneToMany(targetEntity=Act_document.class, cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
    public List<Act_document> getList_doc() { return list_doc != null ? list_doc : new ArrayList<Act_document>(); }
    public void setList_doc(List<Act_document> list_doc) { this.list_doc = list_doc; }
    
    @FieldTitle(name="Прикрепленные файлы")
    @ManyToMany(targetEntity=IFile.class, cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
    public List<IFile> getList_file() { return list_file != null ? list_file : new ArrayList<IFile>(); }
    public void setList_file(List<IFile> list_file) { this.list_file = list_file; }
	
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "Акт П/П";
    	if (!hs.isEmpty(getAct_number())) name += " № " + getAct_number();
    	if (getAct_date() != null) name += " от " + new SimpleDateFormat("dd.MM.yyyy").format(getAct_date());
    	setName(name);
    }
    
    @Autowired
	ObjService objService;
    @Autowired
	UserService userService;
    @Autowired
	HelperService hs;
    @Resource(name = "getObjectChanged")
    ObjectChanged objectChanged;
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Act.class;
		ret.add(new ColumnInfo("act_number", cl));
		ret.add(new ColumnInfo("act_date", cl));
		ret.add(new ColumnInfo("act_status", cl));
		ret.add(new ColumnInfo("time_status", cl));
		ret.add(new ColumnInfo("act_reason", cl));
		ret.add(new ColumnInfo("create_agent__name", cl));
		ret.add(new ColumnInfo("depart__name", cl));
		ret.add(new ColumnInfo("create_time", cl));
		ret.add(new ColumnInfo("change_agent__name", cl));
		ret.add(new ColumnInfo("change_time", cl));
		return ret;
	}
	@Override
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("edit", "Редактировать", "edit"));
		ret.add(new ButtonInfo("view", "Просмотреть", "readme"));
		ret.add(new ButtonInfo("newAct", "Сформировать новый акт", "plus-square", "primary"));
		ret.add(new ButtonInfo("sendAct", "Отправить", null, "primary"));
		String roles = userService.getRoles(null);
		if (roles.indexOf("ADMIN") >= 0 || roles.indexOf("DF") >= 0) {
			ret.add(new ButtonInfo("acceptAct", "Принять", null, "primary"));
			ret.add(new ButtonInfo("confirmAct", "Утвердить", null, "primary"));
			ret.add(new ButtonInfo("refuseAct", "Отказать", null, "primary"));
		}
		ret.add(new ButtonInfo("printAct", "Печать", "print", "primary"));
		ret.add(new ButtonInfo("printActRet", "Печать акта возврата", null, "primary"));
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
    	setAct_num(0);
    	if (getDepart() == null) setDepart(hs.getDepartment());
    	setAct_status((SpCommon)objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sa", "1"}));
    	setTime_status(dt);
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
    	if (objectChanged.isAttrChanged(this, "act_status")) hs.setProperty(this, "time_status", dt);
    	return true;
	}
	@Override
    public Object onRemove() {
    	Object ret = super.onRemove();
    	if (ret != null) return ret;
    	List<Act_document> l = getList_doc();
    	for (Act_document act_doc : l) {
    		Document doc = act_doc.getDoc();
    		if (doc == null) continue;
    		hs.setProperty(doc, "doc_status", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sd", "2"}));
    		hs.setProperty(doc, "act", null);
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
			model.addAttribute("listSp_sa", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sa"}));
			if (!list) {
				model.addAttribute("listIDepartment", objService.findAll(IDepartment.class));
				model.addAttribute("listIOrganization", objService.findAll(IOrganization.class));
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
     	if (op == Operation.create) return getDepart() != null;
    	if (rn == null) return true;
    	String roles = userService.getRoles(null);
    	int st = statusCode();
    	if (op == Operation.update || op == Operation.delete) {
    		if (userService.isAdmin(null)) return true;
    		if (st == 1 && hs.checkPerson(getCreate_agent()) && hs.checkDepartment(getDepart())) return true;
    		if (op == Operation.update && (st == 6 || st == 3) && roles.indexOf("DF") > 0) return true;
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
    	if ((st == 3) && (attr.startsWith("list_doc"))) return true;
    	if ((st == 6) && ("act_reason".equals(attr))) return true;
    	if (attr.startsWith("list_file")) return true;
     	return false;
    }
    @Override
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
     	IDepartment dep = hs.getDepartment(), depart = getDepart();
    	if ("newAct".equals(param)) return dep != null;
    	else if ("add".equals(param)) return onCheckRights(Operation.create);
    	if (getRn() == null) return false;
    	if ("printAct".equals(param)) return true;
    	else if ("printActRet".equals(param)) {
    		if (statusCode() == 6) return true;
    		if (statusCode() == 5) {
	    		for (Act_document act_doc : getList_doc()) {
	    			if (act_doc.getDoc() != null && act_doc.getExclude() != null && (Boolean)act_doc.getExclude()) return true;
	    		}
    		}
    		return false;
    	}
    	String roles = userService.getRoles(null);
    	if ("edit".equals(param)) return onCheckRights(Operation.update);
		else if ("remove".equals(param)) return onCheckRights(Operation.delete);
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		else if ("sendAct".equals(param)) {
			if (statusCode() != 1) return false;
			if (hs.checkPerson(getCreate_agent())) return true;
			if (hs.checkDepartment(depart)) return true;
			if (statusCode() == 1) return true;
		}
		else if ("acceptAct".equals(param)) {
			if (statusCode() != 2) return false;
			if (roles.indexOf("DF") >= 0) return true;
		}
		else if ("confirmAct".equals(param)) {
			if (statusCode() != 3) return false;
			if (roles.indexOf("DF") >= 0) return true;
		}
		else if ("refuseAct".equals(param)) {
			if (statusCode() != 3) return false;
			if (roles.indexOf("DF") >= 0) return true;
		}
		return false;
    }
    @Autowired
	ObjRepositoryCustom objRepository;
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void newAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	for (; ;) {
			IDepartment dep = hs.getDepartment();
			if (dep == null) break;
			Page<?> p = objRepository.findAll(Document.class, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").ascending()), new String[] {"doc_status__code", "depart__rn"}, new Object[] {"2", dep.getRn()});
			if (p == null || p.isEmpty()) break;
	    	onNew();
	    	Integer max = (Integer)objRepository.getMaxAttr(Act.class, "act_num");
	    	if (max == null) max = 0;
	    	hs.setProperty(this, "act_num", max + 1); 
	    	hs.setProperty(this, "act_number", "" + getAct_num());
	    	Act act = (Act)objRepository.createObj(this);
	    	SpCommon doc_status = (SpCommon)objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sd", "3"});
	    	List<Act_document> l = act.getList_doc();
	    	for (Object o : p.getContent()) {
	    		Document doc = (Document)o;
	    		hs.setProperty(doc, "doc_status", doc_status);
	    		hs.setProperty(doc, "act", act);
	    		objRepository.saveObj(doc);
	    		Act_document act_doc = new Act_document();
	    		hs.setProperty(act_doc, "doc", doc);
	    		act_doc.onNew();
	    		act_doc = (Act_document)objRepository.createObj(act_doc);
	    		l.add(act_doc);
	    	}
	    	hs.setProperty(act, "list_doc", l);
	    	break;
		}
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void sendAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("sendAct")) return;
    	hs.setProperty(this, "act_status", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sa", "2"}));
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void acceptAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("acceptAct")) return;
    	hs.setProperty(this, "act_status", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sa", "3"}));
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void confirmAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("confirmAct")) return;
		boolean e = false, ne = false;
		for (Act_document act_doc : getList_doc()) {
			if (act_doc.getExclude() != null && act_doc.getExclude()) e = true;
			else ne = true;
		}
		String act_status = "4";
		if (ne && !e) act_status = "4";
		else if (!ne && e) act_status = "6";
		else if (ne && e) act_status = "5";
		// Изменить документы
		SpCommon doc_status4 = (SpCommon)objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sd", "4"});
		SpCommon doc_status5 = (SpCommon)objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sd", "5"});
		for (Act_document act_doc : getList_doc()) {
			e = act_doc.getExclude() != null && act_doc.getExclude();
			Document doc = act_doc.getDoc();
			if (doc == null) continue;
			hs.setProperty(doc, "doc_status", e ? doc_status5 : doc_status4);
			objRepository.saveObj(doc);
		}
		hs.setProperty(this, "act_status", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sa", act_status}));
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void refuseAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("refuseAct")) return;
    	SpCommon doc_status = (SpCommon)objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sd", "5"});
		for (Act_document act_doc : getList_doc()) {
			Document doc = act_doc.getDoc();
			if (doc == null) continue;
			hs.setProperty(doc, "doc_status", doc_status);
			objRepository.saveObj(doc);
		}
		hs.setProperty(this, "act_reason", "Отклонен пользователем");
		hs.setProperty(this, "act_status", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sa", "6"}));
    }
    public String printAct(HttpServletRequest request) throws Exception {
    	return getRn() != null ? "/printAct?rn=" + getRn() : null;
    }
    public String printActRet(HttpServletRequest request) throws Exception {
    	return getRn() != null ? "/printActRet?rn=" + getRn() : null;
    }
    private int statusCode() {
    	int ret = 1; 
    	try { ret = Integer.valueOf(getAct_status().getCode()); } catch (Exception ex) { }
    	return ret;
    }
}
