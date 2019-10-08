package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.ObjService;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Организация", multi="Организации")
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
	
	@FieldTitle(name="Короткое наименование")
    @Column(length=256)
    public String getShortname() { return shortname; }
    public void setShortname(String shortname) { this.shortname = shortname; }
    
    @FieldTitle(name="Полное наименование")
    @Column(length=2000)
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; updateName(); }
    
    @FieldTitle(name="ИНН")
    @Column(length=12)
    public String getInn() { return inn; }
    public void setInn(String inn) { this.inn = inn; updateCode(); }
    
    @FieldTitle(name="КПП")
    @Column(length=9)
    public String getKpp() { return kpp; }
    public void setKpp(String kpp) { this.kpp = kpp; updateCode(); }
    
    @FieldTitle(name="Адрес")
    @Column(length=2000)
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    @FieldTitle(name="Телефон")
    @Column(length=40)
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @FieldTitle(name="Электронная почта")
    @Column(length=40)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @FieldTitle(name="Код сводного реестра")
    @Column(length=8)
    public String getSrcode() { return srcode; }
    public void setSrcode(String srcode) { this.srcode = srcode; }
    
    @FieldTitle(name="ОГРН")
    @Column(length=15)
    public String getOgrn() { return ogrn; }
    public void setOgrn(String ogrn) { this.ogrn = ogrn; }
    
    @FieldTitle(name="ОКПО")
    @Column(length=8)
    public String getOkpo() { return okpo; }
    public void setOkpo(String ookpo) { this.okpo = ookpo; }
    
    @FieldTitle(name="ОКОПФ")
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
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = IOrganization.class;
		ret.add(new ColumnInfo("mkod", cl)); 
		ret.add(new ColumnInfo("shortname", cl));
		ret.add(new ColumnInfo("fullname", cl));
		ret.add(new ColumnInfo("inn", cl));
		ret.add(new ColumnInfo("kpp", cl));
		ret.add(new ColumnInfo("address", cl));
		ret.add(new ColumnInfo("phone", cl));
		ret.add(new ColumnInfo("email", cl));
		ret.add(new ColumnInfo("srcode", cl));
		ret.add(new ColumnInfo("ogrn", cl));
		ret.add(new ColumnInfo("okpo", cl));
		ret.add(new ColumnInfo("okopf", cl));
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
