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
public class RBuilding extends RProperty {
	private String on_nam;
	private SpCommon on_typ;
	private SpCommon on_celn;
	private String adr_fo;
	private String adr_pa;
	private SpCommon s_fs;
	private SpCommon s_vps;
	private String rf_numre;
	private Date rf_dr;
	private String rf_sk;
	private Date th_dp;
	private Integer th_etag;
	private SpCommon th_mp;
	private SpCommon th_ms;
	private String th_ts;
	private BigDecimal s_perv;
	private BigDecimal s_amor;
	private BigDecimal plosh;
	
	@Column(length=2000)
    public String getOn_nam() { return on_nam; }
    public void setOn_nam(String on_nam) { this.on_nam = on_nam; setName(on_nam); }
	
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getOn_typ() { return on_typ; }
    public void setOn_typ(SpCommon on_typ) { this.on_typ = on_typ; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getOn_celn() { return on_celn; }
    public void setOn_celn(SpCommon on_celn) { this.on_celn = on_celn; }
	
	@Column(length=4000)
    public String getAdr_pa() { return adr_pa; }
    public void setAdr_pa(String adr_pa) { this.adr_pa = adr_pa; }
    
    @Column(length=40)
    public String getAdr_fo() { return adr_fo; }
    public void setAdr_fo(String adr_fo) { this.adr_fo = adr_fo; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getS_fs() { return s_fs; }
    public void setS_fs(SpCommon s_fs) { this.s_fs = s_fs; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getS_vps() { return s_vps; }
    public void setS_vps(SpCommon s_vps) { this.s_vps = s_vps; }
    
    @Column(length=100)
    public String getRf_numre() { return rf_numre; }
    public void setRf_numre(String rf_numre) { this.rf_numre = rf_numre; }
    
    @Temporal(TemporalType.DATE)
    public Date getRf_dr() { return rf_dr; }
    public void setRf_dr(Date rf_dr) { this.rf_dr = rf_dr; }
    
    @Column(length=100)
    public String getRf_sk() { return rf_sk; }
    public void setRf_sk(String rf_sk) { this.rf_sk = rf_sk; }
    
    @Temporal(TemporalType.DATE)
    public Date getTh_dp() { return th_dp; }
    public void setTh_dp(Date th_dp) { this.th_dp = th_dp; }
    
    public Integer getTh_etag() { return th_etag; }
    public void setTh_etag(Integer th_etag) { this.th_etag = th_etag; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getTh_mp() { return th_mp; }
    public void setTh_mp(SpCommon th_mp) { this.th_mp = th_mp; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getTh_ms() { return th_ms; }
    public void setTh_ms(SpCommon th_ms) { this.th_ms = th_ms; }
    
    @Column(length=4000)
    public String getTh_ts() { return th_ts; }
    public void setTh_ts(String th_ts) { this.th_ts = th_ts; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getS_perv() { return s_perv; }
    public void setS_perv(BigDecimal s_perv) { this.s_perv = s_perv; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getS_amor() { return s_amor; }
    public void setS_amor(BigDecimal s_amor) { this.s_amor = s_amor; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getPlosh() { return plosh; }
    public void setPlosh(BigDecimal plosh) { this.plosh = plosh; }
	
	public static String singleTitle() { return "Здание/сооружение"; }
	public static String multipleTitle() { return "Здания/сооружения"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("co_org__name", "Подвед, учреждение")); 
		ret.add(new ColumnInfo("inv_number", "Инвентарный номер"));
		ret.add(new ColumnInfo("on_nam", "Наименование"));
		ret.add(new ColumnInfo("on_typ__name", "Тип объекта", true, true, "on_typ__rn", "select", "listOnTyp"));
		ret.add(new ColumnInfo("on_celn__name", "Целевое назначение", true, true, "on_celn__rn", "select", "listOnCeln"));
		ret.add(new ColumnInfo("adr_pa", "Полный адрес"));
		ret.add(new ColumnInfo("adr_fo", "Код адреса", false));
		ret.add(new ColumnInfo("s_fs__name", "Форма собственности", true, true, "s_fs__rn", "select", "listSFs"));
		ret.add(new ColumnInfo("s_vps__name", "Вид права собственности", true, true, "s_vps__rn", "select", "listSVps"));
		ret.add(new ColumnInfo("rf_numre", "РНФИ"));
		ret.add(new ColumnInfo("rf_dr", "Дата РНФИ"));
		ret.add(new ColumnInfo("rf_sk", "Статус карты РНФИ"));
		ret.add(new ColumnInfo("th_dp", "Дата постройки"));
		ret.add(new ColumnInfo("th_etag", "Этаж"));
		ret.add(new ColumnInfo("th_mp__name", "Материал перекрытий", true, true, "th_mp__rn", "select", "listMatr"));
		ret.add(new ColumnInfo("th_ms__name", "Материал стен", true, true, "th_ms__rn", "select", "listMatr"));
		ret.add(new ColumnInfo("th_ts", "Техническое состояние"));
		ret.add(new ColumnInfo("s_perv", "Первоначальная стоимость"));
		ret.add(new ColumnInfo("s_amor", "Начисленная амортизация"));
		ret.add(new ColumnInfo("plosh", "Площадь, кв.м"));
		
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
     	hs.setProperty(this, "co_type", "Недмижимое");
     	hs.setProperty(this, "co_div", objService.getObjByCode(SpPropertyDivision.class, "02"));
    	return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listOnTyp", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_typo"}));
			model.addAttribute("listOnCeln", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_nazn"}));
			model.addAttribute("listSFs", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_fsob"}));
			model.addAttribute("listSVps", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_vidpfs"}));
			model.addAttribute("listMatr", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_matr"}));
		}
		catch (Exception ex) { }
		return true;
	}
}
