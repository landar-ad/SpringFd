package ru.landar.spring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IProperty extends IBase {
	private IOrganization org;
	private String inv_number;
	private SpPropertyType type;
	private BigDecimal book_value;
	private BigDecimal residual_value;
	private Date in_date;
	private List<IProperty> list_prop;
	private List<RDocument> list_doc;
	
	@ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getOrg() { return org; }
    public void setOrg(IOrganization org) { this.org = org; }
    
    @Column(length=50)
    public String getInv_number() { return inv_number; }
    public void setInv_number(String inv_number) { this.inv_number = inv_number; }
    
    @ManyToOne(targetEntity=SpPropertyType.class, fetch=FetchType.LAZY)
    public SpPropertyType getType() { return type; }
    public void setType(SpPropertyType type) { this.type = type; }
    
    @Column(precision=18, scale = 2)
    public BigDecimal getBook_value() { return book_value; }
    public void setBook_value(BigDecimal book_value) { this.book_value = book_value; }
    
    @Column(precision=18, scale = 2)
    public BigDecimal getResidual_value() { return residual_value; }
    public void setResidual_value(BigDecimal residual_value) { this.residual_value = residual_value; }
    
    @Temporal(TemporalType.DATE)
    public Date getIn_date() { return in_date; }
    public void setIn_date(Date in_date) { this.in_date = in_date; }
    
    @ManyToMany(targetEntity=IProperty.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<IProperty> getList_prop() { return list_prop != null ? list_prop : new ArrayList<IProperty>(); }
    public void setList_prop(List<IProperty> list_prop) { this.list_prop = list_prop; }
    
    @ManyToMany(targetEntity=RDocument.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<RDocument> getList_doc() { return list_doc != null ? list_doc : new ArrayList<RDocument>(); }
    public void setList_doc(List<RDocument> list_doc) { this.list_doc = list_doc; }
    
    public static String singleTitle() { return "Объект имущества"; }
	public static String multipleTitle() { return "Объекты имущества"; }
	public static String menuTitle() { return "Единый реестр имущества"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("org__name", "Подвед, учреждение")); 
		ret.add(new ColumnInfo("inv_number", "Инвентарный номер"));
		ret.add(new ColumnInfo("type__name", "Раздел учета"));
		ret.add(new ColumnInfo("name", "Наименование объекта имущества", true, true, "type__rn", "select", "listPropertyType"));
		ret.add(new ColumnInfo("book_value", "Балансовая стоимость"));
		ret.add(new ColumnInfo("residual_value", "Остаточная стоимость"));
		ret.add(new ColumnInfo("in_date", "Дата ввода в эксплуатацию"));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	
    	return ret;
	}
}
