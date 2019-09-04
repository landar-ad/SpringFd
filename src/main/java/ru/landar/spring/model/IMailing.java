package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IMailing extends IBase {
	private IPerson person;
	
	@ManyToOne(targetEntity=IPerson.class, fetch=FetchType.LAZY)
    public IPerson getPerson() { return person; }
    public void setPerson(IPerson person) { this.person = person; }
}
