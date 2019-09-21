package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Notification6 extends Document {
	private List<Specification6> list_spec;
	
	@ManyToMany(targetEntity=Specification6.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification6> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification6>(); }
    public void setList_spec(List<Specification6> list_spec) { this.list_spec = list_spec; }
    
    @Transient
    public String getBaseClazz() { return "Document"; }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setDoc_type((SpDocType)objService.getObjByCode(SpDocType.class, "76"));
      	return true;
    }
    
    public static String singleTitle() { return "Уведомление об утвержденных показателях бюджетной сметы"; }
	public static String multipleTitle() { return "Уведомления об утвержденных показателях бюджетной сметы"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("doc_number", "Номер документа"));
		ret.add(new ColumnInfo("doc_date", "Дата документа"));
		ret.add(new ColumnInfo("depart__name", "Ответственное структурное подразделение"));
		ret.add(new ColumnInfo("comment", "Резолюция"));
		ret.add(new ColumnInfo("create_agent__name", "Документ создал"));
		ret.add(new ColumnInfo("doc_status__name", "Статус", true, true, "doc_status__rn", "select", "listDocStatus"));
		return ret;
	}
}
