package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpObjectLocation extends IBase {
	private String fias_code;
	
	@Column(length=20)
    public String getFias_code() { return fias_code; }
    public void setFias_code(String fias_code) { this.fias_code = fias_code; }
	
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Расположение объекта"; }
	public static String multipleTitle() { return "Расположения объекта"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("name", "Наименование"));
		ret.add(new ColumnInfo("fias_code", "Код ФИАС"));
		return ret;
	}
	public static boolean listPaginated() { return true; }
}
