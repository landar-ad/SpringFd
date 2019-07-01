package ru.landar.spring.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.servlet.http.Part;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.templateresource.ITemplateResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.landar.spring.classes.AppClassLoader;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SpFileType;

@Component
public class HelperServiceImpl implements HelperService {
	@Autowired
	ObjService objService;
	@Autowired
	UserService userService;
	@Autowired
    private SpringTemplateEngine templateEngine;
	@Autowired
    private AppClassLoader appClassLoader;
	
	@Override
	public boolean isEmpty(String v) { return v == null || v.isEmpty(); }
	@Override
	public boolean isEmptyTrim(String v) { return v == null || v.trim().isEmpty(); }
	@Override
	public Object getObjectByString(String v, Class<?> clType) {
		Object ret = v;
		if (v == null || String.class.isAssignableFrom(clType)) { 
			if (v.isEmpty()) ret = null;
		}
		else if (Integer.class.isAssignableFrom(clType)) { 
			try { ret = Integer.valueOf(v); } catch (Exception ex) { }
		}
		else if (Boolean.class.isAssignableFrom(clType)) {
			ret = new Boolean("on".equals(v) || "1".equals(v) || "yes".equalsIgnoreCase(v) || "true".equalsIgnoreCase(v));
		}
		else if (Long.class.isAssignableFrom(clType)) {
			try { ret = Long.valueOf(v); } catch (Exception ex) { }
		}
		else if (Float.class.isAssignableFrom(clType)) {
			try { ret = Float.valueOf(v); } catch (Exception ex) { }
		}
		else if (Double.class.isAssignableFrom(clType)) {
			try { ret = Double.valueOf(v); } catch (Exception ex) { }
		}
		else if (BigDecimal.class.isAssignableFrom(clType)) {
			v = v.replace(',', '.');
			try { ret = new BigDecimal(v); } catch (Exception ex) { }
		}
		else if (Date.class.isAssignableFrom(clType)) {
			Date d = null;
			try { d = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss").parse(v); } catch (Exception ex) { }
			if (d == null) try { d = new SimpleDateFormat("yyyy-MM-dd'T'HH.mm.ss").parse(v); } catch (Exception ex) { }
			if (d == null) try { d = new SimpleDateFormat("dd.MM.yyyy").parse(v); } catch (Exception ex) { }
			if (d == null) try { d = new SimpleDateFormat("yyyy-MM-dd").parse(v); } catch (Exception ex) { }
			if (d == null) try { d = new SimpleDateFormat("yyyy/MM/dd").parse(v); } catch (Exception ex) { }
			ret = d;
		}
		else if (IBase.class.isAssignableFrom(clType)) {
			try {
				Integer rn = null;
				try { rn = Integer.valueOf(v); } catch (Exception ex) { }
				ret = rn != null ? objService.find(clType, rn) : null;
			}
			catch (Exception ex) { }
		}
		return ret;
	}
	
	@Override
	public Object getObjectByString(Class<?> cl, String attr, String v) {
		if (cl == null || isEmpty(attr)) return v;
		int k = attr.lastIndexOf("__");
		if (k > 0) 	{
			cl = getAttrType(cl, attr.substring(0, k));
			attr = attr.substring(k + 2);
		}
		String getter = "get" + attr.substring(0, 1).toUpperCase() + attr.substring(1);
		try { return getObjectByString(v, cl.getMethod(getter).getReturnType()); } catch (Exception e) { }
		return v;
	}
	@Override
	public Object getObjectByPart(Part part) throws Exception {
		IFile f = null;
		String filesDirectory = (String)objService.getSettings("filesDirectory", "string");
		if (isEmpty(filesDirectory)) filesDirectory = System.getProperty("user.dir") + File.separator + "FILES";
		for (; ;) {
			InputStream is = part.getInputStream();
			if (is == null) break;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			long length = copyStream(is, baos, true, true);
			if (length == 0) break;
			is = new ByteArrayInputStream(baos.toByteArray());
			String filename = part.getSubmittedFileName();
			if (isEmpty(filename)) break;
			f = new IFile();
			String fileext = "", name = filename;
			f.setFilename(filename);
			int k = filename.lastIndexOf('.');
			fileext = k > 0 ? filename.substring(k + 1) : "";
			name = k > 0 ? filename.substring(0, k) : filename;
			f.setFileext(fileext);
			if (!isEmpty(fileext)) {
				SpFileType filetype = (SpFileType)objService.getObjByCode(SpFileType.class, fileext.toLowerCase());
				f.setFiletype(filetype);
			}
			File fd = new File(filesDirectory + new SimpleDateFormat(".yyyy.MM.dd").format(new Date()).replace('.', File.separatorChar));
			fd.mkdirs();
			File ff = new File(fd, new SimpleDateFormat("HHmmss").format(new Date()) + "_" + name + (!fileext.isEmpty() ? "." + fileext : ""));
			f.setFilelength(copyStream(is, new FileOutputStream(ff), true, true));
			f.setFileuri(ff.getAbsolutePath());
			break;
		}
		return f;
	}
	@Override
	public Class<?> getAttrType(Class<?> cl, String attr) {
		Class<?> ret = null;
		if (cl == null || isEmpty(attr)) return ret;
    	for (String a : attr.split("__")) {
    		String getter = "get" + a.substring(0, 1).toUpperCase() + a.substring(1);
    		try { ret = cl.getMethod(getter).getReturnType(); } catch (Exception ex) { }
    		if (ret == null) break;
     		cl = ret;
    	}
		return ret;
	}
	@Override
	public Map<String, Object> getMapProperties(Object obj, boolean persist) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if (obj == null) return map;
		Class<?> cl = obj.getClass();
		Field[] fs = getFields(cl, persist);
		for (Field f: fs) {
			String attr = f.getName();
			if (isEmpty(attr)) continue;
			Class<?> clAttr = getAttrType(cl, attr);
			if (clAttr == null || List.class.isAssignableFrom(clAttr)) continue;
			map.put(attr, getProperty(obj, attr));
		}
		return map;
	}
	@Override
	public Map<String, Object> getMapProperties(Object obj) {
		return getMapProperties(obj, true);
	}
	@Override
	public Map<String, Object[]> getMapChanged(Map<String, Object> mapOld, Map<String, Object> mapNew) {
		Map<String, Object[]> mapChanged = new LinkedHashMap<String, Object[]>();
		for (; ;) {
			if (mapOld == null || mapNew == null) break;
			Iterator<String> it;
			it = mapOld.keySet().iterator();
			while (it.hasNext()) {
				String attr = it.next();
				Object valueOld = mapOld.get(attr), valueNew = mapNew.get(attr);
				if (!equals(valueOld, valueNew)) mapChanged.put(attr, new Object[] {valueOld, valueNew});
			}
			it = mapNew.keySet().iterator();
			while (it.hasNext()) {
				String attr = it.next();
				if (mapChanged.containsKey(attr)) continue;
				Object valueOld = mapOld.get(attr), valueNew = mapNew.get(attr);
				if (!equals(valueOld, valueNew)) mapChanged.put(attr, new Object[] {valueOld, valueNew});
			}
			break;
		}
		return mapChanged;
	}
	@Override
	public Field[] getFields(Class<?> cl, boolean persist) {
		Map<String, Field> map = new LinkedHashMap<String, Field>();
		List<Class<?>> lClasses = new ArrayList<Class<?>>();
		while (cl != null) { 
			lClasses.add(0, cl);
			cl = cl.getSuperclass();
		}
		for (Class<?> clT : lClasses) {
			Field[] fs = null;
			try { fs = clT.getDeclaredFields(); } catch (Exception e) { }
			if (fs != null) {
				for (Field f : fs) {
					String attr = f.getName();
					if (persist) {
						if (getAttrType(clT, attr) == null) continue;
						if (f.isAnnotationPresent(Transient.class)) continue;
					}
					map.put(attr, f);
				}
			}
		}
		List<Field> list = new ArrayList<Field>();
		map.forEach((attr, f) -> list.add(f));
		return list.toArray(new Field[list.size()]);
	}
	@Override
	public void setProperty(Object obj, String attr, Object value) {
		try { 
			Object o = obj;
			String[] as = attr.split("__");
			for (int i=0; i<as.length; i++) {
				String a = as[i];
				String getter = "get" + a.substring(0, 1).toUpperCase() + a.substring(1);
				Method mGet = obj.getClass().getMethod(getter);
				if (i == as.length - 1) {
					String setter = "set" + a.substring(0, 1).toUpperCase() + a.substring(1);
					Method mSet = o.getClass().getMethod(setter, mGet.getReturnType());
					mSet.invoke(o, value);
				}
				else o = mGet.invoke(o);
			}
		} 
		catch (Exception e) { }
	}
	@Override
	public Object getProperty(Object obj, String attr) {
		Object ret = null;
		try { 
			Object o = obj;
			for (String a : attr.split("__")) {
				String getter = "get" + a.substring(0, 1).toUpperCase() + a.substring(1);
				Method mGet = o.getClass().getMethod(getter);
				ret = mGet.invoke(o);
				o = ret;
			}
		} 
		catch (Exception e) { } 
		return ret;
	}
	@Override
	public Object getPropertyJson(Object obj, String attr) {
		Object ret = null;
		try { 
			Class<?> cl = obj.getClass();
			Class<?> clAttr = getAttrType(cl, attr);
			if (clAttr != null) {
				if (IBase.class.isAssignableFrom(clAttr)) {
					ret = getProperty(obj, attr);
				}
				else if (List.class.isAssignableFrom(clAttr)) {
					ret = (List<?>)getProperty(obj, attr);
				}
				else {
					Object o = getProperty(obj, attr);
					if (o != null && o instanceof Date) {
						Date d = (Date)o;
						String s1 = dMy.format(d), s2 = Hms.format(d);
						if (!"00:00:00".equals(s2)) s1 += " " + s2;
						ret = s1;
					}
					else ret = o;
				}
			}
		} 
		catch (Exception e) { } 
		return ret;
	}
	@Override
	public void copyProperties(Object src, Object dest, boolean notNull) {
		if (src == null || dest == null) return;
		Field[] fs = getFields(src.getClass(), true);
		for (Field f : fs) {
			Object o1 = getProperty(src, f.getName());
			if (notNull && o1 == null) continue;
			if (!propertyExists(dest, f.getName())) continue;
			Object o2 = getProperty(dest, f.getName());
			if (notNull && o2 != null) continue;
			setProperty(dest, f.getName(), o1);
		}
	}
	@Override
	public boolean propertyExists(Object obj, String attr) {
		boolean ret = false;
		try { 
			Object o = obj;
			for (String a : attr.split("__")) {
				String getter = "get" + a.substring(0, 1).toUpperCase() + a.substring(1);
				o.getClass().getMethod(getter);
			}
			ret = true;
		} 
		catch (Exception e) { } 
		return ret;
	}
	final SimpleDateFormat dMy = new SimpleDateFormat("dd.MM.yyyy"), dMyHms = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"), Hms = new SimpleDateFormat("HH:mm:ss");
	@Override
	public String getPropertyString(Object obj, String attr) {
		return getObjectString(getProperty(obj, attr));
	}
	@Override
	public String getObjectString(Object o) {
		if (o == null || o instanceof String) return (String)o;
		else if (o instanceof Boolean) return (Boolean)o ? "да" : "нет";
		else if (o instanceof Date) 
		{
			String s1 = dMy.format((Date)o), s2 = Hms.format((Date)o);
			if (!"00:00:00".equals(s2)) s1 += " " + s2;
			return s1; 
		}
		return o.toString();
	}
	@Override
	public boolean equals(Object o1, Object o2)
	{
		if (o1 == null && o2 == null) return true;
		if (o1 == null && o2 != null) return false;
		if (o1 != null && o2 == null) return false;
		if (o1 instanceof IBase) o1 = ((IBase)o1).getRn();
		if (o2 instanceof IBase) o2 = ((IBase)o2).getRn();
		if (o1.equals(o2)) return true;
		if (o1.toString().equals(o2.toString())) return true;
		try
		{
			Method m = o1.getClass().getMethod("compareTo", o2.getClass());
			if (m != null && (Integer)m.invoke(o1, o2) == 0) return true;
		}
		catch (Exception ex) { }
		return false;
	}
	@Override
	public Object getVariable(Object obj, String attr) {
		Object ret = null;
		try {
			String getter = "getVariable";
			Method mGet = obj.getClass().getMethod(getter, String.class);
			ret = mGet.invoke(obj, attr);
		}
		catch (Exception ex) { }
		return ret;
	}
	@Override
	public Object getVariableString(Object obj, String attr) {
		Object o = getVariable(obj, attr);
		if (o == null || o instanceof String) return o;
		else if (o instanceof Boolean) return (Boolean)o ? "да" : "нет";
		else if (o instanceof Date) return attr.indexOf("time") < 0 ? dMy.format((Date)o) : dMyHms.format((Date)o); 
		return o;
	}
	@Override
	public long copyStream(InputStream is, OutputStream os, boolean closeIn, boolean closeOut) throws Exception {
		long ret = 0;
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		int aByte;
	    while ((aByte = bis.read()) != -1) {
	      bos.write(aByte);
	      ret++;
	    }
	    bos.flush();
		if (closeIn) is.close();
		if (closeOut) os.close();
		return ret;
	}
	
	@Override
	public String replaceSpecial(String s) {
		if (s == null || s.isEmpty()) return s;
		StringBuffer buf = new StringBuffer();
		for (int i=0, j=0; i<s.length(); i++) {
			char a = s.charAt(i);
			if (a == '/' ||
				a == '\\' ||
				a == ':' ||
				a == '*' ||
				a == '?' ||
				a == '<' ||
				a == '>' ||
				a == '|' ||
				a == '"') a = '_';
			if (a == ' ')
			{
				j++;
				if (j > 1) continue; 
			}
			else j = 0;
			buf.append(a);
		}
        return buf.toString();
	}
	@Override
	public String loadTemplate(String template) { return (String)objService.getSettings(template, "thymeleaf"); }
	@Override
	public boolean isServerConnected(String url, int timeout) {
		try {
			URL serverUrl = new URL(url);
			URLConnection connection = serverUrl.openConnection();
			connection.setConnectTimeout(timeout);
			connection.connect();
			return true;
		} 
		catch (Exception e) { }
		return false;
	}
	@Override
	public boolean checkRights(Object obj, Operation op) {
		Object o = invoke(obj, "onCheckRights", op);
		return o != null && o instanceof Boolean && (Boolean)o;
	}
	@Override
	public Object invoke(Object obj, String method, Object... args) {
		Method m = getInvokeMethod(obj, method, args);
		if (m == null) return null;
		try { return m.invoke(obj, args); } catch (Exception ex) { } 
		return null;
	}
	@Override
	public Object invokePure(Object obj, String method, Object... args) throws Exception {
		Method m = getInvokeMethod(obj, method, args);
		if (m == null) return null;
		return m.invoke(obj, args); 
	}
	@Override
	public Object invoke(Class<?> cl, String method, Object... args) {
		Method m = getInvokeMethod(cl, method, args);
		if (m == null) return null;
		try { return m.invoke(null, args); } catch (Exception ex) { } 
		return null;
	}
	@Override
	public boolean templateExists(String template) {
		boolean found = false;
		Set<ITemplateResolver> trs = templateEngine.getTemplateResolvers();
		Iterator<ITemplateResolver> it = trs.iterator();
		while (it.hasNext()) {
			ITemplateResolver tr = it.next();
			TemplateResolution te = tr.resolveTemplate(templateEngine.getConfiguration(), null, template, null);
			if (te != null && te.getTemplateResource().exists()) {
				found = true;
				break;
			}
		}
		return found;
	}
	@Override
	public String getTemplateSource(String template) {
		String ret = null;
		Set<ITemplateResolver> trs = templateEngine.getTemplateResolvers();
		Iterator<ITemplateResolver> it = trs.iterator();
		while (it.hasNext()) {
			ITemplateResolver tr = it.next();
			TemplateResolution te = tr.resolveTemplate(templateEngine.getConfiguration(), null, template, null);
			if (te == null) continue;
			ITemplateResource res = te.getTemplateResource();
			if (!res.exists()) continue;
			try {
				Reader r = res.reader();
				StringBuffer cb = new StringBuffer();
				char[] buf = new char[8192];
				while (r.read(buf) > 0) cb.append(buf);
				ret = cb.toString();
				break;
			}
			catch (Exception ex) { }
		}
		return ret;
	}
	private Method getInvokeMethod(Object obj, String method, Object... args) {
		if (obj == null) return null;
		return getInvokeMethod(obj.getClass(), method, args);
	}
	private Method getInvokeMethod(Class<?> cl, String method, Object... args) {
		Method ret = null;
		if (cl == null) return ret;
		Method[] methods = cl.getMethods();
        for (Method m : methods) {
            if (!m.getName().equals(method)) continue;
            Class<?>[] cs = m.getParameterTypes();
            int len = args == null ? 0 : args.length;
            if (cs.length != len) continue;
            int i = 0;
            for (; i<len; i++) {
                if (args[i] == null) continue;
                Class<?> c1 = cs[i], c2 = args[i].getClass();
                if (c2 == null) continue;
                if (c1.isPrimitive()) {
                    try {
                        Field f2 = c2.getField("TYPE"); 
                        if (f2 == null || !c1.equals(f2.get(c2))) break;
                    }
                    catch (Exception ex) { }
                }
                else if (!c1.isAssignableFrom(c2)) break;
            }
            if (i < cs.length) continue;
            ret = m;
            break;
        }
        return ret;
	}
	@Override
	public Class<?> getHandlerClass(String code) {
		Class<?> ret = null;
		for (; ;) {
			ret = appClassLoader.getClass(code);
			if (ret != null) break;
			// Загрузить исходник из настроек
			String source = (String)objService.getSettings(code, "string");
			// Исходный файл
			if (isEmpty(source)) break;
			// Директория для записи результата
			File ft = createTempDirectory("compile");
			if (ft == null) break;
			String tempDir = ft.getAbsolutePath() + File.separator;
			String clazz = code, jFile = clazz + ".java";
			// Файл исходника
			String javaFile = tempDir + File.separator + jFile;
			File f = new File(javaFile);
			try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), "utf8")); 
				writer.write(source);
				writer.close();
			}
			catch (Exception ex) { break; }
			// Параметры компилятора
			String cp = "";
			ClassLoader clo = ClassLoader.getSystemClassLoader();
	        URL[] urls = ((URLClassLoader)clo).getURLs();
	        for(URL url: urls) {
	        	if (!cp.isEmpty()) cp += File.pathSeparator;
	        	cp += url.getFile();
	        }
			String[] param = {"-nowarn", "-encoding", "utf8", "-classpath", cp, "-d", tempDir, javaFile};
			JavaCompiler jc = null;
			try { jc = ToolProvider.getSystemJavaCompiler(); } catch (Exception ex) { }
			if (jc == null) break;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			int status = jc.run(null, os, os, param);	
			if (status != 0) break;
			File compileFolder = new File(tempDir);
			File[] files = compileFolder.listFiles();
			for (File file : files) {
				if (file.isDirectory()) continue;
				String sName = file.getName(); 
				if (sName == null || !sName.endsWith(".class")) continue;
				// Имя класса
				String sClass = sName;
				int k = sName.lastIndexOf('.');
				if (k >= 0) sClass = sName.substring(0, k);
				// Данные
				byte [] b = null;
				try { b = readClassFile(file); } catch (Exception ex) { }
				if (b == null) continue;
				Class<?> cl = appClassLoader.loadFromByte(b, sClass);
				if (clazz.equals(sClass) && cl != null) ret = cl;
			}
			break;
		}
		return ret;
	}
	@Override
	public ObjService getObjService() { return objService; }
	@Override
	public UserService getUserService() { return userService; }
	@Override
	public String getTitleByAttr(String attr)
	{
		String ret = "";
		if ("rn".equals(attr) || "id".equals(attr)) ret = "Идентификатор";
		else if ("code".equals(attr)) ret = "Код";
		else if ("name".equals(attr)) ret = "Наименование";
		else if ("cdate".equals(attr)) ret = "Создан";
		else if ("mdate".equals(attr)) ret = "Изменен";
		else if ("creator".equals(attr)) ret = "Создал";
		else if ("modifier".equals(attr)) ret = "Изменил";
		return ret;
	}
	@Override
	public IDepartment getDepartment() {
		IUser user = userService.getUser((String)null);
		if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
		IPerson person = user.getPerson();
		if (person == null) return null;
		return person.getDepart();
	}
	@Override
	public Integer getDepartmentKey() {
		IDepartment dep = getDepartment();
		return dep == null ? null : dep.getRn();
	}
	@Override
	public boolean checkDepartment(IDepartment depart) {
		IDepartment dep = getDepartment();
		//if (dep != null && "11".equals(dep.getCode())) return true;
		if (dep != null && depart != null && dep.getRn() == depart.getRn()) return true;
		return false;
	}
	@Override
	public boolean checkPerson(IBase base) {
		IUser user = userService.getUser((String)null);
		if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
		IPerson person = user.getPerson();
		return person != null && base != null && person.getRn() == base.getRn();
	}
	@Override
	public Class<?> getClassByName(String clazz) {
		Class<?> ret = null;
		try { ret = (Class<?>)Class.forName(IBase.class.getName().substring(0, IBase.class.getName().lastIndexOf('.') + 1) + clazz); } catch (Exception ex) { }
		return ret;
	}
	@Override
	public Class<?>[] getAllClasses() throws Exception {
		List<Class<?>> l = new ArrayList<Class<?>>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String packageName = IBase.class.getPackage().getName();
		Enumeration<URL> en = classLoader.getResources(packageName.replace('.', '/'));
		while (en.hasMoreElements()) 
	    {  
			String f = en.nextElement().getFile();  
			File[] files = new File(f).listFiles();  
			for (File file : files) 
			{ 
				f = file.getName();
				if (!f.endsWith(".class")) continue;
				l.add(Class.forName(packageName + '.' + f.substring(0, f.length() - 6))); 
			}
	    } 
		return l.toArray(new Class<?>[l.size()]);
	}
	private String[] months = new String[]{"января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"};
	@Override
	public String getFullDate(Date date) {
		if (date == null) return "";
		String month = months[Integer.valueOf(new SimpleDateFormat("MM").format(date)) - 1];
		return new SimpleDateFormat("d").format(date) + " " + month + " " + new SimpleDateFormat("yyyy").format(date);
	}
	@Override
	public String getMonthDate(Date date) {
		if (date == null) return "";
		return months[Integer.valueOf(new SimpleDateFormat("MM").format(date)) - 1];
	}
	@Override
	public String getJsonString(Object obj) throws Exception {
		if (obj == null) return "";
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		getObjectMap(obj, map, new ArrayList<Integer>());
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
	}
	private void  getObjectMap(Object obj, Map<String, Object> map, List<Integer> listRn) throws Exception {
		if (obj == null || map == null || listRn == null) return;
		Integer rn = (Integer)getProperty(obj, "rn");
		if (rn == null) return;
		listRn.add(rn);
		Field[] fs = getFields(obj.getClass(), true);
		for (Field f: fs) {
			String attr = f.getName();
			Object value = getPropertyJson(obj, attr);
			if (value == null) continue;
			Class<?> clAttr = this.getAttrType(obj.getClass(), attr);
			if (clAttr == null) continue;
			if (IBase.class.isAssignableFrom(clAttr)) {
				rn = (Integer)getProperty(value, "rn");
				if (rn == null) continue;
				if (!listRn.contains(rn)) {
					Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
					getObjectMap(value, mapValue, listRn);
					value = mapValue;
				}
				else value = "#" + rn + "#";
			}
			else if (List.class.isAssignableFrom(clAttr)) {
				List<?> l = (List<?>)value;
				List<Object> la = new ArrayList<Object>();
				for (Object o : l) {
					rn = (Integer)getProperty(o, "rn");
					if (rn == null) continue;
					if (!listRn.contains(rn)) {
						Map<String, Object> mapValue = new LinkedHashMap<String, Object>();
						getObjectMap(o, mapValue, listRn);
						la.add(mapValue);
					}
					else la.add("#" + rn + "#");
				}
				value = la;
			}
			map.put(attr, value);
		}
	}
	private static File createTempDirectory(String name) {
		
		File ft = null;
		try { ft = File.createTempFile(name, ""); } catch (Exception ex) { }
		if (ft == null || !ft.delete() || !ft.mkdir()) return null;
		return ft;
	}
	private static byte[] readClassFile(File classFile) throws Exception {
		FileInputStream fis = new FileInputStream(classFile);
		byte [] data = new byte[fis.available()];
		fis.read(data);
		fis.close();
		return data;
	}
	private static void addURLToClassPath(ClassLoader loader, URL u) throws Exception {
		if (loader == null) loader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
		method.setAccessible(true);
		method.invoke(loader, new Object[]{u});
	}
}
