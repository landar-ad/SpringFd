package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Элемент общего справочника", multi="Элементы общего справочника", voc=true)
public class SpCommon extends IBase {
	private String sp_code;
	
	@FieldTitle(name="Код справочника")
	@Column(length=50)
    public String getSp_code() { return sp_code; }
    public void setSp_code(String sp_code) { this.sp_code = sp_code; }

	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Элемент общего справочника"; }
	public static String multipleTitle() { return "Элементы общего справочника"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = SpCommon.class;
		ret.add(new ColumnInfo("sp_code", cl));
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("name", cl)); 
		ret.add(new ColumnInfo("actual", cl));
		return ret;
	}
	public static List<AttributeInfo> listAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		Class<?> cl = SpCommon.class;
		ret.add(new AttributeInfo("sp_code", cl)); 
		ret.add(new AttributeInfo("code", cl)); 
		ret.add(new AttributeInfo("name", cl));
		ret.add(new AttributeInfo("actual", cl));
		return ret;
	}
}
