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
import ru.landar.spring.model.fd.Document;
import ru.landar.spring.model.fd.SpDocStatus;
import ru.landar.spring.model.fd.SpDocType;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Уведомление об утвержденных показателях бюджетной сметы", multi="Уведомления об утвержденных показателях бюджетной сметы", menu="Уведомления об утвержденных показателях")
public class Notification6 extends Document {
	private List<Specification6> list_spec;
	
	@FieldTitle(name="Спецификация")
	@ManyToMany(targetEntity=Specification6.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification6> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification6>(); }
    public void setList_spec(List<Specification6> list_spec) { this.list_spec = list_spec; }
    
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
    	setDoc_type((SpDocType)objService.getObjByCode(SpDocType.class, "76"));
      	return true;
    }
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Notification6.class;
		ret.add(new ColumnInfo("doc_number", cl));
		ret.add(new ColumnInfo("doc_date", cl));
		ret.add(new ColumnInfo("depart__name", "Ответственное структурное подразделение"));
		ret.add(new ColumnInfo("comment", "Резолюция"));
		ret.add(new ColumnInfo("create_agent__name", "Документ создал"));
		ret.add(new ColumnInfo("doc_status__name", "Статус", true, true, "doc_status__rn", "select", "listDocStatus"));
		return ret;
	}
}
