package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
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
@ObjectTitle(single="Связь с объектом имущества", multi="Связи с объектом имущества")
public class RProperty_RProperty extends IBase {
	private RProperty prop;
	private SpCommon conn_type; 
	
	@FieldTitle(name="Объект имущества")
	@ManyToOne(targetEntity=RProperty.class)
    public RProperty getProp() { return prop; }
    public void setProp(RProperty prop) { this.prop = prop; updateName(); }
    
    @FieldTitle(name="Тип связи", sp="pp_conn_type")
    @ManyToOne(targetEntity=SpCommon.class)
    public SpCommon getConn_type() { return conn_type; }
    public void setConn_type(SpCommon conn_type) { this.conn_type = conn_type; updateName(); }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RMeeting_RMember.class;
		ret.add(new ColumnInfo("prop__name", cl));
		ret.add(new ColumnInfo("conn_type", cl));
		return ret;
	}
    
    @Override
   	public Object onAddAttributes(Model model, boolean list) {
   		Object ret = super.onAddAttributes(model, list);
   		if (ret != null) return ret;
   		
   		try {
   			model.addAttribute("listPp_conn_type", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"pp_conn_type"}));
   		}
   		catch (Exception ex) { }
   		return true;
   	}
    
    private void updateName() {
    	AutowireHelper.autowire(this);
		String name = "";
		IBase p = getParentProxy();
		if (p != null) name += p.getName();
		if (prop != null) {
			if (!name.isEmpty()) name += " <-> ";
			name += prop.getName();
		}
		setName(name);
	}
}
