package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Заявка", multi="Заявки")
public class RClaim extends IBase {
	private IOrganization co_org;
	private SpCommon za_type;
	private String za_num;
	private Date za_date;
	private String za_sod;
	private Boolean za_poppr;
	private String prim;
	private SpCommon za_stat;
	private String za_zc;
	private IPerson za_ol;
	private Date za_srz;
	private Date za_spo;
	private RClaim za_opr;
	private Boolean za_vzg;
	private List<RProperty> list_oz;
	private List<RDocument> list_doc;
	
	@FieldTitle(name="Принадлежность")
	@ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getCo_org() { return co_org; }
    public void setCo_org(IOrganization co_org) { this.co_org = co_org; }
    
    @FieldTitle(name="Тип заявки", sp="sp_type_za")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getZa_type() { return za_type; }
    public void setZa_type(SpCommon za_type) { this.za_type = za_type; }
    
    @FieldTitle(name="Номер заявки")
    @Column(length=30)
    public String getZa_num() { return za_num; }
    public void setZa_num(String za_num) { this.za_num = za_num; }
    
    @FieldTitle(name="Дата заявки")
    @Temporal(TemporalType.DATE)
    public Date getZa_date() { return za_date; }
    public void setZa_date(Date za_date) { this.za_date = za_date; }
    
    @FieldTitle(name="Содержание заявки", editType="textarea")
    @Column(length=2000)
    public String getZa_sod() { return za_sod; }
    public void setZa_sod(String za_sod) { this.za_sod = za_sod; }
    
    @FieldTitle(name="Признак применения оценки последствий принятия решений")
    public Boolean getZa_poppr() { return za_poppr; }
    public void setZa_poppr(Boolean za_poppr) { this.za_poppr = za_poppr; }
    
    @FieldTitle(name="Примечание", editType="textarea")
    @Column(length=4000)
    public String getPrim() { return prim; }
    public void setPrim(String prim) { this.prim = prim; }
    
    @FieldTitle(name="Статус заявки", sp="sp_stat_za")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getZa_stat() { return za_stat; }
    public void setZa_stat(SpCommon za_stat) { this.za_stat = za_stat; }
    
    @FieldTitle(name="Заключение членов комиссии", editType="textarea")
    @Column(length=4000)
    public String getZa_zc() { return za_zc; }
    public void setZa_zc(String za_zc) { this.za_zc = za_zc; }
    
    @FieldTitle(name="Ответственное лицо")
    @ManyToOne(targetEntity=IPerson.class, fetch=FetchType.LAZY)
    public IPerson getZa_ol() { return za_ol; }
    public void setZa_ol(IPerson za_ol) { this.za_ol = za_ol; }
    
    @FieldTitle(name="Срок рассмотрения заявки заявки")
    @Temporal(TemporalType.DATE)
    public Date getZa_srz() { return za_srz; }
    public void setZa_srz(Date za_srz) { this.za_srz = za_srz; }
    
    @FieldTitle(name="Срок предоставления отчета о реализации вынесенного решения")
    @Temporal(TemporalType.DATE)
    public Date getZa_spo() { return za_spo; }
    public void setZa_spo(Date za_spo) { this.za_spo = za_spo; }
    
    @FieldTitle(name="Заявка на оценку принятия решений")
    @ManyToOne(targetEntity=RClaim.class, fetch=FetchType.LAZY)
    public RClaim getZa_opr() { return za_opr; }
    public void setZa_opr(RClaim za_opr) { this.za_opr = za_opr; }
    
    @FieldTitle(name="Возможность заочного голосования")
    public Boolean getZa_vzg() { return za_vzg; }
    public void setZa_vzg(Boolean za_vzg) { this.za_vzg = za_vzg; }
	    
    @FieldTitle(name="Объекты заявки")
    @OneToMany(targetEntity=RProperty.class, fetch=FetchType.LAZY)
    public List<RProperty> getList_oz() { return list_oz != null ? list_oz : new ArrayList<RProperty>(); }
    public void setList_oz(List<RProperty> list_oz) { this.list_oz = list_oz; }
    
    @FieldTitle(name="Документы заявки")
    @OneToMany(targetEntity=RDocument.class, fetch=FetchType.LAZY)
    public List<RDocument> getList_doc() { return list_doc != null ? list_doc : new ArrayList<RDocument>(); }
    public void setList_doc(List<RDocument> list_doc) { this.list_doc = list_doc; }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RClaim.class;
		ret.add(new ColumnInfo("co_org__name", cl)); 
		ret.add(new ColumnInfo("za_type", cl));
		ret.add(new ColumnInfo("za_num", cl));
		ret.add(new ColumnInfo("za_date", cl));
		ret.add(new ColumnInfo("za_sod", cl));
		ret.add(new ColumnInfo("za_poppr", cl));
		ret.add(new ColumnInfo("prim", cl));
		ret.add(new ColumnInfo("za_stat", cl));
		ret.add(new ColumnInfo("za_zc", cl));
		ret.add(new ColumnInfo("za_ol", cl));
		ret.add(new ColumnInfo("za_srz", cl));
		ret.add(new ColumnInfo("za_spo", cl));
		ret.add(new ColumnInfo("za_opr__name", cl));
		ret.add(new ColumnInfo("za_vzg", cl));
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
			model.addAttribute("listSp_type_za", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_type_za"}));
			model.addAttribute("listSp_stat_za", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_stat_za"}));
		}
		catch (Exception ex) { }
		return null;
	}
}
