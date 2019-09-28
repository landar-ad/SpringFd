package ru.landar.spring.model.assets;

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
public class SpViewEvent extends IBase {
	
	private String razdel;
	private String doctype;
    
	@Column(length=32)
	public String getRazdel() { return razdel; }
    public void setRazdel(String razdel) { this.razdel = razdel; }
	
    @Column(length=100)
    public String getDoctype() { return doctype; }
    public void setDoctype(String doctype) { this.doctype = doctype; }
    
    public static boolean isVoc() { return true; }
    public static String singleTitle() { return "Вид мероприятия"; }
	public static String multipleTitle() { return "Виды мероприятия"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("name", "Наименование"));
		ret.add(new ColumnInfo("razdel", "Раздел"));
		ret.add(new ColumnInfo("doctype", "Типы документа"));
		return ret;
	}
	public static List<AttributeInfo> listAttribute() {
		
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("code", "Код", "text", null, false, 2)); 
		ret.add(new AttributeInfo("name", "Наименование", "text", null, false));
		ret.add(new AttributeInfo("razdel", "Раздел", "text", null, false, 4));
		ret.add(new AttributeInfo("doctype", "Типы документа", "text", null, false));
		return ret;
	}
}
