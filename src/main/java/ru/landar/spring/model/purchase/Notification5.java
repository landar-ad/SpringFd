package ru.landar.spring.model.purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IAgent;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.fd.Document;
import ru.landar.spring.model.fd.SpDocStatus;
import ru.landar.spring.model.fd.SpDocType;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Уведомление о предложениях на закупку", multi="Уведомления о предложениях на закупку", menu="Уведомления о предложениях")
public class Notification5 extends Document {
	private String kbk;
	private BigDecimal sum1;
	private BigDecimal sum2;
	private BigDecimal sum3;
	private List<Specification5> list_spec;
	
	@FieldTitle(name="КБК")
	@Column(length=20)
    public String getKbk() { return kbk; }
    public void setKbk(String kbk) { this.kbk = kbk; }
    
    @FieldTitle(name="Сумма ЛБО (текущий год)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum1() { return sum1; }
    public void setSum1(BigDecimal sum1) { this.sum1 = sum1; }
    
    @FieldTitle(name="Сумма ЛБО (первый год планового периода)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum2() { return sum2; }
    public void setSum2(BigDecimal sum2) { this.sum2 = sum2; }
    
    @FieldTitle(name="Сумма ЛБО (второй год планового периода)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum3() { return sum3; }
    public void setSum3(BigDecimal sum3) { this.sum3 = sum3; }
	
    @FieldTitle(name="Спецификация")
	@ManyToMany(targetEntity=Specification5.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification5> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification5>(); }
    public void setList_spec(List<Specification5> list_spec) { this.list_spec = list_spec; }
    
    @FieldTitle(name="Основание изменения предложений на закупку")
    public Document getParent_doc() { return super.getParent_doc(); }
    
    @FieldTitle(name="Ответственное структурное подразделение")
    public IDepartment getDepart() { return super.getDepart(); }
   
    @FieldTitle(name="Резолюция")
    public String getComment() { return super.getComment(); }
    
    @FieldTitle(name="Документ создал")
    public IAgent getCreate_agent() { return super.getCreate_agent(); }
    
    @FieldTitle(name="Статус")
    public SpDocStatus getDoc_status() { return super.getDoc_status(); }
    
    @Transient
    public String getBaseClazz() { return "Document"; }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setDoc_type((SpDocType)objService.getObjByCode(SpDocType.class, "75"));
      	return true;
    }
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Notification5.class;
		ret.add(new ColumnInfo("doc_number", cl));
		ret.add(new ColumnInfo("doc_date", cl));
		ret.add(new ColumnInfo("kbk", cl));
		ret.add(new ColumnInfo("sum1", cl));
		ret.add(new ColumnInfo("sum2", cl));
		ret.add(new ColumnInfo("sum3", cl));
		ret.add(new ColumnInfo("parent_doc__name", cl));
		ret.add(new ColumnInfo("depart__name", cl));
		ret.add(new ColumnInfo("comment", cl));
		ret.add(new ColumnInfo("create_agent__name", cl));
		ret.add(new ColumnInfo("doc_status__name", cl, true, true, "*", "select"));
		return ret;
	}
}
