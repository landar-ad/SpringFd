package ru.landar.spring.model;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.ui.Model;

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
    public void setExclude_date(Date exclude_date) { this.exclude_date = exclude_date; }
    
    public static String singleTitle() { return "Сведения о включенном в акт документе"; }
	public static String multipleTitle() { return "Сведения о включенных в акты документах"; }
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setExclude(false);
     	return true;
    }
    @Override
    public Object onUpdate(Map<String, Object> map, Map<String, Object[]> mapChanged) throws Exception {
    	Object ret = super.onUpdate(map, mapChanged);
    	if (ret != null) return ret;
     	Document doc = getDoc();
    	if (doc != null) {
    		boolean exclude = getExclude();
    		if (mapChanged.containsKey("exclude")) {
    			if (exclude) {
    				if (getExclude_date() == null) setExclude_date(new Date());
    				if (hs.isEmpty(getExclude_reason())) setExclude_reason("Причина не указана");
    			}
    			else {
    				setExclude_date(null);
    				setExclude_reason(null);
    			}
    		}
    		SpDocStatus doc_status = doc.getDoc_status();
    		if (doc_status  == null || (!"2".equals(doc_status) && !"3".equals(doc_status))) 
    		{ 
    			setExclude(true);
    			setExclude_date(null);
				setExclude_reason("Состояние документа не позволяет включить его в этот акт");
    			setDoc(null); 
    		}
    		else
    		{
	    		Act act = null;
	    		if (getParent() != null && getParent() instanceof Act) act = (Act)getParent();
	    		doc_status = (SpDocStatus)objService.getObjByCode(SpDocStatus.class, !exclude ? "4" : "5");
	    		doc.setDoc_status(doc_status);
	    		if (doc_status != null && act != null && "4".equals(doc_status.getCode())) {
	    			doc.setAct(act);
	    			doc.setAct_exclude_num(null);
					doc.setAct_exclude_date(null);
	    		}
	    		if ("5".equals(doc_status.getCode())) {
	    			doc.setAct(null);
	    			if (act != null) {
	    				doc.setAct_exclude_num(act.getAct_number());
	    				doc.setAct_exclude_date(act.getAct_date());
	    			}
	    		}
    		}
    	}
		objService.saveObj(this);
		return true;
	}
    @Override
    public Object onRemove() {
    	Object ret = super.onRemove();
    	if (ret != null) return ret;
    	Document doc = getDoc();
    	if (doc != null) {
    		doc.setDoc_status((SpDocStatus)objService.getObjByCode(SpDocStatus.class, "2"));
    	}
    	return true;
    }
    @Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		
		try {
			model.addAttribute("listDocument", objService.findAll(Document.class, null, new String[] {"doc_status__code"}, new Object[] {"2"}));
		}
		catch (Exception ex) { }
		return true;
	}
    @Override
    public Object onRedirectAfterUpdate() { 
    	Object ret = invoke("onRedirectAfterUpdate");
    	if (ret != null) return ret;
    	return getParent() != null ? "/detailsObj?clazz=Act" + "&rn=" + getParent().getRn() : super.onRedirectAfterUpdate();
    }
}
