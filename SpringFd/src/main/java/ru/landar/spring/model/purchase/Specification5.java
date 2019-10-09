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
@ObjectTitle(single="Спецификация уведомления о предложениях на закупку", multi="Спецификации уведомлений о предложениях на закупку")
public class Specification5 extends IBase {
	private String num;
	private String kosgu;
	private BigDecimal sum1;
	private BigDecimal sum2;
	private BigDecimal sum3;
	private String description;
	
	@FieldTitle(name="№ предложения на закупку")
	@Column(length=18)
    public String getNum() { return num; }
    public void setNum(String num) { this.num = num; }
	
    @FieldTitle(name="КОСГУ")
	@Column(length=3)
    public String getKosgu() { return kosgu; }
    public void setKosgu(String kosgu) { this.kosgu = kosgu; }
    
    @FieldTitle(name="Сумма (текущий год)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum1() { return sum1; }
    public void setSum1(BigDecimal sum1) { this.sum1 = sum1; }
    
    @FieldTitle(name="Сумма (первый год планового периода)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum2() { return sum2; }
    public void setSum2(BigDecimal sum2) { this.sum2 = sum2; }
    
    @FieldTitle(name="Сумма (второй год планового периода)")
    @Column(precision = 18, scale = 2)
    public BigDecimal getSum3() { return sum3; }
    public void setSum3(BigDecimal sum3) { this.sum3 = sum3; }
    
    @FieldTitle(name="Описание изменения")
    @Column(length=1000)
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Specification5.class;
		ret.add(new ColumnInfo("num", cl));
		ret.add(new ColumnInfo("kosgu", cl));
		ret.add(new ColumnInfo("sum1", cl));
		ret.add(new ColumnInfo("sum2", cl));
		ret.add(new ColumnInfo("sum3", cl));
		ret.add(new ColumnInfo("description", cl));
		return ret;
	}
}
