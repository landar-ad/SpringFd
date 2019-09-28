package ru.landar.spring.model.fd;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpDocType extends IBase {
	private String fullname;
	private Boolean pay;
	
	@Column(length=2000)
	public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
	
	public Boolean getPay() { return pay; }
    public void setPay(Boolean pay) { this.pay = pay; }
	
    public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип документа"; }
	public static String multipleTitle() { return "Типы документа"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
    	List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("name", "Наименование"));
		ret.add(new ColumnInfo("fullname", "Полное наименование"));
		ret.add(new ColumnInfo("pay", "Платежный документ"));
		return ret;
	}
    public static List<AttributeInfo> listAttribute() {
		
    	List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("code", "Код", "text", null, true, 2)); 
		ret.add(new AttributeInfo("name", "Наименование", "text", null, true));
		ret.add(new AttributeInfo("fullname", "Полное наименование", "text", null, true));
		ret.add(new AttributeInfo("pay", "Платежный документ", "checkbox", null, false));
		return ret;
	}
    @Override
   	public Object onNew() {
    	Object ret = super.onNew();
       	if (ret != null) return ret;
       	setPay(false);
    	return true;
   	}
}
