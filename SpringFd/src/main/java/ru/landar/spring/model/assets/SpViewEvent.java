package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Вид мероприятия", multi="Виды мероприятия", voc=true)
public class SpViewEvent extends IBase {
	
	private String razdel;
	private String doctype;
	
	@FieldTitle(name="Код", editLength=2)
	public String getCode() { return super.getCode(); }
	
	@FieldTitle(name="Раздел", editLength=4)
	@Column(length=32)
	public String getRazdel() { return razdel; }
    public void setRazdel(String razdel) { this.razdel = razdel; }
	
    @FieldTitle(name="Типы документа")
    @Column(length=100)
    public String getDoctype() { return doctype; }
    public void setDoctype(String doctype) { this.doctype = doctype; }
    
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = SpViewEvent.class;
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("name", cl));
		ret.add(new ColumnInfo("razdel", cl));
		ret.add(new ColumnInfo("doctype", cl));
		return ret;
	}
	public static List<AttributeInfo> listAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		Class<?> cl = SpViewEvent.class;
		ret.add(new AttributeInfo("code", cl)); 
		ret.add(new AttributeInfo("name", cl));
		ret.add(new AttributeInfo("razdel", cl));
		ret.add(new AttributeInfo("doctype", cl));
		return ret;
	}
}
