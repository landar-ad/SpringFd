package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RDocument extends IBase {
	
	private SpDocType doctype;
	private String docname;
	private String docnum;
	private Date docdate;
	private Date docdate_end;
	private IFile file;
	private String prim;
	private Boolean apsend;
	private String prim_apsend;
	
	@ManyToOne(targetEntity=SpDocType.class, fetch=FetchType.LAZY)
    public SpDocType getDoctype() { return doctype; }
    public void setDoctype(SpDocType doctype) { this.doctype = doctype; }
    
    @Column(length=1024)
    public String getDocname() { return docname; }
    public void setDocname(String docname) { this.docname = docname; updateName(); }
    
    @Column(length=50)
    public String getDocnum() { return docnum; }
    public void setDocnum(String docnum) { this.docnum = docnum; updateName(); }
    
    @Temporal(TemporalType.DATE)
    public Date getDocdate() { return docdate; }
    public void setDocdate(Date docdate) { this.docdate = docdate; updateName(); }
    
    @Temporal(TemporalType.DATE)
    public Date getDocdate_end() { return docdate_end; }
    public void setDocdate_end(Date docdate_end) { this.docdate_end = docdate_end; }
    
    @Column(length=2048)
    public String getPrim() { return prim; }
    public void setPrim(String prim) { this.prim = prim; }
    
    @ManyToOne(targetEntity=IFile.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public IFile getFile() { return file; }
    public void setFile(IFile file) { this.file = file; }
    
    public Boolean getApsend() { return apsend; }
    public void setApsend(Boolean apsend) { this.apsend = apsend; }
    
    @Column(length=2048)
    public String getPrim_apsend() { return prim_apsend; }
    public void setPrim_apsend(String prim_apsend) { this.prim_apsend = prim_apsend; }
    
    private void updateName()
    {
    	String name = "";
    	if (getDocname() != null) name = getDocname();
    	if (getDocnum() != null && !getDocnum().isEmpty()) 
    	{
    		if (!name.isEmpty()) name += " ";
    		name += "№ ";
    		name += getDocnum();
    	}
    	if (getDocdate() != null)
		{
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
    
	public static String singleTitle() { return "Документ"; }
	public static String multipleTitle() { return "Документы"; }
	public static List<ColumnInfo> listColumn() {
		
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("doctype__name", "Тип документа", true, true, "doctype__rn", "select", "listDocType")); 
		ret.add(new ColumnInfo("docname", "Наименование документа"));
		ret.add(new ColumnInfo("docnum", "Номер документа"));
		ret.add(new ColumnInfo("docdate", "Дата"));
		ret.add(new ColumnInfo("docdate_end", "Действителен до"));
		ret.add(new ColumnInfo("file__name", "Прикрепленный файл"));
		ret.add(new ColumnInfo("apsend", "Документ отсутствует"));
		ret.add(new ColumnInfo("prim_apsend", "Комментарий к отсутствию документа"));
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
			model.addAttribute("listDocType", objService.findAll(SpDocType.class));
			if (!list) model.addAttribute("listFileType", objService.findAll(SpFileType.class));
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
