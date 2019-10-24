package ru.landar.spring.model;

import java.util.List;

import javax.persistence.Column;
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
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.HelperServiceImpl;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Элемент списка правил", multi="Элементы списка правил")
public class IRole_IRule extends IBase {
	private IRule pr;
	private Boolean pr_bl;
	private String pr_isp;
	
	@FieldTitle(name="Правило")
	@ManyToOne(targetEntity=IRule.class, fetch=FetchType.LAZY)
    public IRule getPr() { return pr; }
    public void setPr(IRule pr) { this.pr = pr; updateName(); }
    
    @FieldTitle(name="Заблокировано")
    public Boolean getPr_bl() { return pr_bl; }
    public void setPr_bl(Boolean pr_bl) { this.pr_bl = pr_bl; }
    
    @FieldTitle(name="Контекст")
    @Column(length=50)
    public String getPr_isp() { return pr_isp; }
    public void sePr_isp(String pr_isp) { this.pr_isp = pr_isp; }
    
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
    
    private void updateName() {
    	AutowireHelper.autowire(this);
		String name = "";
		IBase p = getParentProxy();
		if (p != null) name += p.getName();
		if (!name.isEmpty()) name += " <-> ";
		if (pr != null) name += pr.getName();
		setName(name);
	}
}
