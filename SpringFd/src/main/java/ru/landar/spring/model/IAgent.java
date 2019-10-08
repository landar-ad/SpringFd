package ru.landar.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Контрагент", multi="Контрагенты")
public abstract class IAgent extends IBase {
	private SpCommon type;
	private String mkod;
	
	@FieldTitle(name="Тип контрагента", sp="sp_typa")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getType() { return type; }
    public void setType(SpCommon type) { this.type = type; }
    
    @FieldTitle(name="Машинный код")
    @Column(length=5)
	public String getMkod() { return mkod; }
    public void setMkod(String mkod) { this.mkod = mkod; }
}
