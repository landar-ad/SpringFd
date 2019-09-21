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
public class SpFileType extends IBase {
	private String mimetype;
	private String ext;
    
    @Column(length=256)
    public String getMimetype() { return mimetype; }
    public void setMimetype(String mimetype) { this.mimetype = mimetype; }
    
    @Column(length=10)
    public String getExt() { return ext; }
    public void setExt(String ext) { this.ext = ext; }
    
    public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип файла"; }
	public static String multipleTitle() { return "Типы файла"; }
	public static String menuTitle() { return multipleTitle(); }
    public static List<ColumnInfo> listColumn() {
    	List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("name", "Наименование"));
		ret.add(new ColumnInfo("ext", "Расширение"));
		ret.add(new ColumnInfo("mimetype", "Тип"));
		return ret;
	}
    public static List<AttributeInfo> listAttribute() {
		
    	List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("code", "Код", "text", null, false, 2)); 
		ret.add(new AttributeInfo("name", "Наименование", "text", null, false));
		ret.add(new AttributeInfo("ext", "Расширение", "text", null, false, 4));
		ret.add(new AttributeInfo("mimetype", "Тип", "text", null, false));
		return ret;
	}
}