package ru.landar.spring.model.fd;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.LockModeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import ru.landar.spring.model.IMailing;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SearchContent;
import ru.landar.spring.model.SpCommon;
import ru.landar.spring.model.SpFileType;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
@ObjectTitle(single="Документ", multi="Документы")
public class Document extends IBase {
	private SpDocType doc_type;
	private String doc_number;
	private Integer number;
	private Date doc_date;
	private Document parent_doc;
	private IAgent agent;
	private SpCommon doc_status;
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
	private String sedkp_num;
	private Date sedkp_date;
	private String comment;
	private List<IMailing> mailing_list;
	
	@FieldTitle(name="Тип документа")
	@ManyToOne(targetEntity=SpDocType.class, fetch=FetchType.LAZY)
    public SpDocType getDoc_type() { return doc_type; }
    public void setDoc_type(SpDocType doc_type) { this.doc_type = doc_type; updateName(); }
    
    @FieldTitle(name="№ документа")
    @Column(length=40)
    public String getDoc_number() { return doc_number; }
    public void setDoc_number(String doc_number) { this.doc_number = doc_number; updateName(); }
    
    @FieldTitle(name="Порядковый номер")
    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }
    
    @FieldTitle(name="Дата документа")
    @Temporal(TemporalType.DATE)
    public Date getDoc_date() { return doc_date; }
    public void setDoc_date(Date doc_date) { this.doc_date = doc_date; updateName(); }
    
    @FieldTitle(name="Основной документ", visible=false, filterType="text")
    @ManyToOne(targetEntity=Document.class, fetch=FetchType.LAZY)
    public Document getParent_doc() { return parent_doc; }
    public void setParent_doc(Document parent_doc) { this.parent_doc = parent_doc; }
    
    @FieldTitle(name="Контрагент")
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getAgent() { return agent; }
    public void setAgent(IAgent agent) { this.agent = agent; }
	
    @FieldTitle(name="Статус документа", sp="sp_sd")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getDoc_status() { return doc_status; }
    public void setDoc_status(SpCommon doc_status) { this.doc_status = doc_status; }
	
    @FieldTitle(name="Дата присвоения статуса")
    public Date getTime_status() { return time_status; }
    public void setTime_status(Date time_status) { this.time_status = time_status; }
	
    @FieldTitle(name="Создан", nameField="Создан пользователем", visible=false, filterType="text")
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getCreate_agent() { return create_agent; }
    public void setCreate_agent(IAgent create_agent) { this.create_agent = create_agent; }
	
    @FieldTitle(name="Структурное подразделение", nameColumn="№ департамента")
	@ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getDepart() { return depart; }
    public void setDepart(IDepartment depart) { this.depart = depart; }
	
    @FieldTitle(name="Дата создания")
    public Date getCreate_time() { return create_time; }
    public void setCreate_time(Date create_time) { this.create_time = create_time; }
	
    @FieldTitle(name="Изменен", nameField="Изменен пользователем", visible=false, filterType="text")
	@ManyToOne(targetEntity=IAgent.class, fetch=FetchType.LAZY)
    public IAgent getChange_agent() { return change_agent; }
    public void setChange_agent(IAgent change_agent) { this.change_agent = change_agent; }
	
    @FieldTitle(name="Дата изменения", visible=false)
    public Date getChange_time() { return change_time; }
    public void setChange_time(Date change_time) { this.change_time = change_time; }
	
    @FieldTitle(name="Включен в акт", filterType="text")
    @ManyToOne(targetEntity=Act.class, fetch=FetchType.LAZY)
    public Act getAct() { return act; }
    public void setAct(Act act) { this.act = act; }
    
    @FieldTitle(name="Исключен из акта", visible=false, filterType="text")
    @Column(length=40)
    public String getAct_exclude_num() { return act_exclude_num; }
    public void setAct_exclude_num(String act_exclude_num) { this.act_exclude_num = act_exclude_num; }
    
    @FieldTitle(name="Причина исключения", visible=false)
    @Column(length=256)
    public String getAct_exclude_reason() { return act_exclude_reason; }
    public void setAct_exclude_reason(String act_exclude_reason) { this.act_exclude_reason = act_exclude_reason; }

    @FieldTitle(name="Дата исключения", visible=false)
    @Temporal(TemporalType.DATE)
    public Date getAct_exclude_date() { return act_exclude_date; }
    public void setAct_exclude_date(Date act_exclude_date) { this.act_exclude_date = act_exclude_date; }
	
    @FieldTitle(name="Включен в реестр", filterType="text")
    @ManyToOne(targetEntity=Reestr.class, fetch=FetchType.LAZY)
    public Reestr getReestr() { return reestr; }
    public void setReestr(Reestr reestr) { this.reestr = reestr; }

    @FieldTitle(name="Заменен документом", visible=false)
	@ManyToOne(targetEntity=Document.class, fetch=FetchType.LAZY)
    public Document getChange_doc() { return change_doc; }
    public void setChange_doc(Document change_doc) { this.change_doc = change_doc; }
	
    @FieldTitle(name="Дата бухучета")
	@Temporal(TemporalType.DATE)
    public Date getBuh_date() { return buh_date; }
    public void setBuh_date(Date buh_date) { this.buh_date = buh_date; }
	
    @FieldTitle(name="Выписка: №", visible=false)
	@Column(length=40)
    public String getExtract_number() { return extract_number; }
    public void setExtract_number(String extract_number) { this.extract_number = extract_number; }
	
    @FieldTitle(name="Выписка: дата", visible=false)
	@Temporal(TemporalType.DATE)
    public Date getExtract_date() { return extract_date; }
    public void setExtract_date(Date extract_date) { this.extract_date = extract_date; }

    @FieldTitle(name="Прикрепленные файлы")
    @OneToMany(targetEntity=IFile.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<IFile> getList_file() { return list_file != null ? list_file : new ArrayList<IFile>(); }
    public void setList_file(List<IFile> list_file) { this.list_file = list_file; }
    
    @FieldTitle(name="Количество листов", visible=false)
	public Integer getSheet_count() { return sheet_count; }
    public void setSheet_count(Integer sheet_count) { this.sheet_count = sheet_count; }
    
    @FieldTitle(name="Год проверки", visible=false)
    @Column(length=4)
    public String getSp_year() { return sp_year; }
    public void setSp_year(String sp_year) { this.sp_year = sp_year; }
    
    @FieldTitle(name="№ пункта запроса", visible=false)
    @Column(length=1000)
    public String getSp_num() { return sp_num; }
    public void setSp_num(String sp_num) { this.sp_num = sp_num; }
    
    @FieldTitle(name="№ подпункта запроса", visible=false)
    @Column(length=1000)
    public String getSp_subnum() { return sp_subnum; }
    public void setSp_subnum(String sp_subnum) { this.sp_subnum = sp_subnum; }
    
    @FieldTitle(name="Номер документа СЭДКП", visible=false)
    @Column(length=20)
    public String getSedkp_num() { return sedkp_num; }
    public void setSedkp_num(String sedkp_num) { this.sedkp_num = sedkp_num; }
    
    @FieldTitle(name="Дата документа СЭДКП", visible=false)
    @Temporal(TemporalType.DATE)
    public Date getSedkp_date() { return sedkp_date; }
    public void setSedkp_date(Date sedkp_date) { this.sedkp_date = sedkp_date; }
    
    @FieldTitle(name="Примечания", visible=false)
    @Column(length=1000)
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    @FieldTitle(name="Список рассылки")
    @OneToMany(targetEntity=IMailing.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<IMailing> getMailing_list() { return mailing_list != null ? mailing_list : new ArrayList<IMailing>(); }
    public void setMailing_list(List<IMailing> mailing_list) { this.mailing_list = mailing_list; }
	
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "";
		if (getDoc_type() != null) name = getDoc_type().getName();
    	if (!hs.isEmpty(getDoc_number())) name += (!name.isEmpty() ? " " : "") + "№ " + getDoc_number();
    	if (getDoc_date() != null) name += (!name.isEmpty() ? " от " : "От ") + new SimpleDateFormat("dd.MM.yyyy").format(getDoc_date());
    	hs.setProperty(this, "name", name);
    }
    
    @Autowired
	protected ObjService objService;
    @Autowired
	protected UserService userService;
    @Autowired
    protected HelperService hs;
    @Resource(name = "getObjectChanged")
    protected ObjectChanged objectChanged;
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Document.class;
		ret.add(new ColumnInfo("create_time", cl));
		ret.add(new ColumnInfo("depart__code", cl));
		ret.add(new ColumnInfo("doc_type", cl)); 
		ret.add(new ColumnInfo("doc_number", cl));
		ret.add(new ColumnInfo("doc_date", cl));
		ret.add(new ColumnInfo("version", cl));
		ret.add(new ColumnInfo("agent__name", cl));
		ret.add(new ColumnInfo("parent_doc__name", cl));
		ret.add(new ColumnInfo("doc_status", cl));
		ret.add(new ColumnInfo("time_status", cl));
		ret.add(new ColumnInfo("create_agent__name", cl));
		ret.add(new ColumnInfo("change_agent__name", cl));
		ret.add(new ColumnInfo("change_time", cl));
		ret.add(new ColumnInfo("act__name", cl));
		ret.add(new ColumnInfo("reestr__name", cl));
		ret.add(new ColumnInfo("act_exclude_num", cl));
		ret.add(new ColumnInfo("act_exclude_reason", cl));
		ret.add(new ColumnInfo("act_exclude_date", cl));
		ret.add(new ColumnInfo("change_doc__name", cl));
		ret.add(new ColumnInfo("buh_date", cl));
		ret.add(new ColumnInfo("sp_year", cl));
		ret.add(new ColumnInfo("sp_num", cl));
		ret.add(new ColumnInfo("sp_subnum", cl));
		ret.add(new ColumnInfo("extract_number", cl));
		ret.add(new ColumnInfo("extract_date", cl));
		ret.add(new ColumnInfo("sheet_count", cl));
		ret.add(new ColumnInfo("sedkp_num", cl));
		ret.add(new ColumnInfo("sedkp_date", cl));
		ret.add(new ColumnInfo("comment", cl));
		return ret;
	}
	@Override
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = super.listButton();
		if (ret == null) ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("confirm", "Завершить подготовку документа", null, "primary"));
		ret.add(new ButtonInfo("copyDoc", "Создать версию документа", null, "primary"));
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
      	if (getDepart() == null) hs.setProperty(this, "depart", hs.getDepartment());
      	hs.setProperty(this, "doc_date", dt);
      	hs.setProperty(this, "doc_status", (SpCommon)objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sd", "1"}));
      	hs.setProperty(this, "time_status", dt);
      	hs.setProperty(this, "sheet_count", 0);
      	String dep_code = hs.getPropertyString(this, "depart__code");
      	if (!hs.isEmpty(dep_code)) {
      		Integer max = (Integer)objRepository.getMaxAttr(Document.class, "number", new String[] {"depart__code"}, new Object[] {dep_code});
      		if (max == null) max = 0;
      		hs.setProperty(this, "number", ++max);
      		hs.setProperty(this, "doc_number", String.format("%s-%04d", dep_code, max));
      	}
      	return null;
    }
	@Override
	public Object onListAddFilter(List<String> listAttr, List<Object> listValue, Map<String, String[]> mapParam) {
 		Object ret = super.onListAddFilter(listAttr, listValue, mapParam);
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
		boolean p_all = false;
		if (mapParam != null) {
			String[] vs = mapParam.get("p_all");
			if (vs != null) {
				for (String v : vs) {
					if ((Boolean)hs.getObjectByString(v, Boolean.class)) {
						p_all = true;
						break;
					}
				}
			}
		}
		if (!p_all) {
			listAttr.add("change_doc__rn");
			listValue.add(null);
		}
		return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		
		try {
			model.addAttribute("listSpDocType", objService.findAll(SpDocType.class));
			model.addAttribute("listSp_sd", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sd"}));
			model.addAttribute("listIDepartment", objService.findAll(IDepartment.class));
			model.addAttribute("listIAgent", objService.findAll(IPerson.class));
			if (!list) {
				model.addAttribute("listSpFileType", objService.findAll(SpFileType.class));
				model.addAttribute("listIDocument", objService.findAll(Document.class));
				model.addAttribute("listIOrganization", objService.findAll(IOrganization.class));
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
    public Object onRemove() {
    	Object ret = super.onRemove();
    	if (ret != null) return ret;
    	Page<?> p = objRepository.findAll(Document.class, null, new String[] {"change_doc__rn"}, new Object[] {getRn()});
    	if (p != null && p.getContent() != null) {
    		if (userService.isAdmin(null)) for (Object o : p.getContent()) hs.setProperty(o, "change_doc", null);
    		else return false;
    	}
    	p = objRepository.findAll(Document.class, null, new String[] {"parent_doc__rn"}, new Object[] {getRn()});
    	if (p != null && p.getContent() != null) {
    		if (userService.isAdmin(null)) for (Object o : p.getContent()) hs.setProperty(o, "parent_doc", null);
    		else return false;
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
		else if ("copyDoc".equals(param)) return true;
		return false;
    }
    @Autowired
	ObjRepositoryCustom objRepository;
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void confirm(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("confirm")) return;
    	hs.setProperty(this, "doc_status", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_sd", "2"}));
	}
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public String copyDoc(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("copyDoc")) return null;
    	Document docChanged = this;
    	while (docChanged.getChange_doc() != null) docChanged = docChanged.getChange_doc();
    	Document doc = new Document();
    	doc.onNew();
    	hs.setProperty(doc, "doc_type", docChanged.getDoc_type());
    	hs.setProperty(doc, "doc_number", docChanged.getDoc_number());
 		hs.setProperty(doc, "number", docChanged.getNumber());
 		hs.setProperty(doc, "doc_date", docChanged.getDoc_date());
 		hs.setProperty(doc, "parent_doc", docChanged.getParent_doc());
 		hs.setProperty(doc, "agent", docChanged.getAgent());
 		hs.setProperty(doc, "depart", docChanged.getDepart());
  		hs.setProperty(doc, "buh_date", docChanged.getBuh_date());
 		hs.setProperty(doc, "extract_number", docChanged.getExtract_number());
 		hs.setProperty(doc, "extract_date", docChanged.getExtract_date());
    	hs.setProperty(doc, "sheet_count", docChanged.getSheet_count());
    	hs.setProperty(doc, "sp_year", docChanged.getSp_year());
    	hs.setProperty(doc, "sp_num", docChanged.getSp_num());
    	hs.setProperty(doc, "sp_subnum", docChanged.getSp_subnum());
    	hs.setProperty(doc, "sedkp_num", docChanged.getSedkp_num());
    	hs.setProperty(doc, "sedkp_date", docChanged.getSedkp_date());
    	Integer version = docChanged.getVersion();
    	if (version == null) version = 1;
    	hs.setProperty(doc, "version", ++version);
    	doc = (Document)objRepository.createObj(doc);
    	if (docChanged.getList_file() != null) {
 			List<IFile> list_file = new ArrayList<IFile>();
 			for (IFile file : docChanged.getList_file()) {
 				IFile fileCopy = new IFile();
 				String filename = file.getFilename(), name = filename;
 				hs.setProperty(fileCopy, "filename", filename);
 				int k = filename.lastIndexOf('.');
 				name = k > 0 ? filename.substring(0, k) : filename;
 				String fileext = file.getFileext();
 				hs.setProperty(fileCopy, "fileext", fileext);
 				hs.setProperty(fileCopy, "filelength", file.getFilelength());
 				hs.setProperty(fileCopy, "name", file.getName());
 				String fileuri = file.getFileuri();
 				File fs = new File(fileuri);
 				if (fs.exists()) {
 					String filesDirectory = (String)objService.getSettings("filesDirectory", "string");
 					if (hs.isEmpty(filesDirectory)) filesDirectory = System.getProperty("user.dir") + File.separator + "FILES";
 					File fd = new File(filesDirectory + new SimpleDateFormat(".yyyy.MM.dd").format(new Date()).replace('.', File.separatorChar));
 					fd.mkdirs();
 					File ff = new File(fd, new SimpleDateFormat("HHmmss").format(new Date()) + "_" + name + (!fileext.isEmpty() ? "." + fileext : ""));
 					hs.copyStream(new FileInputStream(fs), new FileOutputStream(ff), true, true);
 					fileuri = ff.getAbsolutePath();
 				}
 				hs.setProperty(fileCopy, "fileuri", fileuri);
 				hs.setProperty(fileCopy, "filetype", file.getFiletype());
 				hs.setProperty(fileCopy, "comment", file.getComment());
 				hs.setProperty(fileCopy, "parent", doc);
 				fileCopy = (IFile)objRepository.createObj(fileCopy);
 				list_file.add(fileCopy);
 			}
 			hs.setProperty(doc, "list_file", list_file);
 		}
    	objRepository.saveObj(doc);
    	hs.setProperty(this, "change_doc", doc);
    	objRepository.saveObj(this);
    	return "/detailsObj?clazz=Document&rn=" + doc.getRn();
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
    protected int statusCode() {
    	int ret = 1; 
    	try { ret = Integer.valueOf(getDoc_status().getCode()); } catch (Exception ex) { }
    	return ret;
    }
}
