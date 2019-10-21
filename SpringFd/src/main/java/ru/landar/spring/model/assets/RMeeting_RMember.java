package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
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
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Член комиссии, участвующий в заседании", multi="Члены комиссии, участвующие в заседании")
public class RMeeting_RMember extends IBase {
	private RMember uz;
	private Boolean uz_pz;
	private String uz_oo;
	private SpCommon uz_rg;
	
	@FieldTitle(name="Член комиссии")
	@ManyToOne(targetEntity=RMember.class, fetch=FetchType.LAZY)
    public RMember getUz() { return uz; }
    public void setUz(RMember uz) { this.uz = uz; updateName(); }
    
    @FieldTitle(name="Присутствие на заседании")
    public Boolean getUz_pz() { return uz_pz; }
    public void setUz_pz(Boolean uz_pz) { this.uz_pz = uz_pz; }
    
    @FieldTitle(name="Особое мнение", editType="textarea")
    @Column(length=4000)
    public String getUz_oo() { return uz_oo; }
    public void setUz_oo(String uz_oo) { this.uz_oo = uz_oo; }
    
    @FieldTitle(name="Результат голосования", sp="sp_rezg")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
	public SpCommon getUz_rg() { return uz_rg; }
	public void setUz_rg(SpCommon uz_rg) { this.uz_rg = uz_rg; }
	
	private void updateName() {
    	AutowireHelper.autowire(this);
		String name = "";
		IBase p = getParentProxy();
		if (p != null) name += p.getName();
		if (!name.isEmpty()) name += " <-> ";
		if (uz != null) name += uz.getName();
		setName(name);
	}
	
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RMeeting_RMember.class;
		ret.add(new ColumnInfo("uz__name", cl));
		ret.add(new ColumnInfo("uz_pz", cl));
		ret.add(new ColumnInfo("uz_oo", cl));
		ret.add(new ColumnInfo("uz_rg", cl));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	
    	return null;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listSp_rezg", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_rezg"}));
			model.addAttribute("listRMember", objService.findAll(RMember.class));
		}
		catch (Exception ex) { }
		return null;
	}
}