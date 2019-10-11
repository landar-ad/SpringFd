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
import ru.landar.spring.model.SpCommon;
@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Заседание комиссии", multi="Заседания комиссии")
public class RMeeting extends IBase {
	private Date cm_dz_p;
	private Date cm_dz_f;
	private String cm_nz;
	private SpCommon c_type;
	private SpCommon cm_stat_z;
	private List<RClaim> list_rcl;
	private List<RDocument> list_doc;
	private List<RMeeting_RMember> list_cs;
	
	@FieldTitle(name="Дата заседания планируемая")
    @Temporal(TemporalType.DATE)
    public Date getCm_dz_p() { return cm_dz_p; }
    public void setCm_dz_p(Date cm_dz_p) { this.cm_dz_p = cm_dz_p; }
    
    @FieldTitle(name="Дата заседания фактическая")
    @Temporal(TemporalType.DATE)
    public Date getCm_dz_f() { return cm_dz_f; }
    public void setCm_dz_f(Date cm_dz_f) { this.cm_dz_f = cm_dz_f; }
    
    @FieldTitle(name="Номер заседания")
    @Column(length=30)
    public String getCm_nz() { return cm_nz; }
    public void setCm_nz(String cm_nz) { this.cm_nz = cm_nz; }
    
    @FieldTitle(name="Тип комиссии", sp="sp_type_c")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
	public SpCommon getC_type() { return c_type; }
	public void setC_type(SpCommon c_type) { this.c_type = c_type; }
	
	@FieldTitle(name="Статус заседания", sp="sp_stat_z")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
	public SpCommon getCm_stat_z() { return cm_stat_z; }
	public void setCm_stat_z(SpCommon cm_stat_z) { this.cm_stat_z = cm_stat_z; }
	
	@FieldTitle(name="Заявки к рассмотрению")
    @OneToMany(targetEntity=RClaim.class, fetch=FetchType.LAZY)
    public List<RClaim> getList_rcl() { return list_rcl != null ? list_rcl : new ArrayList<RClaim>(); }
    public void setList_rcl(List<RClaim> list_rcl) { this.list_rcl = list_rcl; }
    
    @FieldTitle(name="Документы заседания")
    @OneToMany(targetEntity=RDocument.class, fetch=FetchType.LAZY)
    public List<RDocument> getList_doc() { return list_doc != null ? list_doc : new ArrayList<RDocument>(); }
    public void setList_doc(List<RDocument> list_doc) { this.list_doc = list_doc; }
    
    @FieldTitle(name="Члены комиссии, участвующие в заседании")
    @OneToMany(targetEntity=RMeeting_RMember.class, cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
    public List<RMeeting_RMember> getList_cs() { return list_cs != null ? list_cs : new ArrayList<RMeeting_RMember>(); }
    public void setList_cs(List<RMeeting_RMember> list_cs) { this.list_cs = list_cs; }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RMeeting.class;
		ret.add(new ColumnInfo("cm_dz_p", cl));
		ret.add(new ColumnInfo("cm_dz_f", cl));
		ret.add(new ColumnInfo("cm_nz", cl));
		ret.add(new ColumnInfo("c_type", cl));
		ret.add(new ColumnInfo("cm_stat_z", cl));
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
			model.addAttribute("listSp_stat_z", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_stat_z"}));
			model.addAttribute("listSp_rezg", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_resg"}));
		}
		catch (Exception ex) { }
		return null;
	}
}