package ru.landar.spring;

import java.util.ArrayList;
import java.util.Iterator;
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
		String clazz = (String)hs.getProperty(obj, "clazz");
		if (clazz == null || clazz.equals("ActionLog") || clazz.equals("ISession")) return;
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
			ci = new ChangeInfo(rn, clazz, op, new LinkedHashMap<String, Object[]>());
			l.add(ci);
		}
		final Map<String, Object[]> mapValue = ci.getValue() == null ? new LinkedHashMap<String, Object[]>() : ci.getValue();
		Map<String, Object> mapObj = hs.getMapProperties(obj, true);
		mapObj.forEach((attr, o) -> {
			Object[] os = mapValue.get(attr);
			if (os == null) {
				if (op == Operation.create) os = new Object[] {null, o};
				else if (op == Operation.update) os = new Object[] {o, o};
				else if (op == Operation.delete) os = new Object[] {o, null};
				if (os != null) mapValue.put(attr, os);
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
		String clazz = (String)hs.getProperty(obj, "clazz");
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
			ci = new ChangeInfo(rn, clazz, Operation.update, new LinkedHashMap<String, Object[]>());
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
		map.forEach((rn, l) -> {
			for (ChangeInfo ci : l) {
				if (ci.checkMap()) ret.add(ci);
			}
		});
		return ret;
	}
	public boolean isAttrChanged(Object obj) { return isAttrChanged(obj, null); }
	public boolean isAttrChanged(Object obj, String attr) {
		if (obj == null) return false;
		Integer rn = (Integer)hs.getProperty(obj, "rn");
		if (rn == null) return false;
		List<ChangeInfo> l = map.get(rn);
		if (l == null) return false;
		ChangeInfo ci = null;
		for (ChangeInfo ciT : l) {
			if (ciT.getOp() == Operation.update) {
				ci = ciT;
				break;
			}
		}
		if (ci == null ) return false;
		Map<String, Object[]> mapValue = ci.getValue();
		if (mapValue == null) return false;
		if (!hs.isEmpty(attr)) {
			Object[] os = mapValue.get(attr); 
			if (os == null) return false;
			return !hs.equals(os[0], os[1]);
		}
		Iterator<String> it = mapValue.keySet().iterator();
		while (it.hasNext()) {
			Object[] os = mapValue.get(it.next());
			if (!hs.equals(os[0], os[1])) return true;
		}
		return false;
	}
	public Object getAttrValue(Object obj, String attr, int idx) {
		if (obj == null) return null;
		Integer rn = (Integer)hs.getProperty(obj, "rn");
		if (rn == null) return null;
		if (hs.isEmpty(attr)) return null;
		List<ChangeInfo> l = map.get(rn);
		if (l == null) return null;
		ChangeInfo ci = null;
		for (ChangeInfo ciT : l) {
			if (ciT.getOp() == Operation.update) {
				ci = ciT;
				break;
			}
		}
		if (ci == null ) return null;
		Map<String, Object[]> mapValue = ci.getValue();
		if (mapValue == null) return null;
		Object[] os = mapValue.get(attr);
		if (os == null) return null;
		if (idx < 0 || idx > 1) return null;
		return os[idx];
	}
}
