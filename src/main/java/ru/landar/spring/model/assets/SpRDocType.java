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
@ObjectTitle(single="Тип документа управления имуществом", multi="Типы документа управления имуществом", voc=true)
public class SpRDocType extends IBase {
	private String fullname;
	private Boolean pay;
	
	@FieldTitle(name="Код", editLength=2)
	public String getCode() { return super.getCode(); }
	
	@FieldTitle(name="Полное наименование", editType="textarea")
	@Column(length=2000)
	public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
	
    @FieldTitle(name="Платежный документ", editLength=2)
	public Boolean getPay() { return pay; }
    public void setPay(Boolean pay) { this.pay = pay; }

	public static List<ColumnInfo> listColumn() {
    	List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
    	Class<?> cl = SpRDocType.class;
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("name", cl));
		ret.add(new ColumnInfo("fullname", cl));
		ret.add(new ColumnInfo("pay", cl));
		return ret;
	}
    public static List<AttributeInfo> listAttribute() {
    	List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
    	Class<?> cl = SpRDocType.class;
		ret.add(new AttributeInfo("code", cl)); 
		ret.add(new AttributeInfo("name", cl));
		ret.add(new AttributeInfo("fullname", cl));
		ret.add(new AttributeInfo("pay", cl));
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
