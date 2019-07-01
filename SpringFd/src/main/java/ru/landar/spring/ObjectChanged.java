package ru.landar.spring;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import ru.landar.spring.classes.ChangeInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.service.HelperService;

public class ObjectChanged {
	@Autowired
	private HelperService hs;
	
	Map<Integer, List<ChangeInfo>> map;
	public ObjectChanged() { map = new LinkedHashMap<Integer, List<ChangeInfo>>(); }
	public void addObject(Object obj, Operation op) {
		if (obj == null) return;
		Integer rn = (Integer)hs.getProperty(obj, "rn");
		if (rn == null) return;
		List<ChangeInfo> l = map.get(rn);
		if (l == null) {
			l = new ArrayList<ChangeInfo>();
			map.put(rn, l);
		}
		ChangeInfo ci = null;
		for (ChangeInfo ciT : l) {
			if (ciT.getOp() == op) {
				ci = ciT;
				break;
			}
		}
		if (ci == null) {
			ci = new ChangeInfo(rn, op, new LinkedHashMap<String, Object[]>());
			l.add(ci);
		}
		final Map<String, Object[]> mapValue = ci.getValue() == null ? new LinkedHashMap<String, Object[]>() : ci.getValue();
		Map<String, Object> mapObj = hs.getMapProperties(obj, true);
		final boolean bOld = op == Operation.delete || op == Operation.update;
		mapObj.forEach((attr, o) -> {
			Object[] os = mapValue.get(attr);
			if (os == null) {
				os = new Object[] {bOld ? o : null, !bOld ? o : null};
				mapValue.put(attr, os);
			}
			else {
				if (op == Operation.delete) os[0]= o;
				else os[1] = o;
			}
		});
		ci.setValue(mapValue);
	}
	public void addValue(Object obj, String attr, Object value) {
		if (obj == null) return;
		Integer rn = (Integer)hs.getProperty(obj, "rn");
		if (rn == null) return;
		List<ChangeInfo> l = map.get(rn);
		if (l == null) {
			l = new ArrayList<ChangeInfo>();
			map.put(rn, l);
		}
		ChangeInfo ci = null;
		for (ChangeInfo ciT : l) {
			if (ciT.getOp() == Operation.update) {
				ci = ciT;
				break;
			}
		}
		if (ci == null) {
			ci = new ChangeInfo(rn, Operation.update, new LinkedHashMap<String, Object[]>());
			l.add(ci);
		}
		Map<String, Object[]> mapValue = ci.getValue();
		if (mapValue == null) {
			mapValue = new LinkedHashMap<String, Object[]>();
			ci.setValue(mapValue);
		}
		Object[] os = mapValue.get(attr);
		if (os == null) {
			os = new Object[] {hs.getProperty(obj, attr), value};
			mapValue.put(attr, os);
		}
		else os[1] = value;
	}
	public List<ChangeInfo> getObjectChanges() {
		List<ChangeInfo> ret = new ArrayList<ChangeInfo>();
		map.forEach((rn, l) -> ret.addAll(l));
		return ret;
	}
}
