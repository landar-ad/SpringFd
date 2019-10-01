package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RBuilding extends RProperty {
	private String co_address;
	
	@Column(length=512)
    public String getCo_address() { return co_address; }
    public void setCo_address(String co_address) { this.co_address = co_address; }
	
	public static String singleTitle() { return "Здание/сооружение"; }
	public static String multipleTitle() { return "Здания/сооружения"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	
    	return ret;
	}
}
