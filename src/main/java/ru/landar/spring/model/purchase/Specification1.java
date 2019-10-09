package ru.landar.spring.model.purchase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Спецификация уведомления о базовых БА (200 и 400 группы ВР)", multi="Спецификации уведомлений о базовых БА (200 и 400 группы ВР)")
public class Specification1 extends IBase {
	private String kbk;
	private BigDecimal sum1;
	private BigDecimal sum2;
	private BigDecimal sum3;
	
	@FieldTitle(name="КБК")
	@Column(length=20)
    public String getKbk() { return kbk; }
    public void setKbk(String kbk) { this.kbk = kbk; }
    
    @FieldTitle(name="Сумма ББА (следующий год)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum1() { return sum1; }
    public void setSum1(BigDecimal sum1) { this.sum1 = sum1; }
    
    @FieldTitle(name="Сумма ББА (первый год планового периода)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum2() { return sum2; }
    public void setSum2(BigDecimal sum2) { this.sum2 = sum2; }
    
    @FieldTitle(name="Сумма ББА (второй год планового периода)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum3() { return sum3; }
    public void setSum3(BigDecimal sum3) { this.sum3 = sum3; }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Specification1.class;
		ret.add(new ColumnInfo("kbk", cl));
		ret.add(new ColumnInfo("sum1", cl));
		ret.add(new ColumnInfo("sum2", cl));
		ret.add(new ColumnInfo("sum3", cl));
		return ret;
	}
}
