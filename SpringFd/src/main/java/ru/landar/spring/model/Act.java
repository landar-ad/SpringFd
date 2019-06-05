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

import javax.persistence.CascadeType;
import javax.persistence.Column;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Act extends IBase {
	private String act_number;
	private Integer act_num;
	private Date act_date;
	private SpActStatus act_status;
	private String act_reason;
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
    
    public Integer getAct_num() { return act_num; }
    public void setAct_num(Integer act_num) { this.act_num = act_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getAct_date() { return act_date; }
    public void setAct_date(Date act_date) { this.act_date = act_date; updateName(); }
    
	@ManyToOne(targetEntity=SpActStatus.class, fetch=FetchType.LAZY)
    public SpActStatus getAct_status() { return act_status; }
    public void setAct_status(SpActStatus act_status) { this.act_status = act_status; }
	
    @Column(length=256)
    public String getAct_reason() { return act_reason; }
    public void setAct_reason(String act_reason) { this.act_reason = act_reason; }
    
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
    	String name = "Акт приема-передачи";
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
    
	public static String singleTitle() { return "Акт приема-передачи"; }
	public static String multipleTitle() { return "Акты приема-передачи"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("act_number", "Номер акта"));
		ret.add(new ColumnInfo("act_date", "Дата акта"));
		ret.add(new ColumnInfo("act_status__name", "Статус акта", true, true, "act_status__rn", "select", "listActStatus"));
		ret.add(new ColumnInfo("time_status", "Дата изменения статуса"));
		ret.add(new ColumnInfo("act_reason", "Причина отказа"));
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
    	setAct_num(0);
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
    	if (mapChanged.containsKey("act_status")) setTime_status(dt);
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
			doc.setAct(null);
			doc.setDoc_status((SpDocStatus)objRepository.findByCode(SpDocStatus.class, "2"));
			objRepository.saveObj(doc);
			act_doc.setDoc(null);
			objRepository.saveObj(act_doc);
    	}
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
    	if (op == Operation.update || op == Operation.delete) {
    		if (userService.isAdmin(null)) return true;
	     	IUser user = userService.getUser((String)null);
			if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
			String roles = user.getRoles();
			if (roles.indexOf("ADMIN") >= 0) return true;
			String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { }
    		if (op == Operation.delete && !"1".equals(act_status)) return false;
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
     	IDepartment dep = hs.getDepartment(), depart = getDepart();
    	if ("newAct".equals(param)) return dep != null;
    	if (getRn() == null) return false;
    	if ("edit".equals(param)) {
    		if (!(Boolean)onCheckRights(Operation.update)) return false;
    		if (userService.isAdmin(null)) return true;
    		String act_status = "1";try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"1".equals(act_status) && !"3".equals(act_status) && !"6".equals(act_status)) return false;
    		return true;
    	}
		else if ("remove".equals(param)) {
			if (!(Boolean)onCheckRights(Operation.delete)) return false;
    		String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"1".equals(act_status)) return false;
    		return true;
		}
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		else if ("sendAct".equals(param)) {
			if (!hs.checkDepartment(depart)) return false;
			String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"1".equals(act_status)) return false;
			return true;
		}
		else if ("acceptAct".equals(param)) {
			if (!hs.checkDepartment(depart)) return false;
			String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"2".equals(act_status)) return false;
			return true;
		}
		else if ("confirmAct".equals(param)) {
			if (!hs.checkDepartment(depart)) return false;
			String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"3".equals(act_status)) return false;
			return true;
		}
		else if ("refuseAct".equals(param)) {
			if (!hs.checkDepartment(depart)) return false;
			String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"3".equals(act_status)) return false;
			return true;
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
    		setAct_num(max + 1); 
    		setAct_number("" + getAct_num());
	    	Act act = (Act)objRepository.createObj(this);
	    	List<Act_document> l = act.getList_doc();
	    	for (Object o : p.getContent()) {
	    		Document doc = (Document)o;
	    		doc.setDoc_status((SpDocStatus)objRepository.findByCode(SpDocStatus.class, "3"));
	    		doc.setAct(act);
	    		Act_document act_doc = new Act_document();
	    		act_doc.setDoc(doc);
	    		act_doc.onNew();
	    		act_doc = (Act_document)objRepository.createObj(act_doc);
	    		l.add(act_doc);
	    	}
	    	act.setList_doc(l);
	    	break;
		}
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void sendAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	for (; ;) {
			IDepartment dep = hs.getDepartment();
			if (dep == null || getDepart() == null || dep.getRn() != getDepart().getRn()) break;
    		String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"1".equals(act_status)) break;
    		setAct_status((SpActStatus)objRepository.findByCode(SpActStatus.class, "2"));
    		break;
    	}
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void acceptAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	for (; ;) {
    		if (!hs.checkDepartment(getDepart())) break;
    		String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"2".equals(act_status)) break;
    		setAct_status((SpActStatus)objRepository.findByCode(SpActStatus.class, "3"));
    		break;
		}
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void confirmAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	for (; ;) {
			if (!hs.checkDepartment(getDepart())) break;
    		String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"3".equals(act_status)) break;
			act_status = "5";
			for (Act_document act_doc : getList_doc()) {
				boolean e = act_doc.getExclude() != null && act_doc.getExclude();
				if (!e) {
					act_status = "4";
					break;
				}
			}
			for (Act_document act_doc : getList_doc()) {
				boolean e = act_doc.getExclude() != null && act_doc.getExclude();
				if ("5".equals(act_status) && e) e = true;
				// Изменить документ
				Document doc = act_doc.getDoc();
				if (doc == null) continue;
				doc.setDoc_status((SpDocStatus)objRepository.findByCode(SpDocStatus.class, !e ? "4" : "5"));
				objRepository.saveObj(doc);
			}
			setAct_status((SpActStatus)objRepository.findByCode(SpActStatus.class, act_status));
    		break;
		}
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void refuseAct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	for (; ;) {
			if (!hs.checkDepartment(getDepart())) break;
    		String act_status = "1"; try { act_status = getAct_status().getCode(); } catch (Exception ex) { } 
    		if (!"3".equals(act_status)) break;
			SpDocStatus doc_status = (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "5");
			for (Act_document act_doc : getList_doc()) {
				// Изменить документ
				Document doc = act_doc.getDoc();
				if (doc == null) continue;
				doc.setDoc_status(doc_status);
				objRepository.saveObj(doc);
			}
			setAct_reason("Отклонен пользователем");
			setAct_status((SpActStatus)objRepository.findByCode(SpActStatus.class, "6"));
    		break;
		}
    }
}
