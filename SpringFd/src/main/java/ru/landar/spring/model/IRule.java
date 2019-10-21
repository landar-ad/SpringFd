package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.ui.Model;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.service.HelperServiceImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Правило", multi="Правила")
public class IRule extends IBase {
	private SpCommon pr_dr;
	private Boolean pr_razr; 
	private String pr_filter;
	
	@FieldTitle(name="Название правила", required=true)
	public String getName() { return super.getName(); }
	
	@FieldTitle(name="Код правила", required=true)
	public String getCode() { return super.getCode(); }
	
	@FieldTitle(name="Разрешено", required=true)
    public Boolean getPr_razr() { return pr_razr; }
    public void setPr_razr(Boolean pr_razr) { this.pr_razr = pr_razr; }
    
    @FieldTitle(name="Фильтр")
    public String getPr_filter() { return pr_filter; }
    public void setPr_filter(String pr_filter) { this.pr_filter = pr_filter; }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = IRule.class;
		ret.add(new ColumnInfo("code", cl));
		ret.add(new ColumnInfo("name", cl)); 
		ret.add(new ColumnInfo("pr_razr", cl));
		ret.add(new ColumnInfo("pr_filter", cl));
		return ret;
	}
    
    public static List<AttributeInfo> listAttribute() {
    	return HelperServiceImpl.getListAttribute(IRule.class, true);
    }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setPr_razr(true);
    	return this;
	}
    
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
		}
		catch (Exception ex) { }
		return null;
	}
}
