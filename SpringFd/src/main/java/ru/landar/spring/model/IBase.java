package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
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
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@Entity
@EntityListeners(IBaseListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class IBase {
	private Integer rn; 
	private String clazz;
	private String name;
	private String code;
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
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getRn() { return rn; }
    public void setRn(Integer rn) { this.rn = rn; }
    
    @Column(length=32)
    public String getClazz() { return clazz; }
    public void setClazz(String clazz) { this.clazz = clazz; }
    
    @Column(length=2048)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    @Column(length=256)
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    @Column(length=128)
    @CreatedBy
    public String getCreator() { return creator; }
    public void setCreator(String creator) { this.creator = creator; }
    
    @CreatedDate
    public Date getCdate() { return cdate; }
    public void setCdate(Date cdate) { this.cdate = cdate; }
    
    @Column(length=128)
    @LastModifiedBy
    public String getModifier() { return modifier; }
    public void setModifier(String modifier) { this.modifier = modifier; }
    
    @LastModifiedDate
    public Date getMdate() { return mdate; }
    public void setMdate(Date mdate) { this.mdate = mdate; }
    
    @ManyToOne(targetEntity=IBase.class)
    public IBase getParent() { return parent; }
    public void setParent(IBase parent) { this.parent = parent; }

    // *********************** Обработчики *************************
    @Autowired
    UserService userService;
    @Autowired
    ObjService objService;
    @Autowired
    HelperService hs;
    public Object onNew() { 
    	Object ret = invoke("onNew");
    	String principal = userService.getPrincipal();
		setCreator(principal);
		setModifier(principal);
    	return ret;
    }
    public Object onUpdate(Map<String, Object> map, Map<String, Object[]> mapChanged) throws Exception { 
    	return invoke("onUpdate", map, mapChanged);
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
    public Object onRedirectAfterUpdate() { 
    	Object ret = invoke("onRedirectAfterUpdate");
    	if (ret != null) return ret;
    	if (getParent() != null) return "/detailsObj?clazz=" + getParent().getClazz() + "&rn=" + getParent().getRn();
    	return "/listObj?clazz=" + getClazz() + (getRn() != null ? "&rn=" + getRn() : "");
    }
    public Object onCheckRights(Operation op) { 
     	Object ret = invoke("onCheckRights", op);
    	if (ret == null) ret = getRn() == null ? true : (op == Operation.update || op == Operation.delete) && userService.isAdmin(null);
    	return ret;
    }
    public Object onCheckUpdateAttribute(String attr) { 
     	Object ret = invoke("onCheckUpdateAttribute", attr);
    	if (ret != null) return ret;
    	if (hs.getAttrType(getClass(), attr) == null) return true;
    	if ("clazz".equals(attr) || "rn".equals(attr)) return false;
      	return ret;
    }
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
    	if (getRn() == null) return false;
		if ("edit".equals(param)) return onCheckRights(Operation.update);
		else if ("remove".equals(param)) return onCheckRights(Operation.delete);
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		return userService.isAdmin(null);
    }
    public Object onBuildContent() { 
    	return invoke("onBuildContent"); 
    }
    public Object onListAddFilter(List<String> listAttr, List<Object> listValue) { 
    	return invoke("onListAddFilter", listAttr, listValue); 
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
	public Object onListPaginated() {
		Object ret = invoke("onListPaginated");
		if (ret != null) return ret;
		return hs.invoke(getClass(), "listPaginated");
	}
	public Object onSingleTitle() {
		Object ret = invoke("onSingleTitle");
		if (ret != null) return ret;
		return hs.invoke(getClass(), "singleTitle");
	}
	public Object onMultipleTitle() {
		Object ret = invoke("onMultipleTitle");
		if (ret != null) return ret;
		return hs.invoke(getClass(), "multipleTitle");
	}
	public Object onListAttribute() {
		Object ret = invoke("onListAttribute");
		if (ret != null) return ret;
		return hs.invoke(getClass(), "listAttribute");
	}
	// Статические методы
	public static String singleTitle() { return "Базовый объект"; }
	public static String multipleTitle() { return "Базовые объекты"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("name", "Наименование"));
		return ret;
	}
	public List<ButtonInfo> listButton() {
		return null;
	}
	public static boolean listPaginated() { return false; }
	public static List<AttributeInfo> listAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("code", "Код", "text", null, false));
		ret.add(new AttributeInfo("name", "Наименование", "text", null, false)); 
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
