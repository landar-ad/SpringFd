package ru.landar.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.assets.RProperty;
import ru.landar.spring.service.HelperServiceImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name="rn")
public abstract class IAgent extends IBase {
	private SpCommon type;
	private String mkod;
	
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getType() { return type; }
    public void setType(SpCommon type) { this.type = type; }
    
	@Column(length=5)
    public String getMkod() { return mkod; }
    public void setMkod(String mkod) { this.mkod = mkod; }
    
    public static String spCode(String attr) {
		String ret = null;
		if ("type".equals(attr)) ret = "sp_typa"; 
		else ret = (String)HelperServiceImpl.invokeStatic(RProperty.class.getSuperclass(), "spCode", attr);
		return ret;
	}
}
