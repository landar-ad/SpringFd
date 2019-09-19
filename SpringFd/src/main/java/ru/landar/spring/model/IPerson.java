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

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.ObjService;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IPerson extends IAgent {
	private String surname;
	private String firstname;
	private String middlename;
	private IDepartment depart;
	private String position;
	private String phone;
	private String email;
	private Date date_fire;
	
	@Column(length=40)
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; updateName(); }
    
    @Column(length=40)
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; updateName(); }
    
    @Column(length=40)
    public String getMiddlename() { return middlename; }
    public void setMiddlename(String middlename) { this.middlename = middlename; updateName(); }
    
    @ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getDepart() { return depart; }
    public void setDepart(IDepartment depart) { this.depart = depart; }
    
    @Column(length=200)
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    @Column(length=40)
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @Column(length=40)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; setCode(email); }
    
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
    public static String singleTitle() { return "Физическое лицо"; }
   	public static String multipleTitle() { return "Физические лица"; }
   	public static List<ColumnInfo> listColumn() {
   		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
   		ret.add(new ColumnInfo("mkod", "Машкод")); 
   		ret.add(new ColumnInfo("surname", "Фамилия"));
   		ret.add(new ColumnInfo("firstname", "Имя"));
   		ret.add(new ColumnInfo("middlename", "Отчество"));
   		ret.add(new ColumnInfo("depart__name", "Структурное подразделение"));
   		ret.add(new ColumnInfo("position", "Должность"));
   		ret.add(new ColumnInfo("phone", "Телефон"));
   		ret.add(new ColumnInfo("email", "Электронная почта"));
   		ret.add(new ColumnInfo("date_fire", "Дата увольнения"));
   		return ret;
   	}
   	public static List<AttributeInfo> listAttribute()
	{
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("mkod", "Машкод", "text", null, false, 4)); 
		ret.add(new AttributeInfo("surname", "Фамилия", "text", null, false, 6));
		ret.add(new AttributeInfo("firstname", "Имя", "text", null, false, 6));
		ret.add(new AttributeInfo("middlename", "Отчество", "text", null, false, 6));
		ret.add(new AttributeInfo("depart", "Структурное подразделение", "select", "listDepartment", false));
		ret.add(new AttributeInfo("position", "Должность", "text", null, false, 6));
		ret.add(new AttributeInfo("phone", "Телефон", "text", null, false, 6));
		ret.add(new AttributeInfo("email", "Электронная почта", "text", null, false, 6));
		ret.add(new AttributeInfo("date_fire", "Дата увольнения", "text", null, false, 4));
		return ret;
	}
   	@Override
   	public Object onNew() {
    	Object ret = super.onNew();
       	if (ret != null) return ret;
       	hs.setProperty(this, "type", (SpAgentType)objService.getObjByCode(SpAgentType.class, "2"));
    	return true;
   	}
   	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listDepartment", objService.findAll(IDepartment.class));
		}
		catch (Exception ex) { }
		return true;
	}
}
