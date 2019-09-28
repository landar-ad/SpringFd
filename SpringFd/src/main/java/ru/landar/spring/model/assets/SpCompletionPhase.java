package ru.landar.spring.model.assets;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
public class SpCompletionPhase extends IBase {
	
	private String phasename;
	private BigDecimal minpct;
	private BigDecimal maxpct;
	
	@Column(length=256)
    public String getPhasename() { return phasename; }
    public void setPhasename(String phasename) { this.phasename = phasename; updateName(); }
    
    @Column(precision=17, scale = 2)
    public BigDecimal getMinpct() { return minpct; }
    public void setMinpct(BigDecimal minpct) { this.minpct = minpct; updateName(); }
    
    @Column(precision=17, scale = 2)
    public BigDecimal getMaxpct() { return maxpct; }
    public void setMaxpct(BigDecimal minpct) { this.maxpct = minpct; updateName(); }
    
    private void updateName()
    {
    	String name = "";
    	if (getPhasename() != null) name = getPhasename();
    	if (getMinpct() != null || getMaxpct() != null)
    	{
    		name += " (";
    		if (getMinpct() != null) name += new DecimalFormat("###.##").format(getMinpct()) + "%";
    		if (getMaxpct() != null && (getMinpct() == null || getMaxpct().compareTo(getMinpct()) > 0)) 
    		{
    			if (getMinpct() != null) name += " - ";
    			name += new DecimalFormat("###.##").format(getMaxpct()) + "%";
    		}
    		name += ")";
    	}
    	setName(name);
    }
    public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Стадия строительства"; }
	public static String multipleTitle() { return "Стадии строительства"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("code", "Код")); 
		ret.add(new ColumnInfo("phasename", "Наименование стадии строительства"));
		ret.add(new ColumnInfo("minpct", "Минимальный процент готовности"));
		ret.add(new ColumnInfo("maxpct", "Максимальный процент готовности"));
		return ret;
	}
	public static List<AttributeInfo> listAttribute() {
		List<AttributeInfo> ret = new ArrayList<AttributeInfo>();
		ret.add(new AttributeInfo("code", "Код", "text", null, false, 2)); 
		ret.add(new AttributeInfo("phasename", "Наименование стадии строительства", "text", null, false));
		ret.add(new AttributeInfo("minpct", "Минимальный процент готовности", "text", null, false, 2));
		ret.add(new AttributeInfo("maxpct", "Максимальный процент готовности", "text", null, false, 2));
		return ret;
	}
}