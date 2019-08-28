package ru.landar.spring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import ru.landar.spring.classes.ColumnInfo;

public class Notification5 extends Document {
	private String kbk;
	private BigDecimal sum1;
	private BigDecimal sum2;
	private BigDecimal sum3;
	private List<Specification5> list_spec;
	
	@Column(length=20)
    public String getKbk() { return kbk; }
    public void setKbk(String kbk) { this.kbk = kbk; }
    
    public BigDecimal getSum1() { return sum1; }
    public void setSum1(BigDecimal sum1) { this.sum1 = sum1; }
    
    public BigDecimal getSum2() { return sum2; }
    public void setSum2(BigDecimal sum2) { this.sum2 = sum2; }
    
    public BigDecimal getSum3() { return sum3; }
    public void setSum3(BigDecimal sum3) { this.sum3 = sum3; }
	
	@ManyToMany(targetEntity=Specification5.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification5> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification5>(); }
    public void setList_spec(List<Specification5> list_spec) { this.list_spec = list_spec; }
    
    public static String singleTitle() { return "Уведомление о предложениях на закупку"; }
	public static String multipleTitle() { return "Уведомления о предложениях на закупку"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("doc_number", "Номер документа"));
		ret.add(new ColumnInfo("doc_date", "Дата документа"));
		ret.add(new ColumnInfo("kbk", "КБК"));
		ret.add(new ColumnInfo("sum1", "Сумма ЛБО (следующий год)"));
		ret.add(new ColumnInfo("sum2", "Сумма ЛБО (первый год планового периода)"));
		ret.add(new ColumnInfo("sum3", "Сумма ЛБО (второй год планового периода)"));
		ret.add(new ColumnInfo("parent_doc__name", "Основание изменения предложений на закупку"));
		ret.add(new ColumnInfo("depart__name", "Ответственное структурное подразделение"));
		ret.add(new ColumnInfo("comment", "Резолюция"));
		ret.add(new ColumnInfo("create_agent__name", "Документ создал"));
		ret.add(new ColumnInfo("doc_status__name", "Статус", true, true, "doc_status__rn", "select", "listDocStatus"));
		return ret;
	}
}
