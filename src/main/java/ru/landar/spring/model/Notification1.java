package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class Notification1 extends Document {
	private Date date_repr;
	private List<Specification1> list_spec;
	
	@Temporal(TemporalType.DATE)
    public Date getDate_repr() { return date_repr; }
    public void setDate_repr(Date date_repr) { this.date_repr = date_repr; }
	
	@ManyToMany(targetEntity=Specification1.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Specification1> getList_spec() { return list_spec != null ? list_spec : new ArrayList<Specification1>(); }
    public void setList_spec(List<Specification1> list_spec) { this.list_spec = list_spec; }
    
    @Transient
    public String getBaseClazz() { return "Document"; }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setDoc_type((SpDocType)objService.getObjByCode(SpDocType.class, "71"));
      	return true;
    }
    
    public static String singleTitle() { return "Уведомление о базовых БА (200 и 400 группы ВР)"; }
	public static String multipleTitle() { return "Уведомления о базовых БА (200 и 400 группы ВР)"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("doc_number", "Номер документа"));
		ret.add(new ColumnInfo("doc_date", "Дата документа"));
		ret.add(new ColumnInfo("parent_doc__name", "Основание доведения"));
		ret.add(new ColumnInfo("depart__name", "Ответственное структурное подразделение"));
		ret.add(new ColumnInfo("date_repr", "Срок представления предложений на закупку"));
		ret.add(new ColumnInfo("comment", "Резолюция"));
		ret.add(new ColumnInfo("create_agent__name", "Документ создал"));
		ret.add(new ColumnInfo("doc_status__name", "Статус", true, true, "doc_status__rn", "select", "listDocStatus"));
		return ret;
	}
}
