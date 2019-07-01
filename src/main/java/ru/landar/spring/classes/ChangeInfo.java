package ru.landar.spring.classes;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import ru.landar.spring.service.HelperService;

public class ChangeInfo {
	@Autowired
	private HelperService hs;
	Integer rn;
	String clazz;
	Operation op;
	Map<String, Object[]> mapValue;
	public ChangeInfo(Integer rn, String clazz, Operation op, Map<String, Object[]> mapValue) {
		setRn(rn);
		setClazz(clazz);
		setOp(op);
		setValue(mapValue);
	}
	public void setRn(Integer rn) { this.rn = rn; }
	public Integer getRn() { return rn; }
	
	public void setClazz(String clazz) { this.clazz = clazz; }
	public String getClazz() { return clazz; }
	
	public void setOp(Operation op) { this.op = op; }
	public Operation getOp() { return op; }
	
	public void setValue(Map<String, Object[]> map) { mapValue = map; }
	public Map<String, Object[]> getValue() { return mapValue; }
	
	public boolean checkMap() {
		Map<String, Object[]> map = new LinkedHashMap<String, Object[]>();
		if (mapValue != null) mapValue.forEach((attr, os) -> {
			if (os != null && !hs.equals(os[0], os[1])) map.put(attr, os);
		});
		mapValue = map;
		return !mapValue.isEmpty();
	}
}
