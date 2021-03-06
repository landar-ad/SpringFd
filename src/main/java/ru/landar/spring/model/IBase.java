package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.ui.Model;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.HelperServiceImpl;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@ObjectTitle(single="Базовый объект", multi="Базовые объекты")
@Entity
@EntityListeners(IBaseListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class IBase {
	private Integer rn; 
	private String clazz;
	private String name;
	private String code;
	private Boolean actual;
	private Integer version;
	private String creator;
	private Date cdate;
	private String modifier;
	private Date mdate;
	private IBase parent;
	public IBase() {
		super();
		setClazz(getClass().getSimpleName());
		Date d = new Date();
		setCdate(d);
		setMdate(d);
		setVersion(1);
		setActual(true);
	}
	
	@Id
	@FieldTitle(name="Идентификатор")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getRn() { return rn; }
    public void setRn(Integer rn) { this.rn = rn; }
    
    @FieldTitle(name="Класс")
    @Column(length=32)
    public String getClazz() { return clazz; }
    public void setClazz(String clazz) { this.clazz = clazz; }
    
    @FieldTitle(name="Наименование")
    @Column(length=2048)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @FieldTitle(name="Код")
    @Column(length=256)
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    @FieldTitle(name="Актуальность")
    public Boolean getActual() { return actual; }
    public void setActual(Boolean actual) { this.actual = actual; }
    
    @FieldTitle(name="Версия")
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    @FieldTitle(name="Создан")
    @Column(length=128)
    @CreatedBy
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    
    @FieldTitle(name="Время создания")
    @CreatedDate
    public Date getCdate() { return cdate; }
    public void setCdate(Date cdate) { this.cdate = cdate; }
    
    @FieldTitle(name="Изменен")
    @Column(length=128)
    @LastModifiedBy
    public String getModifier() { return modifier; }
    public void setModifier(String modifier) { this.modifier = modifier; }
    
    @FieldTitle(name="Время изменения")
    @LastModifiedDate
    public Date getMdate() { return mdate; }
    public void setMdate(Date mdate) { this.mdate = mdate; }
    
    @FieldTitle(name="Родительский объект")
    @ManyToOne(targetEntity=IBase.class, fetch=FetchType.LAZY)
    public IBase getParent() { return parent; }
    public void setParent(IBase parent) { this.parent = parent; }
    
    @Transient
    public IBase getParentProxy() {
    	if (hs == null) AutowireHelper.autowire(this);
    	if (parent != null && parent.getClass().getSimpleName().indexOf("Proxy") >= 0) {
    		Integer rn = (Integer)hs.getProperty(parent, "rn");
			if (rn != null) {
				String clazz = objService.getClassByKey(rn);
				parent = (IBase)objService.find(hs.getClassByName(clazz), rn);
			}
    	}
    	return getParent();
    }
    @Transient
    public IBase getTop() {
    	if (hs == null) AutowireHelper.autowire(this);
    	IBase p = this, ret = null;
    	while ((p = p.getParentProxy()) != null) ret = p;
    	return ret;
    }
    @FieldTitle(name="Базовый класс")
    @Transient
    public String getBaseClazz() { return clazz; }

    // *********************** Обработчики *************************
    @Autowired
    protected UserService userService;
    @Autowired
    protected ObjService objService;
    @Autowired
	protected HelperService hs;
    @Autowired
	protected ObjRepositoryCustom objRepository;

    public Object onNew() { 
    	Object ret = invoke("onNew");
    	String principal = userService.getPrincipal();
		setCreator(principal);
		setModifier(principal);
    	return ret;
    }
    public Object onUpdate() throws Exception { 
    	return invoke("onUpdate");
    }
    public Object onRemove() { 
    	return invoke("onRemove");
	}
    public Object onExecute(String param, HttpServletRequest request) { 
    	Object ret = invoke("onExecute", param, request);
    	if (ret != null) return ret;
    	ret = hs.invoke(this, param, request);
    	return ret;
	}
    public Object onRedirectAfterUpdate(HttpServletRequest request) { 
    	Object ret = invoke("onRedirectAfterUpdate", request);
    	if (ret != null) return ret;
    	IBase p = getParentProxy();
    	if (p != null) return "/detailsObj?clazz=" + p.getClazz() + "&rn=" + p.getRn();
    	return "/listObj?clazz=" + getBaseClazz() + "&p_ret=1" + (getRn() != null ? "&rn=" + getRn() : "");
    }
    public Object onCheckRights(Operation op) { 
     	Object ret = invoke("onCheckRights", op);
     	if (ret != null) return ret;
    	if (getRn() == null) return true;
    	if (op == Operation.load) return true;
    	if (op == Operation.update || op == Operation.delete || op == Operation.create) 
    		if (userService.isAdmin(null)) return true;
    	return ret;
    }
    public Object onCheckUpdateAttribute(String attr) { 
     	Object ret = invoke("onCheckUpdateAttribute", attr);
    	if (ret != null) return ret;
    	if (hs.getAttrType(getClass(), attr) == null) return true;
    	Object o = onCheckRights(Operation.update);
    	if (o != null && o instanceof Boolean && !(Boolean)o) return false;
    	if ("clazz".equals(attr) || "rn".equals(attr)) return false;
      	return ret;
    }
    public Object onCheckListAttribute(String attr, Operation op) { 
     	Object ret = invoke("onCheckListAttribute", attr, op);
    	if (ret != null) return ret;
    	if (hs.getAttrType(getClass(), attr) == null) return true;
    	Object o = onCheckRights(Operation.update);
    	if (o != null && o instanceof Boolean && !(Boolean)o) return false;
    	if ("clazz".equals(attr) || "rn".equals(attr)) return false;
    	o = onCheckUpdateAttribute(attr);
    	if (o != null && o instanceof Boolean && !(Boolean)o) return false;
      	return ret;
    }
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
     	if ("add".equals(param)) return onCheckRights(Operation.create);
     	if (getRn() == null) return false;
		if ("edit".equals(param)) return onCheckRights(Operation.update);
		else if ("remove".equals(param)) return onCheckRights(Operation.delete);
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		return ret;
    }
    public Object onBuildContent() { 
    	return invoke("onBuildContent"); 
    }
    public Object onListAddFilter(List<String> listAttr, List<Object> listValue, Map<String, String[]> mapParam) { 
    	return invoke("onListAddFilter", listAttr, listValue, mapParam); 
    }
	public Object onAddAttributes(Model model, boolean list) {
		return invoke("onAddAttributes", model, list); 
	}
	public Object onListColumn() {
		Object ret = invoke("onListColumn");
		if (ret != null) return ret;
		return hs.invoke(getClass(), "listColumn");
	}
	public Object onListButton() {
		Object ret = invoke("onListButton");
		if (ret != null) return ret;
		return hs.invoke(this, "listButton");
	}
	public Object onDetailsButton() {
		Object ret = invoke("onDetailsButton");
		if (ret != null) return ret;
		return hs.invoke(this, "detailsButton");
	}
	public Object onListPaginated() {
		Object ret = invoke("onListPaginated");
		if (ret != null) return ret;
		return hs.invoke(getClass(), "listPaginated");
	}
	public Object onSingleTitle() {
		Object ret = invoke("onSingleTitle");
		if (ret != null) return ret;
		return HelperServiceImpl.getAttrInfo(getClass(), null, "single");
	}
	public Object onMultipleTitle() {
		Object ret = invoke("onMultipleTitle");
		if (ret != null) return ret;
		return HelperServiceImpl.getAttrInfo(getClass(), null, "multi");
	}
	public Object onMenuTitle() {
		Object ret = invoke("onMenuTitle");
		if (ret != null) return ret;
		return HelperServiceImpl.getAttrInfo(getClass(), null, "menu");
	}
	public Object onListAttribute() {
		Object ret = invoke("onListAttribute");
		if (ret != null) return ret;
		return hs.invoke(getClass(), "listAttribute");
	}
	// Статические методы
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = IBase.class;
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("name", cl));
		ret.add(new ColumnInfo("actual", cl));
		return ret;
	}
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("edit", "Редактировать", "edit"));
		ret.add(new ButtonInfo("view", "Просмотреть", "readme"));
		ret.add(new ButtonInfo("add", "Добавить", "plus-circle"));
		ret.add(new ButtonInfo("remove", "Удалить", "trash"));
		return ret;
	}
	public List<ButtonInfo> detailsButton() {
		return null;
	}
	public static boolean listPaginated() { return false; }
	public static List<AttributeInfo> listAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		Class<?> cl = IBase.class;
		ret.add(new AttributeInfo("code", cl));
		ret.add(new AttributeInfo("name", cl));
		ret.add(new AttributeInfo("actual", cl, "check", null, false, false, 2, null));
		return ret;
	}
	/**
	 * Функция вызова внешнего обработчика объекта
	 * @param method
	 * @param args
	 * @return
	 */
	public Object invoke(String method, Object ... args) {
		AutowireHelper.autowire(this);
		Object[] p = new Object[2 + (args != null ? args.length : 0)];
		p[0] = this;
		p[1] = hs;
		if (args != null) for (int i=0; i<args.length; i++) p[2 + i] = args[i];
		Object ret = null;
    	Class<?> cl = hs.getHandlerClass(getClazz() + "_listeners");
    	if (cl != null) 
    	{
    		Object o = null;
    		try { o = cl.newInstance(); } catch (Exception ex) { }
    		ret = o == null ? hs.invoke(cl, method, p) : hs.invoke(cl, method, p); 
    	}
    	return ret;
	}
}
