package ru.landar.spring.model;

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
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.repository.ObjRepositoryCustom;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Notification4 extends Document {
	private Date date_repr;
	private List<Specification4> list_spec;
	
	@Temporal(TemporalType.DATE)
    public Date getDate_repr() { return date_repr; }
    public void setDate_repr(Date date_repr) { this.date_repr = date_repr; }
	
	@ManyToMany(targetEntity=Specification4.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification4> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification4>(); }
    public void setList_spec(List<Specification4> list_spec) { this.list_spec = list_spec; }
    
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
		ret.add(new ButtonInfo("createNotification5", "Сформировать предложения на закупку", null, "primary"));
		return ret;
    }
    
    public static String singleTitle() { return "Уведомление о БР и ЛБО (200 и 400 группы ВР)"; }
	public static String multipleTitle() { return "Уведомления о БР и ЛБО (200 и 400 группы ВР)"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("doc_number", "Номер документа"));
		ret.add(new ColumnInfo("doc_date", "Дата документа"));
		ret.add(new ColumnInfo("parent_doc__name", "Основание изменения БР и ЛБО"));
		ret.add(new ColumnInfo("depart__name", "Ответственное структурное подразделение"));
		ret.add(new ColumnInfo("date_repr", "Срок представления предложений на закупку"));
		ret.add(new ColumnInfo("comment", "Резолюция"));
		ret.add(new ColumnInfo("create_agent__name", "Документ создал"));
		ret.add(new ColumnInfo("doc_status__name", "Статус", true, true, "doc_status__rn", "select", "listDocStatus"));
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
