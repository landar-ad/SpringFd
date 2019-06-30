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
		l.add(new ChangeInfo(rn, op, hs.getMapProperties(obj, true)));
	}
	public List<ChangeInfo> getObjectChanges() {
		List<ChangeInfo> ret = new ArrayList<ChangeInfo>();
		map.forEach((rn, l) -> {
			ChangeInfo ciCreate = null, ciUpdateStart = null, ciUpdateEnd = null, ciDelete = null;
			for (ChangeInfo ci : l) {
				if (ci.getOp() == Operation.create) {
					ciCreate = ci;
					ciUpdateStart = null;
					ciUpdateEnd = null;
				}
				else if (ci.getOp() == Operation.update) {
					if (ciUpdateStart == null) ciUpdateStart = ci;
					else ciUpdateEnd = ci;
				}
				else if (ci.getOp() == Operation.delete) { ciDelete = ci; break; }
			}
			if (ciCreate != null) {
				ciCreate.buildValueCompared(false);
				ret.add(ciCreate);
			}
			if (ciUpdateStart == null && ciCreate != null) ciUpdateStart = ciCreate;
			if (ciUpdateEnd == null && ciUpdateStart != null) ciUpdateEnd = ciUpdateStart;
			if (ciUpdateStart != null && ciUpdateEnd != null) {
				Map<String, Object[]> mapValueCompared = hs.getMapChanged(ciUpdateStart.getValue(), ciUpdateEnd.getValue());
				if (mapValueCompared != null && !mapValueCompared.isEmpty()) {
					ciUpdateEnd.setValueCompared(mapValueCompared);
					ret.add(ciUpdateEnd);
				}
			}
			if (ciDelete != null) {
				ciDelete.buildValueCompared(true);
				ret.add(ciDelete);
			}
		});
		return ret;
	}
}
