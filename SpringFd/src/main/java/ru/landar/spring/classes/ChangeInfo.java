package ru.landar.spring.classes;

import java.util.Map;

public class ChangeInfo {
	Integer rn;
	Operation op;
	Map<String, Object[]> mapValue;
	public ChangeInfo(Integer rn, Operation op, Map<String, Object[]> mapValue) {
		setRn(rn);
		setOp(op);
		setValue(mapValue);
	}
	public void setRn(Integer rn) { this.rn = rn; }
	public Integer getRn() { return rn; }
	
	public void setOp(Operation op) { this.op = op; }
	public Operation getOp() { return op; }
	
	public void setValue(Map<String, Object[]> map) { mapValue = map; }
	public Map<String, Object[]> getValue() { return mapValue; }
}
