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
import ru.landar.spring.service.HelperServiceImpl;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RLand extends RProperty {
	private String on_nam;
	private String kad_num;
	private Date kad_dpnu;
	private BigDecimal plosh;
	private String rf_ni;
	private Date rf_dr;
	private Date rf_vi;
	private String adr_pa;
	private String adr_fo;
	private SpCommon k_kz;
	private SpCommon k_vi;
	private SpCommon k_oo;
	private Boolean ki_gkh;
	private Boolean ki_az;
	private Boolean ki_omr;
	private Boolean ki_ppz;
	private Boolean ki_inoe;
	private Boolean ki_neisp;
	private Boolean so_of;
	private Date so_psz;
	private SpCommon so_vp;
	private Date so_sdp;
	private SpCommon so_fs;
	private Date so_drs_p;
	private Date so_drs_f;
	private Date so_drivp_p;
	private Date so_drivp_f;
	private BigDecimal s_rs;
	private BigDecimal s_ks;
	private BigDecimal s_ns;
	private BigDecimal s_ap;
	
	@Column(length=2000)
    public String getOn_nam() { return on_nam; }
    public void setOn_nam(String on_nam) { this.on_nam = on_nam; setName(on_nam); }
	
	@Column(length=18)
    public String getKad_num() { return kad_num; }
    public void setKad_num(String kad_num) { this.kad_num = kad_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getKad_dpnu() { return kad_dpnu; }
    public void setKad_dpnu(Date kad_dpnu) { this.kad_dpnu = kad_dpnu; }
    
    @Column(precision=17, scale=3)
    public BigDecimal getPlosh() { return plosh; }
    public void setPlosh(BigDecimal plosh) { this.plosh = plosh; }
    
    @Column(length=20)
    public String getRf_ni() { return rf_ni; }
    public void setRf_ni(String rf_ni) { this.rf_ni = rf_ni; }
    
    @Temporal(TemporalType.DATE)
    public Date getRf_dr() { return rf_dr; }
    public void setRf_dr(Date rf_dr) { this.rf_dr = rf_dr; }
    
    @Temporal(TemporalType.DATE)
    public Date getRf_vi() { return rf_vi; }
    public void setrf_vi(Date rf_vi) { this.rf_vi = rf_vi; }
    
    @Column(length=4000)
    public String getAdr_pa() { return adr_pa; }
    public void setAdr_pa(String adr_pa) { this.adr_pa = adr_pa; }
    
    @Column(length=40)
    public String getAdr_fo() { return adr_fo; }
    public void setAdr_fo(String adr_fo) { this.adr_fo = adr_fo; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getK_kz() { return k_kz; }
    public void setK_kz(SpCommon k_kz) { this.k_kz = k_kz; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getK_vi() { return k_vi; }
    public void setK_vi(SpCommon k_vi) { this.k_vi = k_vi; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getK_oo() { return k_oo; }
    public void setK_oo(SpCommon k_oo) { this.k_oo = k_oo; }
    
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
    
    public Boolean getSo_of() { return so_of; }
    public void setSo_of(Boolean so_of) { this.so_of = so_of; }
    
    @Temporal(TemporalType.DATE)
    public Date getSo_psz() { return so_psz; }
    public void setSo_psz(Date so_psz) { this.so_psz = so_psz; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSo_vp() { return so_vp; }
    public void setSo_vp(SpCommon so_vp) { this.so_vp = so_vp; }
    
    @Temporal(TemporalType.DATE)
    public Date getSo_sdp() { return so_sdp; }
    public void setSo_sdp(Date so_sdp) { this.so_sdp = so_sdp; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSo_fs() { return so_fs; }
    public void setSo_fs(SpCommon so_fs) { this.so_fs = so_fs; }
    
    @Temporal(TemporalType.DATE)
    public Date getSo_drs_p() { return so_drs_p; }
    public void setSo_drs_p(Date so_drs_p) { this.so_drs_p = so_drs_p; }
    
    @Temporal(TemporalType.DATE)
    public Date getSo_drs_f() { return so_drs_f; }
    public void setSo_drs_f(Date so_drs_f) { this.so_drs_f = so_drs_f; }
    
    @Temporal(TemporalType.DATE)
    public Date getSo_drivp_p() { return so_drivp_p; }
    public void setSo_drivp_p(Date so_drivp_p) { this.so_drivp_p = so_drivp_p; }
    
    @Temporal(TemporalType.DATE)
    public Date getSo_drivp_f() { return so_drivp_f; }
    public void setSo_drivp_f(Date so_drivp_f) { this.so_drivp_f = so_drivp_f; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getS_rs() { return s_rs; }
    public void setS_rs(BigDecimal s_rs) { this.s_rs = s_rs; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getS_ks() { return s_ks; }
    public void setS_ks(BigDecimal s_ks) { this.s_ks = s_ks; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getS_ns() { return s_ns; }
    public void setS_ns(BigDecimal s_ns) { this.s_ns = s_ns; }
   
    @Column(precision=17, scale=2)
    public BigDecimal getS_ap() { return s_ap; }
    public void setS_ap(BigDecimal s_ap) { this.s_ap = s_ap; }
    
	public static String singleTitle() { return "Земельный участок"; }
	public static String multipleTitle() { return "Земельные участки"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("co_org__name", "Подвед, учреждение")); 
		ret.add(new ColumnInfo("inv_number", "Инвентарный номер"));
		ret.add(new ColumnInfo("on_nam", "Наименование"));
		ret.add(new ColumnInfo("kad_num", "Кадастровый номер"));
		ret.add(new ColumnInfo("kad_dpnu", "Дата постановки на кадастровый учет"));
		ret.add(new ColumnInfo("plosh", "Площадь земельного участка, кв.м"));
		ret.add(new ColumnInfo("rf_ni", "РНФИ"));
		ret.add(new ColumnInfo("rf_dr", "Дата РНФИ"));
		ret.add(new ColumnInfo("rf_vi", "Дата выбытия"));
		ret.add(new ColumnInfo("adr_pa", "Полный адрес"));
		ret.add(new ColumnInfo("adr_fo", "Код адреса", false));
		ret.add(new ColumnInfo("k_kz__name", "Категория земель", false, true, "k_kz__rn", "select", "listSp_kz"));
		ret.add(new ColumnInfo("k_vi__name", "Разрешенное использование (назначение)", false, true, "k_vi__rn", "select", "listSp_vi"));
		ret.add(new ColumnInfo("k_oo__name", "Особенности оборота", false, true, "k_oo__rn", "select", "listSp_oo"));
		ret.add(new ColumnInfo("ki_gkh", "ЖКХ", false));
		ret.add(new ColumnInfo("ki_az", "Административное здание", false));
		ret.add(new ColumnInfo("ki_omr", "Объект мобилизационного резерва", false));
		ret.add(new ColumnInfo("ki_ppz", "Прочие для производственных целей", false));
		ret.add(new ColumnInfo("ki_inoe", "Иное", false));
		ret.add(new ColumnInfo("ki_neisp", "Не используется", false));
		ret.add(new ColumnInfo("so_of", "Оформлен"));
		ret.add(new ColumnInfo("so_psz", "Прогноз завершения оформления"));
		ret.add(new ColumnInfo("so_vp__name", "Вид права", false, true, "so_vp__rn", "select", "listSp_vidpfs"));
		ret.add(new ColumnInfo("so_sdp", "Срок действия права", false));
		ret.add(new ColumnInfo("so_fs__name", "Вид собственности", false, true, "so_fs__rn", "select", "listSp_fsob"));
		ret.add(new ColumnInfo("so_drs_p", "Дата регистрации прав собственности РФ - планируемая", false));
		ret.add(new ColumnInfo("so_drs_f", "Дата регистрации прав собственности РФ - фактическая", false));
		ret.add(new ColumnInfo("so_drivp_p", "Дата регистрации иных вещных прав - планируемая", false));
		ret.add(new ColumnInfo("so_drivp_f", "Дата регистрации иных вещных прав - фактическая", false));
		ret.add(new ColumnInfo("s_rs", "Рыночная стоимость"));
		ret.add(new ColumnInfo("s_ks", "Кадастровая стоимость"));
		ret.add(new ColumnInfo("s_ns", "Нормативная стоимость"));
		ret.add(new ColumnInfo("s_ap", "Арендная плата (в месяц)"));
		ret.add(new ColumnInfo("comment", "Примечания", false));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	public static String spCode(String attr) {
		String ret = null;
		if ("k_kz".equals(attr)) ret = "sp_kz"; 
		else if ("k_vi".equals(attr)) ret = "sp_vi";
		else if ("k_oo".equals(attr)) ret = "sp_oo"; 
		else if ("so_vp".equals(attr)) ret = "sp_vidpfs"; 
		else if ("so_fs".equals(attr)) ret = "sp_fsob"; 
		else ret = (String)HelperServiceImpl.invokeStatic(RProperty.class.getSuperclass(), "spCode", attr);
		return ret;
	}
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
     	hs.setProperty(this, "co_type", "Недмижимое");
     	hs.setProperty(this, "co_div", (SpCommon)objRepository.find(SpCommon.class, new String[] {"code", "sp_code"}, new Object[] {"01", "sp_rui"}));
    	return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listSp_kz", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_kz"}));
			model.addAttribute("listSp_vi", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_vi"}));
			model.addAttribute("listSp_oo", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_oo"}));
			model.addAttribute("listSp_vidpfs", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_vidpfs"}));
			model.addAttribute("listSp_fsob", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_fsob"}));
		}
		catch (Exception ex) { }
		return true;
	}
}
