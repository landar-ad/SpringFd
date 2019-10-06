package ru.landar.spring.model.assets;

import java.math.BigDecimal;
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

import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RLand extends RProperty {
	private String сad_num;
	private Date cad_date;
	private BigDecimal co_area;
	private String fp_reg_num;
	private Date fp_in_date;
	private Date fp_out_date;
	private String co_address;
	private String co_address_code;
	private BigDecimal dist_ns;
	private SpCommon co_category;
	private String co_usage;
	private SpCommon co_feature;
	private Boolean ki_gkh;
	private Boolean ki_az;
	private Boolean ki_omr;
	private Boolean ki_ppz;
	private Boolean ki_inoe;
	private Boolean ki_neisp;
	private Boolean pr_formed;
	private Date pr_prognoz_date;
	private SpCommon pr_r_type;
	private String pr_owner;
	private SpCommon pr_term;
	private SpCommon pr_o_type;
	private Date pr_reg_rf_plan_date;
	private Date pr_reg_rf_fact_date;
	private Date pr_reg_other_plan_date;
	private Date pr_reg_other_fact_date;
	private SpCommon square_suf;
	private BigDecimal area_suf;
	private Boolean inv_attr;
	private String eff_use_fut;
	private String co_chrs;
	private BigDecimal market_value;
	private BigDecimal cadastre_value;
	private BigDecimal standard_cost;
	private BigDecimal co_rent;
	
	@Column(length=18)
    public String getCad_num() { return сad_num; }
    public void setCad_num(String сad_num) { this.сad_num = сad_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getCad_date() { return cad_date; }
    public void setCad_date(Date cad_date) { this.cad_date = cad_date; }
    
    @Column(precision=17, scale=3)
    public BigDecimal getCo_area() { return co_area; }
    public void setCo_area(BigDecimal co_area) { this.co_area = co_area; }
    
    @Column(length=12)
    public String getFp_reg_num() { return fp_reg_num; }
    public void setFp_reg_num(String fp_reg_num) { this.fp_reg_num = fp_reg_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getFp_in_date() { return fp_in_date; }
    public void setFp_in_date(Date fp_in_date) { this.fp_in_date = fp_in_date; }
    
    @Temporal(TemporalType.DATE)
    public Date getFp_out_date() { return fp_out_date; }
    public void setFp_out_date(Date fp_out_date) { this.fp_out_date = fp_out_date; }
    
    @Column(length=512)
    public String getCo_address() { return co_address; }
    public void setCo_address(String co_address) { this.co_address = co_address; }
    
    @Column(length=30)
    public String getCo_address_code() { return co_address_code; }
    public void setCo_address_code(String co_address_code) { this.co_address_code = co_address_code; }
    
    @Column(precision=17, scale=3)
    public BigDecimal getDist_ns() { return dist_ns; }
    public void setDist_ns(BigDecimal dist_ns) { this.dist_ns = dist_ns; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getCo_category() { return co_category; }
    public void setCo_category(SpCommon co_category) { this.co_category = co_category; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getCo_feature() { return co_feature; }
    public void setCo_feature(SpCommon co_feature) { this.co_feature = co_feature; }
    
    @Column(length=1024)
    public String getCo_usage() { return co_usage; }
    public void setCo_usage(String co_usage) { this.co_usage = co_usage; }
    
    public Boolean getKi_gkh() { return ki_gkh; }
    public void setKi_gkh(Boolean ki_gkh) { this.ki_gkh = ki_gkh; }
    
    public Boolean getKi_az() { return ki_az; }
    public void setKi_az(Boolean ki_az) { this.ki_az = ki_az; }
    
    public Boolean getKi_omr() { return ki_omr; }
    public void setKi_omr(Boolean ki_omr) { this.ki_omr = ki_omr; }
    
    public Boolean getKi_ppz() { return ki_ppz; }
    public void setKi_ppz(Boolean ki_ppz) { this.ki_ppz = ki_ppz; }
    
    public Boolean getKi_inoe() { return ki_inoe; }
    public void setKi_inoe(Boolean ki_inoe) { this.ki_inoe = ki_inoe; }
    
    public Boolean getKi_neisp() { return ki_neisp; }
    public void setKi_neisp(Boolean ki_neisp) { this.ki_neisp = ki_neisp; }
    
    public Boolean getPr_formed() { return pr_formed; }
    public void setPr_formed(Boolean pr_formed) { this.pr_formed = pr_formed; }
    
    @Temporal(TemporalType.DATE)
    public Date getPr_prognoz_date() { return pr_prognoz_date; }
    public void setPr_prognoz_date(Date pr_prognoz_date) { this.pr_prognoz_date = pr_prognoz_date; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getPr_r_type() { return pr_r_type; }
    public void setPr_r_type(SpCommon pr_r_type) { this.pr_r_type = pr_r_type; }
    
    @Column(length=1024)
    public String getPr_owner() { return pr_owner; }
    public void setPr_owner(String pr_owner) { this.pr_owner = pr_owner; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getPr_term() { return pr_term; }
    public void setPr_term(SpCommon pr_term) { this.pr_term = pr_term; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getPr_o_type() { return pr_o_type; }
    public void setPr_o_type(SpCommon pr_o_type) { this.pr_o_type = pr_o_type; }
    
    @Temporal(TemporalType.DATE)
    public Date getPr_reg_rf_plan_date() { return pr_reg_rf_plan_date; }
    public void setPr_reg_rf_plan_date(Date pr_reg_rf_plan_date) { this.pr_reg_rf_plan_date = pr_reg_rf_plan_date; }
    
    @Temporal(TemporalType.DATE)
    public Date getPr_reg_rf_fact_date() { return pr_reg_rf_fact_date; }
    public void setPr_reg_rf_fact_date(Date pr_reg_rf_fact_date) { this.pr_reg_rf_fact_date = pr_reg_rf_fact_date; }
    
    @Temporal(TemporalType.DATE)
    public Date getPr_reg_other_plan_date() { return pr_reg_other_plan_date; }
    public void setPr_reg_other_plan_date(Date pr_reg_other_plan_date) { this.pr_reg_other_plan_date = pr_reg_other_plan_date; }
    
    @Temporal(TemporalType.DATE)
    public Date getPr_reg_other_fact_date() { return pr_reg_other_fact_date; }
    public void setPr_reg_other_fact_date(Date pr_reg_other_fact_date) { this.pr_reg_other_fact_date = pr_reg_other_fact_date; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSquare_suf() { return square_suf; }
    public void setSquare_suf(SpCommon square_suf) { this.square_suf = square_suf; }
    
    @Column(precision=17, scale=3)
    public BigDecimal getArea_suf() { return area_suf; }
    public void setArea_suf(BigDecimal area_suf) { this.area_suf = area_suf; }
    
    public Boolean getInv_attr() { return inv_attr; }
    public void setInv_attr(Boolean inv_attr) { this.inv_attr = inv_attr; }
    
    @Column(length=512)
    public String getEff_use_fut() { return eff_use_fut; }
    public void setEff_use_fut(String eff_use_fut) { this.eff_use_fut = eff_use_fut; }
    
    @Column(length=2048)
    public String getCo_chrs() { return co_chrs; }
    public void setCo_chrs(String co_chrs) { this.co_chrs = co_chrs; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getMarket_value() { return market_value; }
    public void setMarket_value(BigDecimal market_value) { this.market_value = market_value; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getCadastre_value() { return cadastre_value; }
    public void setCadastre_value(BigDecimal cadastre_value) { this.cadastre_value = cadastre_value; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getStandard_cost() { return standard_cost; }
    public void setStandard_cost(BigDecimal standard_cost) { this.standard_cost = standard_cost; }
   
    @Column(precision=17, scale=2)
    public BigDecimal getCo_rent() { return co_rent; }
    public void setCo_rent(BigDecimal co_rent) { this.co_rent = co_rent; }
    
	public static String singleTitle() { return "Земельный участок"; }
	public static String multipleTitle() { return "Земельные участки"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("co_org__name", "Подвед, учреждение")); 
		ret.add(new ColumnInfo("inv_number", "Инвентарный номер"));
		ret.add(new ColumnInfo("cad_num", "Кадастровый номер"));
		ret.add(new ColumnInfo("cad_date", "Дата постановки на кадастровый учет"));
		ret.add(new ColumnInfo("co_area", "Площадь земельного участка, кв.м"));
		ret.add(new ColumnInfo("fp_reg_num", "РНФИ"));
		ret.add(new ColumnInfo("fp_in_date", "Дата РНФИ"));
		ret.add(new ColumnInfo("fp_out_date", "Дата выбытия"));
		ret.add(new ColumnInfo("co_address", "Полный адрес"));
		ret.add(new ColumnInfo("co_address_code", "Код адреса", false));
		ret.add(new ColumnInfo("dist_ns", "Расстояние до ближайшего населенного пункта, м"));
		ret.add(new ColumnInfo("co_category__name", "Категория земель", false, true, "co_category__rn", "select", "listLandCategory"));
		ret.add(new ColumnInfo("co_usage", "Разрешенное использование (назначение)"));
		ret.add(new ColumnInfo("co_feature__name", "Особенности оборота", false, true, "co_feature__rn", "select", "listLandFeature"));
		ret.add(new ColumnInfo("ki_gkh", "ЖКХ", false));
		ret.add(new ColumnInfo("ki_az", "Административное здание", false));
		ret.add(new ColumnInfo("ki_omr", "Объект мобилизационного резерва", false));
		ret.add(new ColumnInfo("ki_ppz", "Прочие для производственных целей", false));
		ret.add(new ColumnInfo("ki_inoe", "Иное", false));
		ret.add(new ColumnInfo("ki_neisp", "Не используется", false));
		ret.add(new ColumnInfo("pr_formed", "Оформлен"));
		ret.add(new ColumnInfo("pr_prognoz_date", "Прогноз завершения оформления"));
		ret.add(new ColumnInfo("pr_r_type__name", "Вид права", false, true, "pr_r_type__rn", "select", "listRightType"));
		ret.add(new ColumnInfo("pr_owner", "Правообладатель"));
		ret.add(new ColumnInfo("pr_term__name", "Срок действия права", false, true, "pr_term__rn", "select", "listRightTerm"));
		ret.add(new ColumnInfo("pr_o_type__name", "Вид собственности", false, true, "pr_o_type__rn", "select", "listOwnershipType"));
		ret.add(new ColumnInfo("pr_reg_rf_plan_date", "Дата регистрации прав собственности РФ - планируемая", false));
		ret.add(new ColumnInfo("pr_reg_rf_fact_date", "Дата регистрации прав собственности РФ - фактическая", false));
		ret.add(new ColumnInfo("pr_reg_other_plan_date", "Дата регистрации иных вещных прав - планируемая", false));
		ret.add(new ColumnInfo("pr_reg_other_fact_date", "Дата регистрации иных вещных прав - фактическая", false));
		ret.add(new ColumnInfo("square_suf", "Достаточность по площади", false, true, "square_suf__rn", "select", "listSquareSufficiency"));
		ret.add(new ColumnInfo("area_suf", "Избыточная/недостающая площадь, кв.м", false));
		ret.add(new ColumnInfo("inv_attr", "Инвестиционная привлекательность", false));
		ret.add(new ColumnInfo("eff_use_fut", "Наиболее эффективное использование на перспективу", false));
		ret.add(new ColumnInfo("co_chrs", "Характеристика", false));
		ret.add(new ColumnInfo("market_value", "Рыночная стоимость"));
		ret.add(new ColumnInfo("cadastre_value", "Кадастровая стоимость"));
		ret.add(new ColumnInfo("standard_cost", "Нормативная стоимость"));
		ret.add(new ColumnInfo("co_rent", "Арендная плата (в месяц)"));
		ret.add(new ColumnInfo("comment", "Примечания", false));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	public static String spCode(String attr) {
		String ret = null;
		if ("co_category".equals(attr)) ret = "sp_kz"; 
		else if ("co_feature".equals(attr)) ret = "sp_ooz"; 
		else if ("pr_r_type".equals(attr)) ret = "sp_vpr"; 
		else if ("pr_term".equals(attr)) ret = "sp_sdpr"; 
		else if ("pr_r_type".equals(attr)) ret = "sp_vspr"; 
		else if ("square_suf".equals(attr)) ret = "sp_dpz"; 
		return ret;
	}
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
     	hs.setProperty(this, "co_type", "Недмижимое");
     	hs.setProperty(this, "co_div", objService.getObjByCode(SpPropertyDivision.class, "01"));
    	return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listLandCategory", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_kz"}));
			model.addAttribute("listLandFeature", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_ooz"}));
			model.addAttribute("listRightType", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_vpr"}));
			model.addAttribute("listRightTerm", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_sdpr"}));
			model.addAttribute("listOwnershipType", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_vspr"}));
			model.addAttribute("listSquareSufficiency", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_dpz"}));
		}
		catch (Exception ex) { }
		return true;
	}
}
