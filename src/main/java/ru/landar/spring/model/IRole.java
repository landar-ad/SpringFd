package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.service.HelperServiceImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Роль", multi="Роли")
public class IRole extends IBase {
	private List<IRole_IRule> ro_prs;
	
	@FieldTitle(name="Код роли", required=true)
	public String getCode() { return super.getCode(); }
	
	@FieldTitle(name="Название роли", required=true)
	public String getName() { return super.getName(); }
	
	@FieldTitle(name="Правила")
    @OneToMany(targetEntity=IRole_IRule.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<IRole_IRule> getRo_prs() { return ro_prs != null ? ro_prs : new ArrayList<IRole_IRule>(); }
    public void setRo_prs(List<IRole_IRule> ro_prs) { this.ro_prs = ro_prs; }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = IRole.class;
		ret.add(new ColumnInfo("code", cl));
		ret.add(new ColumnInfo("name", cl)); 
		return ret;
	}
    
    public static List<AttributeInfo> listAttribute() {
    	return HelperServiceImpl.getListAttribute(IRule.class, true);
    }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	
    	return null;
	}
}
