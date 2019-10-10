package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Комиссия Министерства", multi="Комиссии Министерства")
public class RCommission extends IBase {
	private String c_name;
	private String c_num;
	private Date c_date;
	private String c_pr_num;
	private Date c_pr_date;
	private SpCommon c_type; 
	private List<RMember> c_sostav;
	private List<RMeeting> c_meeting;
	private List<RDocument> c_docs;
	
	@FieldTitle(name="Наименование комиссии")
    @Column(length=2000)
    public String getC_name() { return c_name; }
    public void setC_name(String c_name) { this.c_name = c_name; }
    
    @FieldTitle(name="Комиссия №")
    @Column(length=30)
    public String getC_num() { return c_num; }
    public void setC_num(String c_num) { this.c_num = c_num; }
    
    @FieldTitle(name="Дата создания комиссии")
    @Temporal(TemporalType.DATE)
    public Date getC_date() { return c_date; }
    public void setC_date(Date c_date) { this.c_date = c_date; }
    
    @FieldTitle(name="Номер приказа")
    @Column(length=30)
    public String getC_pr_num() { return c_pr_num; }
    public void setC_pr_num(String c_pr_num) { this.c_pr_num = c_pr_num; }
    
    @FieldTitle(name="Дата приказа")
    @Temporal(TemporalType.DATE)
    public Date getC_pr_date() { return c_pr_date; }
    public void setC_pr_date(Date c_pr_date) { this.c_pr_date = c_pr_date; }
    
    @FieldTitle(name="Тип комиссии", sp="sp_type_c")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
	public SpCommon getC_type() { return c_type; }
	public void setC_type(SpCommon c_type) { this.c_type = c_type; }
	
	@FieldTitle(name="Состав комиссии")
    @OneToMany(targetEntity=RMember.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<RMember> getC_sostav() { return c_sostav != null ? c_sostav : new ArrayList<RMember>(); }
    public void setC_sostav(List<RMember> c_sostav) { this.c_sostav = c_sostav; }
    
    @FieldTitle(name="Заседания комиссии")
    @OneToMany(targetEntity=RMeeting.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<RMeeting> getC_meeting() { return c_meeting != null ? c_meeting : new ArrayList<RMeeting>(); }
    public void setC_meeting(List<RMeeting> c_meeting) { this.c_meeting = c_meeting; }
    
    @FieldTitle(name="Список документов")
    @OneToMany(targetEntity=RDocument.class, fetch=FetchType.LAZY)
    public List<RDocument> getC_docs() { return c_docs != null ? c_docs : new ArrayList<RDocument>(); }
    public void setC_docs(List<RDocument> c_docs) { this.c_docs = c_docs; }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RCommission.class;
		ret.add(new ColumnInfo("c_name", cl));
		ret.add(new ColumnInfo("c_num", cl));
		ret.add(new ColumnInfo("c_date", cl));
		ret.add(new ColumnInfo("c_pr_num", cl));
		ret.add(new ColumnInfo("c_pr_date", cl));
		ret.add(new ColumnInfo("c_type", cl));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	
    	return null;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listSp_type_c", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_type_c"}));
			model.addAttribute("listSp_cs_rol", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_cs_rol"}));
			model.addAttribute("listIDepartment", objService.findAll(IDepartment.class));
			model.addAttribute("listSp_type_c", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_type_c"}));
			model.addAttribute("listSp_stat_z", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_stat_z"}));
		}
		catch (Exception ex) { }
		return null;
	}
}