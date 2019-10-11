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
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Объект имущества", multi="Объекты имущества", menu="Единый реестр имущества")
public class RProperty extends IBase {
	private IOrganization co_org;
	private String inv_number;
	private String on_nam;
	private SpCommon co_div;
	private String co_type;
	private BigDecimal book_value;
	private BigDecimal residual_value;
	private Date in_date;
	private Boolean ocdi;
	private List<RProperty_RProperty> list_prop;
	private List<RProperty_RDocument> list_doc;
	private String comment;
	
	@FieldTitle(name="Принадлежность")
	@ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getCo_org() { return co_org; }
    public void setCo_org(IOrganization co_org) { this.co_org = co_org; }
    
    @FieldTitle(name="Инвентарный номер")
    @Column(length=20)
    public String getInv_number() { return inv_number; }
    public void setInv_number(String inv_number) { this.inv_number = inv_number; }
    
    @FieldTitle(name="Наименование")
    @Column(length=2000)
    public String getOn_nam() { return on_nam; }
    public void setOn_nam(String on_nam) { this.on_nam = on_nam; setName(on_nam); }
	
    @FieldTitle(name="Раздел учета", sp="sp_rui")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getCo_div() { return co_div; }
    public void setCo_div(SpCommon co_div) { this.co_div = co_div; }
    
    @FieldTitle(name="Тип имущества", sp="p_type")
    @Column(length=16)
    public String getCo_type() { return co_type; }
    public void setCo_type(String co_type) { this.co_type = co_type; }
    
    @FieldTitle(name="Балансовая стоимость")
    @Column(precision=18, scale = 2)
    public BigDecimal getBook_value() { return book_value; }
    public void setBook_value(BigDecimal book_value) { this.book_value = book_value; }
    
    @FieldTitle(name="Остаточная стоимость")
    @Column(precision=18, scale = 2)
    public BigDecimal getResidual_value() { return residual_value; }
    public void setResidual_value(BigDecimal residual_value) { this.residual_value = residual_value; }
    
    @FieldTitle(name="Дата ввода в эксплуатацию")
    @Temporal(TemporalType.DATE)
    public Date getIn_date() { return in_date; }
    public void setIn_date(Date in_date) { this.in_date = in_date; }
    
    @FieldTitle(name="Связанные объекты имущества")
    @OneToMany(targetEntity=RProperty_RProperty.class, cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
    public List<RProperty_RProperty> getList_prop() { return list_prop != null ? list_prop : new ArrayList<RProperty_RProperty>(); }
    public void setList_prop(List<RProperty_RProperty> list_prop) { this.list_prop = list_prop; }
    
    @FieldTitle(name="Прикрепленные документы")
    @OneToMany(targetEntity=RProperty_RDocument.class, cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
    public List<RProperty_RDocument> getList_doc() { return list_doc != null ? list_doc : new ArrayList<RProperty_RDocument>(); }
    public void setList_doc(List<RProperty_RDocument> list_doc) { this.list_doc = list_doc; }
    
    @FieldTitle(name="Отнесен к ОЦДИ")
    public Boolean getOcdi() { return ocdi; }
    public void setOcdi(Boolean ocdi) { this.ocdi = ocdi; }
    
    @FieldTitle(name="Примечание", visible=false)
    @Column(length=2048)
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RProperty.class;
		ret.add(new ColumnInfo("co_org__name", cl)); 
		ret.add(new ColumnInfo("inv_number", cl));
		ret.add(new ColumnInfo("co_div", cl));
		ret.add(new ColumnInfo("co_type", cl));
		ret.add(new ColumnInfo("on_nam", cl));
		ret.add(new ColumnInfo("book_value", cl));
		ret.add(new ColumnInfo("residual_value", cl));
		ret.add(new ColumnInfo("in_date", cl));
		ret.add(new ColumnInfo("ocdi", cl));
		ret.add(new ColumnInfo("comment", cl));
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
			model.addAttribute("listSp_rui", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_rui"}));
			model.addAttribute("listP_type", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"p_type"}));
			model.addAttribute("listPp_conn_type", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"pp_conn_type"}));
		}
		catch (Exception ex) { }
		return null;
	}
}
