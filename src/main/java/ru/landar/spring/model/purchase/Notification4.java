package ru.landar.spring.model.purchase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.LockModeType;
import javax.persistence.OneToMany;
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
import ru.landar.spring.model.SpCommon;
import ru.landar.spring.model.fd.Document;
import ru.landar.spring.model.fd.SpDocType;
import ru.landar.spring.repository.ObjRepositoryCustom;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Уведомление о БР и ЛБО (200 и 400 группы ВР)", multi="Уведомления о БР и ЛБО (200 и 400 группы ВР)", menu="Уведомления о БР и ЛБО")
public class Notification4 extends Document {
	private Date date_repr;
	private List<Specification4> list_spec;
	
	@FieldTitle(name="Срок представления предложений на закупку")
	@Temporal(TemporalType.DATE)
    public Date getDate_repr() { return date_repr; }
    public void setDate_repr(Date date_repr) { this.date_repr = date_repr; }
	
    @FieldTitle(name="Спецификация")
	@OneToMany(targetEntity=Specification4.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification4> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification4>(); }
    public void setList_spec(List<Specification4> list_spec) { this.list_spec = list_spec; }
    
    @FieldTitle(name="Основание изменения БР и ЛБО")
    public Document getParent_doc() { return super.getParent_doc(); }
    
    @FieldTitle(name="Ответственное структурное подразделение")
    public IDepartment getDepart() { return super.getDepart(); }
   
    @FieldTitle(name="Резолюция")
    public String getComment() { return super.getComment(); }
    
    @FieldTitle(name="Документ создал")
    public IAgent getCreate_agent() { return super.getCreate_agent(); }
    
    @FieldTitle(name="Статус")
    public SpCommon getDoc_status() { return super.getDoc_status(); }
    
    @Transient
    public String getBaseClazz() { return "Document"; }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setDoc_type((SpDocType)objService.getObjByCode(SpDocType.class, "74"));
      	return true;
    }
    
    @Override
    public List<ButtonInfo> detailsButton() {
    	List<ButtonInfo> ret = super.detailsButton();
		if (ret == null) ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("createNotification5", "Сформировать предложения на закупку", "plus-square", "primary"));
		return ret;
    }
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Notification4.class;
		ret.add(new ColumnInfo("doc_number", cl));
		ret.add(new ColumnInfo("doc_date", cl));
		ret.add(new ColumnInfo("parent_doc__name", cl));
		ret.add(new ColumnInfo("depart__name", cl));
		ret.add(new ColumnInfo("date_repr", cl));
		ret.add(new ColumnInfo("comment", cl));
		ret.add(new ColumnInfo("create_agent__name", cl));
		ret.add(new ColumnInfo("doc_status", cl));
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
		else if ("createNotification5".equals(param)) {
			if (statusCode() != 4) return false;
			if (userService.isAdmin(null)) return true;
			return false;
		}
		return super.onCheckExecute(param);
    }
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public String createNotification5(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("createNotification5")) return null;
    	List<Integer> newRn = new ArrayList<Integer>();
    	List<Specification4> l = getList_spec();
    	for (Specification4 spec : l) {
    		if (hs.isEmpty(spec.getKbk())) continue;
    		Notification5 n5 = new Notification5();
    		hs.setProperty(n5, "kbk", spec.getKbk());
    		hs.setProperty(n5, "sum1", spec.getSum4());
    		hs.setProperty(n5, "sum2", spec.getSum5());
    		hs.setProperty(n5, "sum3", spec.getSum6());
    		hs.setProperty(n5, "depart", objRepository.findByCode(IDepartment.class, "09"));
    		n5.onNew();
    		objRepository.createObj(n5);
    		if (n5.getRn() != null) newRn.add(n5.getRn());
    	}
    	return newRn.size() == 1 ? "/detailsObj?clazz=Document&rn=" + newRn.get(0) : (newRn.size() > 1 ? "/listObj?clazz=Document&rn=" + newRn.get(0) : null);
	} 
	
}
