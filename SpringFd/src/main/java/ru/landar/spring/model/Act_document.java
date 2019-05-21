package ru.landar.spring.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Act_document extends IBase {
	private Document doc;
	private Boolean exclude;
	private String exclude_reason;
	private Date exclude_date;
	
	@ManyToOne(targetEntity=Document.class, fetch=FetchType.LAZY)
    public Document getDoc() { return doc; }
    public void setDoc(Document doc) { this.doc = doc; }
    
    public Boolean getExclude() { return exclude; }
    public void setExclude(Boolean exclude) { this.exclude = exclude; }
    
    @Column(length=256)
    public String getExclude_reason() { return exclude_reason; }
    public void setExclude_reason(String exclude_reason) { this.exclude_reason = exclude_reason; }

    @Temporal(TemporalType.DATE)
    public Date getExclude_date() { return exclude_date; }
    public void seExclude_date(Date exclude_date) { this.exclude_date = exclude_date; }

}
