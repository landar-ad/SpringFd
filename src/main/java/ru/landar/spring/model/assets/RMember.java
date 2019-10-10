package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Член комиссии Министерства", multi="Члены комиссии Министерства")
public class RMember extends IBase {
	private SpCommon cs_rol;
	private IPerson cs_sot;
	private Date cs_dn;
	private Date cs_dv;
	private IDepartment cs_dep;
	private List<RDocument> cs_docs;
	
	@FieldTitle(name="Роль", sp="sp_cs_rol")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
	public SpCommon getCs_rol() { return cs_rol; }
	public void setCs_rol(SpCommon cs_rol) { this.cs_rol = cs_rol; }
	
	@FieldTitle(name="Сотрудник")
    @ManyToOne(targetEntity=IPerson.class, fetch=FetchType.LAZY)
    public IPerson getCs_sot() { return cs_sot; }
    public void setCs_sot(IPerson cs_sot) { this.cs_sot = cs_sot; updateName(); }
    
    @FieldTitle(name="Дата назначения")
    @Temporal(TemporalType.DATE)
    public Date getCs_dn() { return cs_dn; }
    public void setCs_dn(Date cs_dn) { this.cs_dn = cs_dn; }
    
    @FieldTitle(name="Дата выбытия")
    @Temporal(TemporalType.DATE)
    public Date getCs_dv() { return cs_dv; }
    public void setCs_dv(Date cs_dv) { this.cs_dv = cs_dv; }
    
    @FieldTitle(name="Департамент")
    @ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getCs_dep() { return cs_dep; }
    public void setCs_dep(IDepartment cs_dep) { this.cs_dep = cs_dep; }
    
    @FieldTitle(name="Список документов")
    @OneToMany(targetEntity=RDocument.class, fetch=FetchType.LAZY)
    public List<RDocument> getCs_docs() { return cs_docs != null ? cs_docs : new ArrayList<RDocument>(); }
    public void setCs_docs(List<RDocument> cs_docs) { this.cs_docs = cs_docs; }
    
    private void updateName() {
    	String name = "";
    	if (getCs_rol() != null) name = getCs_rol().getName();
    	if (getCs_sot() != null) {
    		if (!hs.isEmpty(name)) name += " ";
    		name += cs_sot.getName();
    	}
    	setName(name);
    }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RMember.class;
		ret.add(new ColumnInfo("cs_rol", cl));
		ret.add(new ColumnInfo("cs_sot__name", cl));
		ret.add(new ColumnInfo("cs_dn", cl));
		ret.add(new ColumnInfo("cs_dv", cl));
		ret.add(new ColumnInfo("cs_dep__code", cl));
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
			model.addAttribute("listSp_cs_rol", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_cs_rol"}));
		}
		catch (Exception ex) { }
		return null;
	}
}
