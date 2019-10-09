package ru.landar.spring.model.purchase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.LockModeType;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;

import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IAgent;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.fd.Document;
import ru.landar.spring.model.fd.SpDocStatus;
import ru.landar.spring.model.fd.SpDocType;
import ru.landar.spring.repository.ObjRepositoryCustom;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Уведомление о базовых БА (200 и 400 группы ВР)", multi="Уведомления о базовых БА (200 и 400 группы ВР)", menu="Уведомления о базовых БА")
public class Notification1 extends Document {
	private Date date_repr;
	private List<Specification1> list_spec;
	
	@FieldTitle(name="Срок представления предложений на закупку")
	@Temporal(TemporalType.DATE)
    public Date getDate_repr() { return date_repr; }
    public void setDate_repr(Date date_repr) { this.date_repr = date_repr; }
	
    @FieldTitle(name="Спецификация")
	@ManyToMany(targetEntity=Specification1.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification1> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification1>(); }
    public void setList_spec(List<Specification1> list_spec) { this.list_spec = list_spec; }
    
    @FieldTitle(name="Основание доведения")
    public Document getParent_doc() { return super.getParent_doc(); }
    
    @FieldTitle(name="Ответственное структурное подразделение")
    public IDepartment getDepart() { return super.getDepart(); }
   
    @FieldTitle(name="Резолюция")
    public String getComment() { return super.getComment(); }
    
    @FieldTitle(name="Документ создал")
    public IAgent getCreate_agent() { return super.getCreate_agent(); }
    
    @FieldTitle(name="Статус")
    public SpDocStatus getDoc_status() { return super.getDoc_status(); }
    
    @Transient
    public String getBaseClazz() { return "Document"; }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setDoc_type((SpDocType)objService.getObjByCode(SpDocType.class, "71"));
      	return true;
    }
    @Override
    public List<ButtonInfo> detailsButton() {
    	List<ButtonInfo> ret = super.detailsButton();
		if (ret == null) ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("createNotification2", "Сформировать проекты предложений на закупку", "plus-square", "primary"));
		return ret;
    }
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Notification1.class;
		ret.add(new ColumnInfo("doc_number", cl));
		ret.add(new ColumnInfo("doc_date", cl));
		ret.add(new ColumnInfo("parent_doc__name", cl));
		ret.add(new ColumnInfo("depart__name", cl));
		ret.add(new ColumnInfo("date_repr", cl));
		ret.add(new ColumnInfo("comment", cl));
		ret.add(new ColumnInfo("create_agent__name", cl));
		ret.add(new ColumnInfo("doc_status__name", cl, true, true, "*", "select"));
		return ret;
	}
	@Autowired
	ObjRepositoryCustom objRepository;
	@Override
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
     	if ("save".equals(param)) return onCheckRights(Operation.update);
     	else if ("cancel".equals(param)) return true;
		else if ("createNotification2".equals(param)) {
			if (statusCode() != 4) return false;
			if (userService.isAdmin(null)) return true;
			return false;
		}
		return super.onCheckExecute(param);
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public String createNotification2(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("createNotification2")) return null;
    	List<Integer> newRn = new ArrayList<Integer>();
    	List<Specification1> l = getList_spec();
    	for (Specification1 spec : l) {
    		if (hs.isEmpty(spec.getKbk())) continue;
    		Notification2 n2 = new Notification2();
    		hs.setProperty(n2, "kbk", spec.getKbk());
    		hs.setProperty(n2, "sum1", spec.getSum1());
    		hs.setProperty(n2, "sum2", spec.getSum2());
    		hs.setProperty(n2, "sum3", spec.getSum3());
    		hs.setProperty(n2, "depart", objRepository.findByCode(IDepartment.class, "09"));
    		n2.onNew();
    		objRepository.createObj(n2);
    		if (n2.getRn() != null) newRn.add(n2.getRn());
    	}
    	return newRn.size() == 1 ? "/detailsObj?clazz=Document&rn=" + newRn.get(0) : (newRn.size() > 1 ? "/listObj?clazz=Document&rn=" + newRn.get(0) : null);
	} 
}
