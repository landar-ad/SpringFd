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
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.ui.Model;

import ru.landar.spring.ObjectChanged;
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

import javax.annotation.Resource;
import javax.persistence.CascadeType;
import javax.persistence.Column;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Document extends IBase {
	private SpDocType doc_type;
	private String doc_number;
	private Integer number;
	private Date doc_date;
	private Document parent_doc;
	private IAgent agent;
	private SpDocStatus doc_status;
	private Date time_status;
	private IAgent create_agent;
	private IDepartment depart;
	private Date create_time;
	private IAgent change_agent;
	private Date change_time;
	private Act act;
	private Reestr reestr;
	private String act_exclude_num;
	private Date act_exclude_date;
	private String act_exclude_reason;
	private Document change_doc;
	private Date buh_date;
	private String extract_number;
	private Date extract_date;
	private List<IFile> list_file;
	private Integer sheet_count;
	private String sp_year;
	private String sp_num;
	private String sp_subnum;
	
	@ManyToOne(targetEntity=SpDocType.class, fetch=FetchType.LAZY)
    public SpDocType getDoc_type() { return doc_type; }
    public void setDoc_type(SpDocType doc_type) { this.doc_type = doc_type; updateName(); }
    
    @Column(length=40)
    public String getDoc_number() { return doc_number; }
    public void setDoc_number(String doc_number) { this.doc_number = doc_number; updateName(); }
    
    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
    
    @Temporal(TemporalType.DATE)
    public Date getDoc_date() { return doc_date; }
    public void setDoc_date(Date doc_date) { this.doc_date = doc_date; updateName(); }
    
    @ManyToOne(targetEntity=Document.class, fetch=FetchType.LAZY)
    public Document getParent_doc() { return parent_doc; }
    public void setParent_doc(Document parent_doc) { this.parent_doc = parent_doc; }

	@ManyToOne(targetEntity=IAgent.class)
    public IAgent getAgent() { return agent; }
    public void setAgent(IAgent agent) { this.agent = agent; }
	
	@ManyToOne(targetEntity=SpDocStatus.class, fetch=FetchType.LAZY)
    public SpDocStatus getDoc_status() { return doc_status; }
    public void setDoc_status(SpDocStatus doc_status) { this.doc_status = doc_status; }
	
    public Date getTime_status() { return time_status; }
    public void setTime_status(Date time_status) { this.time_status = time_status; }
	
	@ManyToOne(targetEntity=IAgent.class)
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
	
    @ManyToOne(targetEntity=Act.class, fetch=FetchType.LAZY)
    public Act getAct() { return act; }
    public void setAct(Act act) { this.act = act; }
    
    @Column(length=40)
    public String getAct_exclude_num() { return act_exclude_num; }
    public void setAct_exclude_num(String act_exclude_num) { this.act_exclude_num = act_exclude_num; }
    
    @Column(length=256)
    public String getAct_exclude_reason() { return act_exclude_reason; }
    public void setAct_exclude_reason(String act_exclude_reason) { this.act_exclude_reason = act_exclude_reason; }

    @Temporal(TemporalType.DATE)
    public Date getAct_exclude_date() { return act_exclude_date; }
    public void setAct_exclude_date(Date act_exclude_date) { this.act_exclude_date = act_exclude_date; }
	
    @ManyToOne(targetEntity=Reestr.class, fetch=FetchType.LAZY)
    public Reestr getReestr() { return reestr; }
    public void setReestr(Reestr reestr) { this.reestr = reestr; }

	@ManyToOne(targetEntity=Document.class, fetch=FetchType.LAZY)
    public Document getChange_doc() { return change_doc; }
    public void setChange_doc(Document change_doc) { this.change_doc = change_doc; }
	
	@Temporal(TemporalType.DATE)
    public Date getBuh_date() { return buh_date; }
    public void setBuh_date(Date buh_date) { this.buh_date = buh_date; }
	
	@Column(length=40)
    public String getExtract_number() { return extract_number; }
    public void setExtract_number(String extract_number) { this.extract_number = extract_number; }
	
	@Temporal(TemporalType.DATE)
    public Date getExtract_date() { return extract_date; }
    public void setExtract_date(Date extract_date) { this.extract_date = extract_date; }

    @ManyToMany(targetEntity=IFile.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<IFile> getList_file() { return list_file != null ? list_file : new ArrayList<IFile>(); }
    public void setList_file(List<IFile> list_file) { this.list_file = list_file; }
    
	public Integer getSheet_count() { return sheet_count; }
    public void setSheet_count(Integer sheet_count) { this.sheet_count = sheet_count; }
    
    @Column(length=4)
    public String getSp_year() { return sp_year; }
    public void setSp_year(String sp_year) { this.sp_year = sp_year; }
    
    @Column(length=1000)
    public String getSp_num() { return sp_num; }
    public void setSp_num(String sp_num) { this.sp_num = sp_num; }
    
    @Column(length=1000)
    public String getSp_subnum() { return sp_subnum; }
    public void setSp_subnum(String sp_subnum) { this.sp_subnum = sp_subnum; }
	
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "";
		if (getDoc_type() != null) name = getDoc_type().getName();
    	if (!hs.isEmpty(getDoc_number())) name += (!name.isEmpty() ? " " : "") + "№ " + getDoc_number();
    	if (getDoc_date() != null) name += (!name.isEmpty() ? " от " : "От ") + new SimpleDateFormat("dd.MM.yyyy").format(getDoc_date());
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
    
	public static String singleTitle() { return "Документ"; }
	public static String multipleTitle() { return "Документы"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("create_time", "Дата создания"));
		ret.add(new ColumnInfo("depart__code", "№ департамента"));
		ret.add(new ColumnInfo("doc_type__name", "Тип документа")); 
		ret.add(new ColumnInfo("doc_number", "№ документа"));
		ret.add(new ColumnInfo("doc_date", "Дата документа"));
		ret.add(new ColumnInfo("agent__name", "Контрагент"));
		ret.add(new ColumnInfo("parent_doc__name", "Основной документ", false));
		ret.add(new ColumnInfo("doc_status__name", "Статус документа", true, true, "doc_status__rn", "select", "listDocStatus"));
		ret.add(new ColumnInfo("time_status", "Дата присвоения статуса"));
		ret.add(new ColumnInfo("create_agent__name", "Создан", false));
		ret.add(new ColumnInfo("change_agent__name", "Изменен", false));
		ret.add(new ColumnInfo("change_time", "Дата изменения", false));
		ret.add(new ColumnInfo("act__name", "Включен в акт"));
		ret.add(new ColumnInfo("reestr__name", "Включен в реестр"));
		ret.add(new ColumnInfo("act_exclude_num", "Исключен из акта", false));
		ret.add(new ColumnInfo("change_doc__name", "Заменен документом", false));
		ret.add(new ColumnInfo("buh_date", "Дата бухучета"));
		ret.add(new ColumnInfo("sp_year", "Год проверки"));
		ret.add(new ColumnInfo("sp_num", "№ пункта запроса"));
		ret.add(new ColumnInfo("sp_subnum", "№ подпункта запроса"));
		ret.add(new ColumnInfo("extract_number", "Выписка: №", false));
		ret.add(new ColumnInfo("extract_date", "Выписка: дата", false));
		ret.add(new ColumnInfo("list_file", "Прикрепленные файлы", false));
		ret.add(new ColumnInfo("sheet_count", "Количество листов", false));
		ret.add(new ColumnInfo("version", "Версия"));
		return ret;
	}
	@Override
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = super.listButton();
		if (ret == null) ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("confirm", "Завершить подготовку документа", null, "success"));
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
    	hs.setProperty(this, "create_agent", user.getPerson());
      	hs.setProperty(this, "create_time", dt);
      	hs.setProperty(this, "change_agent", user.getPerson());
      	hs.setProperty(this, "change_time", dt);
      	hs.setProperty(this, "depart", hs.getDepartment());
      	hs.setProperty(this, "doc_date", dt);
      	hs.setProperty(this, "doc_status", (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "1"));
      	hs.setProperty(this, "time_status", dt);
      	hs.setProperty(this, "sheet_count", 0);
      	String dep_code = hs.getPropertyString(this, "depart__code");
      	if (!hs.isEmpty(dep_code)) {
      		Integer max = (Integer)objRepository.getMaxAttr(Document.class, "number", new String[] {"depart__code"}, new Object[] {dep_code});
      		if (max == null) max = 0;
      		hs.setProperty(this, "number", ++max);
      		hs.setProperty(this, "doc_number", String.format("%s-%04d", dep_code, max));
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
			model.addAttribute("listDocType", objService.findAll(SpDocType.class));
			model.addAttribute("listDocStatus", objService.findAll(SpDocStatus.class));
			if (!list) {
				model.addAttribute("listFileType", objService.findAll(SpFileType.class));
				model.addAttribute("listDocument", objService.findAll(Document.class));
				model.addAttribute("listDepartment", objService.findAll(IDepartment.class));
				model.addAttribute("listAgent", objService.findAll(IOrganization.class));
			}	
		}
		catch (Exception ex) { }
		return true;
	}
    @Override
    public Object onUpdate() throws Exception { 
    	Object ret = super.onUpdate();
    	if (ret != null) return ret;
    	if (objectChanged.isAttrChanged(this)) {
	    	Date dt = new Date();
	    	IUser user = userService.getUser((String)null);
	    	if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
	    	hs.setProperty(this, "change_agent", user.getPerson());
	    	hs.setProperty(this, "change_time", dt);
	    	if (objectChanged.isAttrChanged(this, "doc_status")) hs.setProperty(this, "time_status", dt);
    	}
		return true;
    }
    @Override
    public Object onCheckRights(Operation op) { 
    	Object ret = invoke("onCheckRights", op);
     	if (ret != null) return ret;
    	Integer rn = getRn();
    	if (op == Operation.create) return hs.getDepartment() != null;
    	if (rn == null) return true;
    	if (op == Operation.update || op == Operation.delete) {
    		if (getAct() != null || getReestr() != null) return false;
    		if (userService.isAdmin(null)) return true;
    		if (statusCode() != 1) return false;
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
    	if ("buh_date".equals(attr) || 
    		"extract_number".equals(attr) || 
    		"extract_date".equals(attr)) return true;
     	return false;
    }
    @Override
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
     	if ("add".equals(param)) return onCheckRights(Operation.create);
    	if (getRn() == null) return false;
    	if ("edit".equals(param)) return onCheckRights(Operation.update);
		else if ("remove".equals(param)) return onCheckRights(Operation.delete);
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		else if ("confirm".equals(param)) {
			if (statusCode() != 1) return false;
			if (userService.isAdmin(null)) return true;
			if (hs.checkPerson(getCreate_agent())) return true;
 			if (hs.checkDepartment(getDepart())) return true;
		}
		return false;
    }
    @Autowired
	ObjRepositoryCustom objRepository;
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void confirm(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("confirm")) return;
    	hs.setProperty(this, "doc_status", (SpDocStatus)objRepository.findByCode(SpDocStatus.class, "2"));
	}
    @Override
    public Object onBuildContent() {
    	Object ret = super.onBuildContent();
    	if (ret != null) return ret;
    	String ctx = objService.getServiceContext();
    	ret = new SearchContent();
    	SearchContent sc = (SearchContent)ret;
    	sc.setId(ctx + "_" + getRn());
    	sc.setClazz(getClazz());
    	sc.setName(getName());
    	sc.setContext(ctx);
    	String content = getName();
    	String v = hs.getPropertyString(this, "create_agent__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Создан: " + v;
    	}
    	v = hs.getPropertyString(this, "change_agent__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Изменен: " + v;
    	}
    	v = hs.getPropertyString(this, "depart__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Структурное подразделение: " + v;
    	}
    	v = hs.getPropertyString(this, "agent__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Контрагент: " + v;
    	}
    	v = hs.getPropertyString(this, "parent_doc__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Документ-основание: " + v;
    	}
    	v = hs.getPropertyString(this, "act__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Акт: " + v;
    	}
    	v = hs.getPropertyString(this, "act_exclude_num");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Исключен из акта: " + v;
    	}
    	v = hs.getPropertyString(this, "act_exclude_reason");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Причина исключения: " + v;
    	}
    	v = hs.getPropertyString(this, "reestr__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Реестр: " + v;
    	}
    	v = hs.getPropertyString(this, "change_doc__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Замена в реестре: " + v;
    	}
    	v = hs.getPropertyString(this, "doc_status__name");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Статус: " + v;
    	}
    	v = hs.getPropertyString(this, "extract_number");
    	if (!hs.isEmpty(v)) {
    		if (!hs.isEmpty(content)) content += "; ";
    		content += "Выписка: " + v;
    	}
    	sc.setContent(content);
    	return ret;
    }
    private int statusCode() {
    	int ret = 1; 
    	try { ret = Integer.valueOf(getDoc_status().getCode()); } catch (Exception ex) { }
    	return ret;
    }
}
