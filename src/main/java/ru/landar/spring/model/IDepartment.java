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
import ru.landar.spring.service.ObjService;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IDepartment extends IBase {
	private String shortname;
	private String fullname;
	private IDepartment prn;
	private Integer level;
	private Date date_from;
	private Date date_to;
	private IOrganization org;
    
	@Column(length=256)
    public String getShortname() { return shortname; }
    public void setShortname(String shortname) { this.shortname = shortname; }
    
    @Column(length=2000)
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; setName(fullname); }
	
    @ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getPrn() { return prn; }
    public void setPrn(IDepartment prn) { this.prn = prn; }
    
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    
    @Temporal(TemporalType.DATE)
    public Date getDate_from() { return date_from; }
    public void setDate_from(Date date_from) { this.date_from = date_from; }
    
    @Temporal(TemporalType.DATE)
    public Date getDate_to() { return date_to; }
    public void setDate_to(Date date_to) { this.date_to = date_to; }
    
    @ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getOrg() { return org; }
    public void setOrg(IOrganization org) { this.org = org; }
    
	@Autowired
	ObjService objService;

	public static String singleTitle() { return "Структурное подразделение"; }
	public static String multipleTitle() { return "Структурные подразделения"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("shortname", "Короткое наименование"));
		ret.add(new ColumnInfo("fullname", "Полное наименование"));
		ret.add(new ColumnInfo("level", "Уровень"));
		ret.add(new ColumnInfo("prn__name", "Вышестоящее подразделение", true, true, "prn_rn", "select", "listDepartment"));
		ret.add(new ColumnInfo("date_from", "Действует с:"));
		ret.add(new ColumnInfo("date_to", "Действует по:"));
		ret.add(new ColumnInfo("org__name", "Организация", true, true, "org_rn", "select", "listOrg"));
		return ret;
	}
	public static List<AttributeInfo> listAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("code", "Код", "text", null, true, 2)); 
		ret.add(new AttributeInfo("shortname", "Короткое наименование", "text", null, true, 6));
		ret.add(new AttributeInfo("fullname", "Полное наименование", "textarea", null, true));
		ret.add(new AttributeInfo("level", "Уровень", "text", null, false, 1));
		ret.add(new AttributeInfo("prn", "Вышестоящее подразделение", "select", "listDepartment", false, 7));
		ret.add(new AttributeInfo("date_from", "Действует с:", "text", null, true, 4));
		ret.add(new AttributeInfo("date_to", "Действует по:", "text", null, false, 4));
		ret.add(new AttributeInfo("org", "Организация", "select", "listOrg", false, 7));
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
			model.addAttribute("listDepartment", objService.findAll(IDepartment.class)); 
			model.addAttribute("listOrg", objService.findAll(IOrganization.class));
		} catch (Exception ex) { }
		return true;
	}
}
