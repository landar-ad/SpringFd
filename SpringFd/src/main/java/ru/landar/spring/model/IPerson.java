package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.ObjService;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Физическое лицо", multi="Физические лица", menu="Персоналии")
public class IPerson extends IAgent {
	private String surname;
	private String firstname;
	private String middlename;
	private IDepartment depart;
	private String position;
	private String phone;
	private String email;
	private Date date_fire;
	
	@FieldTitle(name="Фамилия")
	@Column(length=40)
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; updateName(); }
    
    @FieldTitle(name="Имя")
    @Column(length=40)
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; updateName(); }
    
    @FieldTitle(name="Отчество")
    @Column(length=40)
    public String getMiddlename() { return middlename; }
    public void setMiddlename(String middlename) { this.middlename = middlename; updateName(); }
    
    @FieldTitle(name="Структурное подразделение")
    @ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getDepart() { return depart; }
    public void setDepart(IDepartment depart) { this.depart = depart; }
    
    @FieldTitle(name="Должность")
    @Column(length=200)
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    @FieldTitle(name="Телефон")
    @Column(length=40)
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @FieldTitle(name="Электронная почта")
    @Column(length=40)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; setCode(email); }
    
    @FieldTitle(name="Дата увольнения")
    @Temporal(TemporalType.DATE)
    public Date getDate_fire() { return date_fire; }
    public void setDate_fire(Date date_fire) { this.date_fire = date_fire; }
    
    private void updateName() {
    	AutowireHelper.autowire(this);
    	String name = "";
    	if (surname != null) name = surname;
    	if (firstname != null && !firstname.isEmpty()) {
    		if (!name.isEmpty()) name += " ";
    		name += firstname.substring(0, 1) + ".";
    	}
    	if (middlename != null && !middlename.isEmpty()) {
    		name += middlename.substring(0, 1) + ".";
    	}	
    	hs.setProperty(this, "name", name);
    }
    
    @Autowired
   	ObjService objService;
    
   	public static List<ColumnInfo> listColumn() {
   		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
   		Class<?> cl = IPerson.class;
   		ret.add(new ColumnInfo("mkod", cl)); 
   		ret.add(new ColumnInfo("surname", cl));
   		ret.add(new ColumnInfo("firstname", cl));
   		ret.add(new ColumnInfo("middlename", cl));
   		ret.add(new ColumnInfo("depart__name", cl));
   		ret.add(new ColumnInfo("position", cl));
   		ret.add(new ColumnInfo("phone", cl));
   		ret.add(new ColumnInfo("email", cl));
   		ret.add(new ColumnInfo("date_fire", cl));
   		return ret;
   	}
   	@Override
   	public Object onNew() {
    	Object ret = super.onNew();
       	if (ret != null) return ret;
       	hs.setProperty(this, "type", (SpCommon)objRepository.find(SpCommon.class, new String[] {"code", "sp_code"}, new Object[] {"2", "sp_typa"}));
    	return true;
   	}
   	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listIDepartment", objService.findAll(IDepartment.class));
		}
		catch (Exception ex) { }
		return true;
	}
}
