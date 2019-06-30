package ru.landar.spring.classes;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChangeInfo {
	Integer rn;
	Operation op;
	Map<String, Object> mapValue;
	Map<String, Object[]> mapValueCompared;
	public ChangeInfo(Integer rn, Operation op, Map<String, Object> mapValue) {
		setRn(rn);
		setOp(op);
		setValue(mapValue);
		setValueCompared(null);
	}
	public void setRn(Integer rn) { this.rn = rn; }
	public Integer getRn() { return rn; }
	
	public void setOp(Operation op) { this.op = op; }
	public Operation getOp() { return op; }
	
	public void setValue(Map<String, Object> map) { mapValue = map; }
	public Map<String, Object> getValue() { return mapValue; }
	
	public void setValueCompared(Map<String, Object[]> map) { mapValueCompared = map; }
	public Map<String, Object[]> getValueCompared() { return mapValueCompared; }
	
	public void buildValueCompared(boolean bNew) {
		if (mapValue == null) return;
		mapValueCompared = new LinkedHashMap<String, Object[]>();
		mapValue.forEach((key, value) -> {
			mapValueCompared.put(key, new Object[] {bNew ? value : null, !bNew ? null : value});
		});
	}
}
