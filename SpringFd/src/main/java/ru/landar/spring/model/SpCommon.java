package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpCommon extends IBase {
	private String sp_code;
	
	@Column(length=50)
    public String getSp_code() { return sp_code; }
    public void setSp_code(String sp_code) { this.sp_code = sp_code; }
    
    public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Элемент общего справочника"; }
	public static String multipleTitle() { return "Элементы общего справочника"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("sp_code", "Код справочника"));
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("name", "Наименование")); 
		ret.add(new ColumnInfo("actual", "Актуальность"));
		return ret;
	}
	public static List<AttributeInfo> listAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("sp_code", "Код справочника", "text", null, true)); 
		ret.add(new AttributeInfo("code", "Код", "text", null, true)); 
		ret.add(new AttributeInfo("name", "Наименование элемента", "text", null, true));
		ret.add(new AttributeInfo("actual", "Актуальность", "checkbox", null, false));
		return ret;
	}
}
