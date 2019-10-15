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
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Здание/сооружение", multi="Здания/сооружения")
public class RBuilding extends RProperty {
	private SpCommon on_typ;
	private SpCommon on_celn;
	private String adr_fo;
	private String adr_pa;
	private SpCommon so_fs;
	private SpCommon so_vp;
	private String rf_ni;
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
	
	@FieldTitle(name="Тип объекта", sp="sp_typo")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getOn_typ() { return on_typ; }
    public void setOn_typ(SpCommon on_typ) { this.on_typ = on_typ; }
    
    @FieldTitle(name="Целевое назначение", sp="sp_nazn")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getOn_celn() { return on_celn; }
    public void setOn_celn(SpCommon on_celn) { this.on_celn = on_celn; }
	
    @FieldTitle(name="Полный адрес")
	@Column(length=4000)
    public String getAdr_pa() { return adr_pa; }
    public void setAdr_pa(String adr_pa) { this.adr_pa = adr_pa; }
    
    @FieldTitle(name="Код адреса", visible=false)
    @Column(length=40)
    public String getAdr_fo() { return adr_fo; }
    public void setAdr_fo(String adr_fo) { this.adr_fo = adr_fo; }
    
    @FieldTitle(name="Вид права", sp="sp_vidpfs")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSo_vp() { return so_vp; }
    public void setSo_vp(SpCommon so_vp) { this.so_vp = so_vp; }

    @FieldTitle(name="Форма собственности", sp="sp_fsob")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSo_fs() { return so_fs; }
    public void setSo_fs(SpCommon so_fs) { this.so_fs = so_fs; }
    
    @FieldTitle(name="РНФИ")
    @Column(length=20)
    public String getRf_ni() { return rf_ni; }
    public void setRf_ni(String rf_ni) { this.rf_ni = rf_ni; }
    
    @FieldTitle(name="Дата РНФИ")
    @Temporal(TemporalType.DATE)
    public Date getRf_dr() { return rf_dr; }
    public void setRf_dr(Date rf_dr) { this.rf_dr = rf_dr; }
    
    @FieldTitle(name="Статус карты РНФИ")
    @Column(length=100)
    public String getRf_sk() { return rf_sk; }
    public void setRf_sk(String rf_sk) { this.rf_sk = rf_sk; }
    
    @FieldTitle(name="Дата постройки")
    @Temporal(TemporalType.DATE)
    public Date getTh_dp() { return th_dp; }
    public void setTh_dp(Date th_dp) { this.th_dp = th_dp; }
    
    @FieldTitle(name="Этаж")
    public Integer getTh_etag() { return th_etag; }
    public void setTh_etag(Integer th_etag) { this.th_etag = th_etag; }
    
    @FieldTitle(name="Материал перекрытий", sp="sp_matr")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getTh_mp() { return th_mp; }
    public void setTh_mp(SpCommon th_mp) { this.th_mp = th_mp; }
    
    @FieldTitle(name="Материал стен", sp="sp_matr")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getTh_ms() { return th_ms; }
    public void setTh_ms(SpCommon th_ms) { this.th_ms = th_ms; }
    
    @FieldTitle(name="Техническое состояние")
    @Column(length=4000)
    public String getTh_ts() { return th_ts; }
    public void setTh_ts(String th_ts) { this.th_ts = th_ts; }
    
    @FieldTitle(name="Первоначальная стоимость")
    @Column(precision=17, scale=2)
    public BigDecimal getS_perv() { return s_perv; }
    public void setS_perv(BigDecimal s_perv) { this.s_perv = s_perv; }
    
    @FieldTitle(name="Начисленная амортизация")
    @Column(precision=17, scale=2)
    public BigDecimal getS_amor() { return s_amor; }
    public void setS_amor(BigDecimal s_amor) { this.s_amor = s_amor; }
    
    @FieldTitle(name="Площадь, кв.м")
    @Column(precision=17, scale=2)
    public BigDecimal getPlosh() { return plosh; }
    public void setPlosh(BigDecimal plosh) { this.plosh = plosh; }
	
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RBuilding.class;
		ret.add(new ColumnInfo("co_org__name", cl)); 
		ret.add(new ColumnInfo("inv_number", cl));
		ret.add(new ColumnInfo("on_nam", cl));
		ret.add(new ColumnInfo("on_typ", cl));
		ret.add(new ColumnInfo("on_celn", cl));
		ret.add(new ColumnInfo("adr_pa", cl));
		ret.add(new ColumnInfo("adr_fo", cl));
		ret.add(new ColumnInfo("so_fs", cl));
		ret.add(new ColumnInfo("so_vp", cl));
		ret.add(new ColumnInfo("rf_ni", cl));
		ret.add(new ColumnInfo("rf_dr", cl));
		ret.add(new ColumnInfo("rf_sk", cl));
		ret.add(new ColumnInfo("th_dp", cl));
		ret.add(new ColumnInfo("th_etag", cl));
		ret.add(new ColumnInfo("th_mp", cl));
		ret.add(new ColumnInfo("th_ms", cl));
		ret.add(new ColumnInfo("th_ts", cl));
		ret.add(new ColumnInfo("s_perv", cl));
		ret.add(new ColumnInfo("s_amor", cl));
		ret.add(new ColumnInfo("plosh", cl));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
     	hs.setProperty(this, "co_type", "Недмижимое");
     	hs.setProperty(this, "co_div", (SpCommon)objRepository.find(SpCommon.class, new String[] {"code", "sp_code"}, new Object[] {"02", "sp_rui"}));
    	return this;
	}
	@Override
    public Object onUpdate() throws Exception { 
    	Object ret = super.onUpdate();
    	if (ret != null) return ret;
    	if (getBook_value() == null && getS_perv() != null) hs.setProperty(this, "book_value", getS_perv());
    	if (getResidual_value() == null && getS_perv() != null && getS_amor() != null && getS_amor().compareTo(getS_perv()) <= 0) hs.setProperty(this, "residual_value", getS_perv().subtract(getS_amor()));
    	if (getTh_dp() != null && getIn_date() == null) hs.setProperty(this, "in_date", getTh_dp());
		return true;
    }
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listSp_typo", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_typo"}));
			model.addAttribute("listSp_nazn", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_nazn"}));
			model.addAttribute("listSp_fsob", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_fsob"}));
			model.addAttribute("listSp_vidpfs", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_vidpfs"}));
			model.addAttribute("listSp_matr", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_matr"}));
		}
		catch (Exception ex) { }
		return true;
	}
}
