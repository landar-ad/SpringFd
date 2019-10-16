package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Связь между типами комиссий и заявок", multi="Связи между типами комиссий и заявок", voc=true)
public class SpRCommission_RClaim {
	private SpCommon c_type;
	private SpCommon za_type;
	
	@FieldTitle(name="Тип комиссии", sp="sp_type_c")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
	public SpCommon getC_type() { return c_type; }
	public void setC_type(SpCommon c_type) { this.c_type = c_type; }
	
	@FieldTitle(name="Тип заявки", sp="sp_type_za")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getZa_type() { return za_type; }
    public void setZa_type(SpCommon za_type) { this.za_type = za_type; }
}
