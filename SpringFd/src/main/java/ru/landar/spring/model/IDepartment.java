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
import ru.landar.spring.service.ObjService;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Структурное подразделение", multi="Структурные подразделения")
public class IDepartment extends IBase {
	private String shortname;
	private String fullname;
	private IDepartment prn;
	private Integer level;
	private Date date_from;
	private Date date_to;
	private IOrganization org;
	
	@FieldTitle(name="Номер департамента", nameColumn="№ департамента")
	public String getCode() { return super.getCode(); }
    
	@FieldTitle(name="Короткое наименование")
	@Column(length=256)
    public String getShortname() { return shortname; }
    public void setShortname(String shortname) { this.shortname = shortname; }
    
    @FieldTitle(name="Полное наименование")
    @Column(length=2000)
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; setName(fullname); }
	
    @FieldTitle(name="")
    @ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getPrn() { return prn; }
    public void setPrn(IDepartment prn) { this.prn = prn; }
    
    @FieldTitle(name="Уровень")
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    
    @FieldTitle(name="Действует с:")
    @Temporal(TemporalType.DATE)
    public Date getDate_from() { return date_from; }
    public void setDate_from(Date date_from) { this.date_from = date_from; }
    
    @FieldTitle(name="Действует по:")
    @Temporal(TemporalType.DATE)
    public Date getDate_to() { return date_to; }
    public void setDate_to(Date date_to) { this.date_to = date_to; }
    
    @FieldTitle(name="Организация")
    @ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getOrg() { return org; }
    public void setOrg(IOrganization org) { this.org = org; }
    
	@Autowired
	ObjService objService;

	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = IDepartment.class;
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("shortname", cl));
		ret.add(new ColumnInfo("fullname", cl));
		ret.add(new ColumnInfo("level", cl));
		ret.add(new ColumnInfo("prn__name", cl));
		ret.add(new ColumnInfo("date_from", cl));
		ret.add(new ColumnInfo("date_to", cl));
		ret.add(new ColumnInfo("org__name", cl));
		return ret;
	}
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	hs.setProperty(this, "level", 0);
     	return true;
    }
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = invoke("onAddAttributes", model, list);
		if (ret != null) return ret;
		try { 
			model.addAttribute("listIDepartment", objService.findAll(IDepartment.class)); 
			model.addAttribute("listIOrganization", objService.findAll(IOrganization.class));
		} catch (Exception ex) { }
		return true;
	}
}
