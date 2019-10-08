package ru.landar.spring.model.fd;

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
@ObjectTitle(single="Тип документа", multi="Типы документа", voc=true)
public class SpDocType extends IBase {
	private String fullname;
	private Boolean pay;
	
	@FieldTitle(name="Полное наименование")
	@Column(length=2000)
	public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
	
    @FieldTitle(name="Платежный документ")
	public Boolean getPay() { return pay; }
    public void setPay(Boolean pay) { this.pay = pay; }
	
	public static List<ColumnInfo> listColumn() {
    	List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
    	Class<?> cl = SpDocType.class;
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("name", cl));
		ret.add(new ColumnInfo("fullname", cl));
		ret.add(new ColumnInfo("pay", cl));
		return ret;
	}
    public static List<AttributeInfo> listAttribute() {
    	List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
    	Class<?> cl = SpDocType.class;
		ret.add(new AttributeInfo("code", cl, "text", null, true, 2, null)); 
		ret.add(new AttributeInfo("name", cl, "text", null, true, 0, null));
		ret.add(new AttributeInfo("fullname", cl, "text", null, true, 0, null));
		ret.add(new AttributeInfo("pay", cl, "checkbox", null, false, 2, null));
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
