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
@ObjectTitle(single="Тип файла", multi="Типы файла", voc=true)
public class SpFileType extends IBase {
	private String mimetype;
	private String ext;
    
	@FieldTitle(name="Код", editLength = 2)
	public String getCode() { return super.getCode(); }
	
	@FieldTitle(name="Тип")
    @Column(length=256)
    public String getMimetype() { return mimetype; }
    public void setMimetype(String mimetype) { this.mimetype = mimetype; }
    
    @FieldTitle(name="Расширение", editLength = 4)
    @Column(length=10)
    public String getExt() { return ext; }
    public void setExt(String ext) { this.ext = ext; }
    
    public static List<ColumnInfo> listColumn() {
    	List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
    	Class<?> cl = SpFileType.class;
		ret.add(new ColumnInfo("code", cl)); 
		ret.add(new ColumnInfo("name", cl));
		ret.add(new ColumnInfo("ext", cl));
		ret.add(new ColumnInfo("mimetype", cl));
		return ret;
	}
    public static List<AttributeInfo> listAttribute() {
    	List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
    	Class<?> cl = SpFileType.class;
		ret.add(new AttributeInfo("code", cl)); 
		ret.add(new AttributeInfo("name", cl));
		ret.add(new AttributeInfo("ext", cl));
		ret.add(new AttributeInfo("mimetype", cl));
		return ret;
	}
}