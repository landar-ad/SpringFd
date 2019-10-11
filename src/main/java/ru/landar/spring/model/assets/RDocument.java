package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SpFileType;
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
@ObjectTitle(single="Документ управления имуществом", multi="Документы управления имуществом")
public class RDocument extends IBase {
	
	private SpRDocType doctype;
	private String docname;
	private String docnum;
	private Date docdate;
	private Date docdate_end;
	private String prim;
	private Boolean apsend;
	private String prim_apsend;
	private List<IFile> list_file;
	
	@FieldTitle(name="Тип документа")
	@ManyToOne(targetEntity=SpRDocType.class, fetch=FetchType.LAZY)
    public SpRDocType getDoctype() { return doctype; }
    public void setDoctype(SpRDocType doctype) { this.doctype = doctype; }
    
    @FieldTitle(name="Наименование документа")
    @Column(length=1024)
    public String getDocname() { return docname; }
    public void setDocname(String docname) { this.docname = docname; updateName(); }
    
    @FieldTitle(name="Номер документа")
    @Column(length=50)
    public String getDocnum() { return docnum; }
    public void setDocnum(String docnum) { this.docnum = docnum; updateName(); }
    
    @FieldTitle(name="Дата")
    @Temporal(TemporalType.DATE)
    public Date getDocdate() { return docdate; }
    public void setDocdate(Date docdate) { this.docdate = docdate; updateName(); }
    
    @FieldTitle(name="Действителен до")
    @Temporal(TemporalType.DATE)
    public Date getDocdate_end() { return docdate_end; }
    public void setDocdate_end(Date docdate_end) { this.docdate_end = docdate_end; }
    
    @FieldTitle(name="Примечания")
    @Column(length=2048)
    public String getPrim() { return prim; }
    public void setPrim(String prim) { this.prim = prim; }
    
    @FieldTitle(name="Документ отсутствует")
    public Boolean getApsend() { return apsend; }
    public void setApsend(Boolean apsend) { this.apsend = apsend; }
    
    @FieldTitle(name="Комментарий к отсутствию документа")
    @Column(length=2048)
    public String getPrim_apsend() { return prim_apsend; }
    public void setPrim_apsend(String prim_apsend) { this.prim_apsend = prim_apsend; }
    
    @FieldTitle(name="Прикрепленные файлы")
    @ManyToMany(targetEntity=IFile.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<IFile> getList_file() { return list_file != null ? list_file : new ArrayList<IFile>(); }
    public void setList_file(List<IFile> list_file) { this.list_file = list_file; }
    
    private void updateName() {
    	if (hs == null) AutowireHelper.autowire(this);
    	String name = "";
    	if (getDocname() != null) name = getDocname();
    	if (getDocnum() != null && !getDocnum().isEmpty()) {
    		if (!name.isEmpty()) name += " ";
    		name += "№ ";
    		name += getDocnum();
    	}
    	if (getDocdate() != null) {
    		if (!name.isEmpty()) name += " от ";
    		else name += "От ";
    		name += new SimpleDateFormat("dd.MM.yyyy").format(getDocdate());
		}
    	setName(name);
    }
    
    @Autowired
	ObjService objService;
    @Autowired
	UserService userService;
    @Autowired
	HelperService hs;
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RDocument.class;
		ret.add(new ColumnInfo("doctype", cl)); 
		ret.add(new ColumnInfo("docname", cl));
		ret.add(new ColumnInfo("docnum", cl));
		ret.add(new ColumnInfo("docdate", cl));
		ret.add(new ColumnInfo("docdate_end", cl));
		ret.add(new ColumnInfo("apsend", cl));
		ret.add(new ColumnInfo("prim_apsend", cl));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	
	@Override
	public Object onListAddFilter(List<String> listAttr, List<Object> listValue, Map<String, String[]> mapParam) {
 		Object ret = super.onListAddFilter(listAttr, listValue, mapParam);
		if (ret != null) return ret;
		
		IUser user = userService.getUser((String)null);
		if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
		String roles = user.getRoles();
		if (roles.indexOf("ADMIN") < 0) {
			IOrganization org = user.getOrg();
			if (org == null) throw new SecurityException("У пользователя " + user.getLogin() + " не задана организация");
			listAttr.add("parent__customer__rn");
			listValue.add(org.getRn());
		}
		return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		
		try {
			model.addAttribute("lisSpRDocType", objService.findAll(SpRDocType.class));
			if (!list) model.addAttribute("listSpFileType", objService.findAll(SpFileType.class));
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
			if (org == null) throw new SecurityException("У пользователя " + user.getLogin() + " не указана организация");
			UnfinishedConstruction uc = (UnfinishedConstruction)getParent();
			if (uc != null && uc.getCustomer() != null && org.getRn().compareTo(uc.getCustomer().getRn()) == 0) return true;
			return false;
    	}
    	return true;
    }
    @Override
    public Object onUpdate() throws Exception { 
    	Object ret = super.onUpdate();
    	if (ret != null) return ret;
		
		return true;
    }
    @Override
    public Object onRedirectAfterUpdate(HttpServletRequest request) { 
    	Object ret = invoke("onRedirectAfterUpdate", request);
    	if (ret != null) return ret;
    	return getParent() != null ? "/detailsObj?clazz=UnfinishedConstruction" + "&rn=" + getParent().getRn() + "&p_tab=5" : super.onRedirectAfterUpdate(request);
    }
}
