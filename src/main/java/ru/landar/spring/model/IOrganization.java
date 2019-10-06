package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.ObjService;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IOrganization extends IAgent {
	private String shortname; 
	private String fullname; 
	private String inn; 
	private String kpp;
	private String address;
	private String phone;
	private String email;
	private String srcode; 
	private String ogrn;
	private String okpo;
	private String okopf;
	
    @Column(length=256)
    public String getShortname() { return shortname; }
    public void setShortname(String shortname) { this.shortname = shortname; }
    
    @Column(length=2000)
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; updateName(); }
    
    @Column(length=12)
    public String getInn() { return inn; }
    public void setInn(String inn) { this.inn = inn; updateCode(); }
    
    @Column(length=9)
    public String getKpp() { return kpp; }
    public void setKpp(String kpp) { this.kpp = kpp; updateCode(); }
    
    @Column(length=2000)
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    @Column(length=40)
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @Column(length=40)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Column(length=8)
    public String getSrcode() { return srcode; }
    public void setSrcode(String srcode) { this.srcode = srcode; }
    
    @Column(length=15)
    public String getOgrn() { return ogrn; }
    public void setOgrn(String ogrn) { this.ogrn = ogrn; }
    
    @Column(length=8)
    public String getOkpo() { return okpo; }
    public void setOkpo(String ookpo) { this.okpo = ookpo; }
    
    @Column(length=5)
    public String getOkopf() { return okopf; }
    public void setOkopf(String okopf) { this.okopf = okopf; }
    
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "";
    	if (shortname != null) name = shortname;
    	if (name.isEmpty() && !fullname.isEmpty()) name = fullname;
    	hs.setProperty(this, "name", name);
    }
    private void updateCode() {
     	String code = "";
    	if (inn != null && !inn.isEmpty()) code = inn;
    	if (kpp != null && !kpp.isEmpty()) { if (!code.isEmpty()) code += "_"; code += kpp; }
    	if (!code.isEmpty()) hs.setProperty(this, "code", code);
    }
    @Autowired
	ObjService objService;
    public static String singleTitle() { return "Организация"; }
	public static String multipleTitle() { return "Организации"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("mkod", "Машкод")); 
		ret.add(new ColumnInfo("shortname", "Короткое наименование"));
		ret.add(new ColumnInfo("fullname", "Полное наименование"));
		ret.add(new ColumnInfo("inn", "ИНН"));
		ret.add(new ColumnInfo("kpp", "КПП"));
		ret.add(new ColumnInfo("address", "Адрес"));
		ret.add(new ColumnInfo("srcode", "Код сводного реестра"));
		ret.add(new ColumnInfo("ogrn", "ОГРН"));
		ret.add(new ColumnInfo("okpo", "ОКПО"));
		ret.add(new ColumnInfo("okopf", "ОКОПФ"));
		return ret;
	}
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	hs.setProperty(this, "type", (SpCommon)objRepository.find(SpCommon.class, new String[] {"code", "sp_code"}, new Object[] {"1", "sp_typa"}));
     	return true;
    }
}
