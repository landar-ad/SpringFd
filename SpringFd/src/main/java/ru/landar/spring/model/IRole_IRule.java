package ru.landar.spring.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.ui.Model;

import ru.landar.spring.classes.AttributeInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.service.HelperServiceImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Элемент списка правил", multi="Элементы списка правил")
public class IRole_IRule extends IBase {
	private IRule pr;
	private Boolean pr_bl;
	
	@FieldTitle(name="Правило")
	@ManyToOne(targetEntity=IRule.class, fetch=FetchType.LAZY)
    public IRule getUz() { return pr; }
    public void setUz(IRule pr) { this.pr = pr; }
    
    @FieldTitle(name="Заблокировано")
    public Boolean getPr_bl() { return pr_bl; }
    public void setPr_bl(Boolean pr_bl) { this.pr_bl = pr_bl; }
    
    public static List<AttributeInfo> listAttribute() {
    	return HelperServiceImpl.getListAttribute(IRole_IRule.class, true);
    }
    
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setPr_bl(false);
    	return this;
	}
    
    @Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listIRule", objService.findAll(IRule.class));
		}
		catch (Exception ex) { }
		return null;
	}
}
