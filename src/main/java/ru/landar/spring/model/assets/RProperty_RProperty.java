package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.ui.Model;

import ru.landar.spring.model.IBase;
import ru.landar.spring.model.SpCommon;
import ru.landar.spring.service.HelperServiceImpl;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RProperty_RProperty extends IBase {
	private RProperty prop;
	private SpCommon conn_type; 
	
	@ManyToOne(targetEntity=RProperty.class)
    public RProperty getProp() { return prop; }
    public void setProp(RProperty prop) { this.prop = prop; updateName(); }
    
    @ManyToOne(targetEntity=SpCommon.class)
    public SpCommon getConn_type() { return conn_type; }
    public void setConn_type(SpCommon conn_type) { this.conn_type = conn_type; updateName(); }
    
    public static String spCode(String attr) {
		String ret = null;
		if ("conn_type".equals(attr)) ret = "pp_conn_type"; 
		else ret = (String)HelperServiceImpl.invokeStatic(RProperty.class.getSuperclass(), "spCode", attr);
		return ret;
	}
    
    @Override
   	public Object onAddAttributes(Model model, boolean list) {
   		Object ret = super.onAddAttributes(model, list);
   		if (ret != null) return ret;
   		
   		try {
   			model.addAttribute("listConnectionType", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"pp_conn_type"}));
   		}
   		catch (Exception ex) { }
   		return true;
   	}
    
    private void updateName() {
		String name = "";
		if (getParent() != null) name += getParent().getName();
		if (prop != null) {
			if (!name.isEmpty()) name += " <-> ";
			name += prop.getName();
		}
		setName(name);
	}
}
