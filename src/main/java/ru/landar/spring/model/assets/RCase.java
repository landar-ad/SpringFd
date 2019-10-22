package ru.landar.spring.model.assets;

import java.math.BigDecimal;
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
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Судебное дело", multi="Судебные дела")
public class RCase extends IBase {
	private IOrganization co_org;
	private SpCommon sd_st;
	private String sd_ist;
	private String sd_otv;
	private String sd_tl;
	private SpCommon sd_tsr;
	private String sd_ns;
	private String sd_nd;
	private Date sd_drs;
	private String sd_psr;
	private BigDecimal sd_ci;
	private String sd_po;
	private Boolean sd_lu;
	private SpCommon sd_sd;
	private SpCommon sd_hrd;
	private Date sd_dsz;
	private SpCommon sd_rr;
	private Date sd_dvzs;
	private SpCommon sd_ro;
	private List<Item_RDocument> sd_docs;
	
	@FieldTitle(name="Принадлежность")
	@ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getCo_org() { return co_org; }
    public void setCo_org(IOrganization co_org) { this.co_org = co_org; }
    
    @FieldTitle(name="Статус подведомственной организации в деле", sp="sp_sdst")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSd_st() { return sd_st; }
    public void setSd_st(SpCommon sd_st) { this.sd_st = sd_st; }
    
    @FieldTitle(name="Истец")
    @Column(length=200)
    public String getSd_ist() { return sd_ist; }
    public void setSd_ist(String sd_ist) { this.sd_ist = sd_ist; }
    
    @FieldTitle(name="Ответчик")
    @Column(length=200)
    public String getSd_otv() { return sd_otv; }
    public void setSd_otv(String sd_otv) { this.sd_otv = sd_otv; }
    
    @FieldTitle(name="Третье лицо")
    @Column(length=200)
    public String getSd_tl() { return sd_tl; }
    public void setSd_tl(String sd_tl) { this.sd_tl = sd_tl; }
    
    @FieldTitle(name="Тип судебного разбирательства", sp="sp_sdtsr")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSd_tsr() { return sd_tsr; }
    public void setSd_tsr(SpCommon sd_tsr) { this.sd_tsr = sd_tsr; }
    
    @FieldTitle(name="Наименование суда")
    @Column(length=200)
    public String getSd_ns() { return sd_ns; }
    public void setSd_ns(String sd_ns) { this.sd_ns = sd_ns; }
    
    @FieldTitle(name="Номер дела")
    @Column(length=30)
    public String getSd_nd() { return sd_nd; }
    public void setSd_nd(String sd_nd) { this.sd_nd = sd_nd; updateName(); }
    
    @FieldTitle(name="Дата регистрации дела в суде")
    @Temporal(TemporalType.DATE)
    public Date getSd_drs() { return sd_drs; }
    public void setSd_drs(Date sd_drs) { this.sd_drs = sd_drs; updateName(); }
    
    @FieldTitle(name="Предмет судебного разбирательства")
    @Column(length=4000)
    public String getSd_psr() { return sd_psr; }
    public void setSd_psr(String sd_psr) { this.sd_psr = sd_psr; }
    
    @FieldTitle(name="Цена иска")
    @Column(precision=17, scale = 2)
    public BigDecimal getSd_ci() { return sd_ci; }
    public void setSd_ci(BigDecimal sd_ci) { this.sd_ci = sd_ci; }
    
    @FieldTitle(name="Позиция организации")
    @Column(length=4000)
    public String getSd_po() { return sd_po; }
    public void setSd_po(String sd_po) { this.sd_po = sd_po; }
    
    @FieldTitle(name="Личное участие")
    public Boolean getSd_lu() { return sd_lu; }
    public void setSd_lu(Boolean sd_lu) { this.sd_lu = sd_lu; }
    
    @FieldTitle(name="Состояние дела", sp="sp_sdsd")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSd_sd() { return sd_sd; }
    public void setSd_sd(SpCommon sd_sd) { this.sd_sd = sd_sd; }
    
    @FieldTitle(name="Ход рассмотрения дела", sp="sp_sdhrd")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSd_hrd() { return sd_hrd; }
    public void setSd_hrd(SpCommon sd_hrd) { this.sd_hrd = sd_hrd; }
    
    @FieldTitle(name="Дата следующего заседания")
    public Date getSd_dsz() { return sd_dsz; }
    public void setSd_dsz(Date sd_dsz) { this.sd_dsz = sd_dsz; }
    
    @FieldTitle(name="Результат рассмотрения", sp="sp_sdrr")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSd_rr() { return sd_rr; }
    public void setSd_rr(SpCommon sd_rr) { this.sd_rr = sd_rr; }
    
    @FieldTitle(name="Дата вступления судебного постановления в законную силу")
    @Temporal(TemporalType.DATE)
    public Date getSd_dvzs() { return sd_dvzs; }
    public void setSd_dvzs(Date sd_dvzs) { this.sd_dvzs = sd_dvzs; }
    
    @FieldTitle(name="Результат для организации", sp="sp_sdro")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSd_ro() { return sd_ro; }
    public void setSd_ro(SpCommon sd_ro) { this.sd_ro = sd_ro; }
    
    @FieldTitle(name="Список документоа")
    @OneToMany(targetEntity=Item_RDocument.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Item_RDocument> getSd_docs() { return sd_docs != null ? sd_docs : new ArrayList<Item_RDocument>(); }
    public void setSd_docs(List<Item_RDocument> sd_docs) { this.sd_docs = sd_docs; }
    
    private void updateName() {
    	if (hs == null) AutowireHelper.autowire(this);
    	String name = "";
    	if (!hs.isEmptyTrim(getSd_nd())) name = getSd_nd();
    	if (getSd_drs() != null) {
    		if (!hs.isEmpty(name)) name += " от "; else name += "От ";
    		name += hs.getPropertyString(this, "sd_drs");
    	}
    	setName(name);
    }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RCase.class;
		ret.add(new ColumnInfo("co_org__name", cl)); 
		ret.add(new ColumnInfo("sd_st", cl));
		ret.add(new ColumnInfo("sd_ist", cl));
		ret.add(new ColumnInfo("sd_otv", cl));
		ret.add(new ColumnInfo("sd_tl", cl));
		ret.add(new ColumnInfo("sd_tsr", cl));
		ret.add(new ColumnInfo("sd_ns", cl));
		ret.add(new ColumnInfo("sd_nd", cl));
		ret.add(new ColumnInfo("sd_drs", cl));
		ret.add(new ColumnInfo("sd_psr", cl));
		ret.add(new ColumnInfo("sd_ci", cl));
		ret.add(new ColumnInfo("sd_po", cl));
		ret.add(new ColumnInfo("sd_lu", cl));
		ret.add(new ColumnInfo("sd_sd", cl));
		ret.add(new ColumnInfo("sd_hrd", cl));
		ret.add(new ColumnInfo("sd_dsz", cl));
		ret.add(new ColumnInfo("sd_rr", cl));
		ret.add(new ColumnInfo("sd_dvzs", cl));
		ret.add(new ColumnInfo("sd_ro", cl));
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
			model.addAttribute("listSp_sdst", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sdst"}));
			model.addAttribute("listSp_sdtsr", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sdtsr"}));
			model.addAttribute("listSp_sdsd", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sdsd"}));
			model.addAttribute("listSp_sdhrd", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sdhrd"}));
			model.addAttribute("listSp_sdrr", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sdrr"}));
			model.addAttribute("listSp_sdro", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sdro"}));
		}
		catch (Exception ex) { }
		return null;
	}
}