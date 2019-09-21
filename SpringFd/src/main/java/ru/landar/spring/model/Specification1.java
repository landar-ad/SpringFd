package ru.landar.spring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Specification1 extends IBase {
	private String kbk;
	private BigDecimal sum1;
	private BigDecimal sum2;
	private BigDecimal sum3;
	
	@Column(length=20)
    public String getKbk() { return kbk; }
    public void setKbk(String kbk) { this.kbk = kbk; }
    
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum1() { return sum1; }
    public void setSum1(BigDecimal sum1) { this.sum1 = sum1; }
    
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum2() { return sum2; }
    public void setSum2(BigDecimal sum2) { this.sum2 = sum2; }
    
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum3() { return sum3; }
    public void setSum3(BigDecimal sum3) { this.sum3 = sum3; }
    
    public static String singleTitle() { return "Спецификация уведомления о базовых БА (200 и 400 группы ВР)"; }
	public static String multipleTitle() { return "Спецификации уведомлений о базовых БА (200 и 400 группы ВР)"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("kbk", "КБК"));
		ret.add(new ColumnInfo("sum1", "Сумма ББА (следующий год)"));
		ret.add(new ColumnInfo("sum2", "Сумма ББА (первый год планового периода)"));
		ret.add(new ColumnInfo("sum3", "Сумма ББА (второй год планового периода)"));
		return ret;
	}
}
