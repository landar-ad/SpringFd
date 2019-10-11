package ru.landar.spring.model.purchase;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IAgent;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.SpCommon;
import ru.landar.spring.model.fd.Document;
import ru.landar.spring.model.fd.SpDocType;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Уведомление о показателях проекта бюджетной сметы", multi="Уведомления о показателях проекта бюджетной сметы", menu="Уведомления о показателях проекта")
public class Notification3 extends Document {
	private List<Specification3> list_spec;
	
	@FieldTitle(name="Спецификация")
	@ManyToMany(targetEntity=Specification3.class, cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
    public List<Specification3> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification3>(); }
    public void setList_spec(List<Specification3> list_spec) { this.list_spec = list_spec; }
    
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
    	setDoc_type((SpDocType)objService.getObjByCode(SpDocType.class, "73"));
      	return true;
    }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Notification3.class;
		ret.add(new ColumnInfo("doc_number", cl));
		ret.add(new ColumnInfo("doc_date", cl));
		ret.add(new ColumnInfo("depart__name", cl));
		ret.add(new ColumnInfo("comment", cl));
		ret.add(new ColumnInfo("create_agent__name", cl));
		ret.add(new ColumnInfo("doc_status", cl));
		return ret;
	}
}
