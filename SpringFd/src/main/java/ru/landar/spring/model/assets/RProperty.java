package ru.landar.spring.model.assets;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RProperty extends IBase {
	private IOrganization co_org;
	private String inv_number;
	private SpPropertyDivision co_div;
	private String co_type;
	private BigDecimal book_value;
	private BigDecimal residual_value;
	private Date in_date;
	private Boolean ocdi;
	private List<RProperty_RProperty> list_prop;
	private List<RProperty_RDocument> list_doc;
	private String comment;
	
	@ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getCo_org() { return co_org; }
    public void setCo_org(IOrganization co_org) { this.co_org = co_org; }
    
    @Column(length=20)
    public String getInv_number() { return inv_number; }
    public void setInv_number(String inv_number) { this.inv_number = inv_number; }
    
    @ManyToOne(targetEntity=SpPropertyDivision.class, fetch=FetchType.LAZY)
    public SpPropertyDivision getCo_div() { return co_div; }
    public void setCo_div(SpPropertyDivision co_div) { this.co_div = co_div; }
    
    @Column(length=16)
    public String getCo_type() { return co_type; }
    public void setCo_type(String co_type) { this.co_type = co_type; }
    
    @Column(precision=18, scale = 2)
    public BigDecimal getBook_value() { return book_value; }
    public void setBook_value(BigDecimal book_value) { this.book_value = book_value; }
    
    @Column(precision=18, scale = 2)
    public BigDecimal getResidual_value() { return residual_value; }
    public void setResidual_value(BigDecimal residual_value) { this.residual_value = residual_value; }
    
    @Temporal(TemporalType.DATE)
    public Date getIn_date() { return in_date; }
    public void setIn_date(Date in_date) { this.in_date = in_date; }
    
    @ManyToMany(targetEntity=RProperty_RProperty.class, fetch=FetchType.LAZY)
    public List<RProperty_RProperty> getList_prop() { return list_prop != null ? list_prop : new ArrayList<RProperty_RProperty>(); }
    public void setList_prop(List<RProperty_RProperty> list_prop) { this.list_prop = list_prop; }
    
    @ManyToMany(targetEntity=RProperty_RDocument.class, fetch=FetchType.LAZY)
    public List<RProperty_RDocument> getList_doc() { return list_doc != null ? list_doc : new ArrayList<RProperty_RDocument>(); }
    public void setList_doc(List<RProperty_RDocument> list_doc) { this.list_doc = list_doc; }
    
    public Boolean getOcdi() { return ocdi; }
    public void setOcdi(Boolean ocdi) { this.ocdi = ocdi; }
    
    @Column(length=2048)
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public static String singleTitle() { return "Объект имущества"; }
	public static String multipleTitle() { return "Объекты имущества"; }
	public static String menuTitle() { return "Единый реестр имущества"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("co_org__name", "Подвед, учреждение")); 
		ret.add(new ColumnInfo("inv_number", "Инвентарный номер"));
		ret.add(new ColumnInfo("co_div__name", "Раздел учета", true, true, "co_div__rn", "select", "listPropertyDivision"));
		ret.add(new ColumnInfo("co_type", "Тип имущества", true, true, "co_type", "select", "listPropertyType", "code"));
		ret.add(new ColumnInfo("name", "Наименование объекта имущества"));
		ret.add(new ColumnInfo("book_value", "Балансовая стоимость"));
		ret.add(new ColumnInfo("residual_value", "Остаточная стоимость"));
		ret.add(new ColumnInfo("in_date", "Дата ввода в эксплуатацию"));
		ret.add(new ColumnInfo("ocdi", "Отнесен к ОЦДИ"));
		ret.add(new ColumnInfo("comment", "Примечание", false));
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
			model.addAttribute("listPropertyDivision", objService.findAll(SpPropertyDivision.class));
			model.addAttribute("listPropertyType", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"p_type"}));
			model.addAttribute("listConnectionType", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"pp_conn_type"}));
		}
		catch (Exception ex) { }
		return null;
	}
}
