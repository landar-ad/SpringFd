package ru.landar.spring.model;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.ObjectChanged;
import ru.landar.spring.repository.ObjRepositoryCustom;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Act_document extends IBase {
	private Document doc;
	private Boolean exclude;
	private String exclude_reason;
	private Date exclude_date;
	
	@ManyToOne(targetEntity=Document.class)
    public Document getDoc() { return doc; }
    public void setDoc(Document doc) { this.doc = doc; updateName(); }
    
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
	
	private void updateName() {
		String name = "";
		if (getParent() != null && getParent() instanceof Act) name += getParent().getName();
		if (doc != null) {
			if (!name.isEmpty()) name += " <-> ";
			name += doc.getName();
		}
	}
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setExclude(false);
     	return true;
    }
    @Autowired
	ObjRepositoryCustom objRepository;
    @Resource(name = "getObjectChanged")
    ObjectChanged objectChanged;
    @Override
    public Object onUpdate() throws Exception {
    	Object ret = super.onUpdate();
    	if (ret != null) return ret;
    	Act act = (Act)getParent();
		Document doc = getDoc();
		if (objectChanged.isAttrChanged(this, "doc")) {
			Document docOld = (Document)objectChanged.getAttrValue(this, "doc", 0);
			if (docOld != null) docOld.setAct(null);
			if (act != null && doc != null) doc.setAct(act);
		}
    	if (objectChanged.isAttrChanged(this, "exclude")) {
    		if (getExclude() != null && getExclude()) {
    			if (hs.isEmpty(getExclude_reason())) setExclude_reason("Причина не указана");
    			if (getExclude_date() == null) setExclude_date(new Date());
    			if (doc != null) {
    				doc.setDoc_status((SpDocStatus)objRepository.findByCode(SpDocStatus.class, "5"));
    				doc.setAct_exclude_date(act != null ? act.getAct_date() : null);
    				doc.setAct_exclude_num(act != null ? act.getAct_number() : null);
    				doc.setAct_exclude_reason(getExclude_reason());
    			}
    		}
    		else {
    			setExclude_date(null);
    			setExclude_reason(null);
    			if (doc != null) {
    				doc.setDoc_status((SpDocStatus)objRepository.findByCode(SpDocStatus.class, "3"));
    				doc.setAct_exclude_date(null);
    				doc.setAct_exclude_num(null);
    				doc.setAct_exclude_reason(null);
    			}
    		}
    	}
    	return true;
	}
    @Override
    public Object onRemove() {
    	Object ret = super.onRemove();
    	if (ret != null) return ret;
    	Document doc = getDoc();
    	if (doc != null) {
    		doc.setDoc_status((SpDocStatus)objRepository.findByCode(SpDocStatus.class, "2"));
    		doc.setAct(null);
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
    public Object onRedirectAfterUpdate(HttpServletRequest request) { 
    	Object ret = invoke("onRedirectAfterUpdate", request);
    	if (ret != null) return ret;
    	return getParent() != null ? "/detailsObj?clazz=" + getParent().getClazz() + "&rn=" + getParent().getRn() : null;
    }
}
