package ru.landar.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name="rn")
public abstract class IAgent extends IBase {
	private SpAgentType type;
	private String mkod;
	
	@ManyToOne(targetEntity=SpAgentType.class, fetch=FetchType.LAZY)
    public SpAgentType getType() { return type; }
    public void setType(SpAgentType type) { this.type = type; }
    
	@Column(length=5)
    public String getMkod() { return mkod; }
    public void setMkod(String mkod) { this.mkod = mkod; }
}
