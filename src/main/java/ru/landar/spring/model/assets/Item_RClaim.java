package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Заявка на рассмотрении", multi="Заявки на рассмотрении")
public class Item_RClaim extends IBase {
	private RClaim claim;
	
	@FieldTitle(name="Заявка")
	@ManyToOne(targetEntity=RClaim.class, fetch=FetchType.LAZY)
    public RClaim getClaim() { return claim; }
    public void setClaim(RClaim claim) { this.claim = claim; updateName(); }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Item_RClaim.class;
		ret.add(new ColumnInfo("claim__name", cl));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	
    	return null;
	}
	
	private void updateName() {
    	AutowireHelper.autowire(this);
		String name = "";
		IBase p = getParentProxy();
		if (p != null) name += p.getName();
		if (!name.isEmpty()) name += " <-> ";
		if (claim != null) name += claim.getName();
		setName(name);
	}
    
    @Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listRClaim", objService.findAll(RClaim.class));
		}
		catch (Exception ex) { }
		return null;
	}
}
