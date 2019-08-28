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
public class Specification4 extends IBase {
	private String kbk;
	private BigDecimal sum1;
	private BigDecimal sum2;
	private BigDecimal sum3;
	private BigDecimal sum4;
	private BigDecimal sum5;
	private BigDecimal sum6;
	
	@Column(length=20)
    public String getKbk() { return kbk; }
    public void setKbk(String kbk) { this.kbk = kbk; }
    
    public BigDecimal getSum1() { return sum1; }
    public void setSum1(BigDecimal sum1) { this.sum1 = sum1; }
    
    public BigDecimal getSum2() { return sum2; }
    public void setSum2(BigDecimal sum2) { this.sum2 = sum2; }
    
    public BigDecimal getSum3() { return sum3; }
    public void setSum3(BigDecimal sum3) { this.sum3 = sum3; }
    
    public BigDecimal getSum4() { return sum4; }
    public void setSum4(BigDecimal sum4) { this.sum4 = sum4; }
    
    public BigDecimal getSum5() { return sum5; }
    public void setSum5(BigDecimal sum5) { this.sum5 = sum5; }
    
    public BigDecimal getSum6() { return sum6; }
    public void setSum6(BigDecimal sum6) { this.sum6 = sum6; }
    
    public static String singleTitle() { return "Спецификация"; }
	public static String multipleTitle() { return "Спецификации"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("kbk", "КБК"));
		ret.add(new ColumnInfo("sum1", "Сумма БА (текущий год)"));
		ret.add(new ColumnInfo("sum2", "Сумма БА (первый год планового периода)"));
		ret.add(new ColumnInfo("sum3", "Сумма БА (второй год планового периода)"));
		ret.add(new ColumnInfo("sum4", "Сумма ЛБО (текущий год)"));
		ret.add(new ColumnInfo("sum5", "Сумма ЛБО (первый год планового периода)"));
		ret.add(new ColumnInfo("sum6", "Сумма ЛБО (второй год планового периода)"));
		return ret;
	}
}
