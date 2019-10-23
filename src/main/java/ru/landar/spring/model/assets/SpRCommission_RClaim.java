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
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Связь между типами комиссий и заявок", multi="Связи между типами комиссий и заявок", voc=true)
public class SpRCommission_RClaim extends IBase {
	private SpCommon c_type;
	private SpCommon za_type;
	
	@FieldTitle(name="Код", readOnly=true)
	public String getCode() { return super.getCode(); }
	
	@FieldTitle(name="Тип комиссии", sp="sp_type_c")
	@ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
	public SpCommon getC_type() { return c_type; }
	public void setC_type(SpCommon c_type) { this.c_type = c_type; updateCode(); }
	
	@FieldTitle(name="Тип заявки", sp="sp_type_za")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getZa_type() { return za_type; }
    public void setZa_type(SpCommon za_type) { this.za_type = za_type; updateCode(); }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RCommission.class;
		ret.add(new ColumnInfo("code", cl));
		ret.add(new ColumnInfo("c_type", cl));
		ret.add(new ColumnInfo("za_type", cl));
		return ret;
	}
    
    private void updateCode() {
    	String code = "";
    	if (getC_type() != null && getC_type().getCode() != null) code = getC_type().getCode();
    	if (getZa_type() != null && getZa_type().getCode() != null) {
    		if (!code.isEmpty()) code += "_";
    		code += getZa_type().getCode();
    	}
    	setCode(code);
    }
    
    @Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listSp_type_c", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_type_c"}));
			model.addAttribute("listSp_type_za", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_type_za"}));
		}
		catch (Exception ex) { }
		return null;
	}
}
