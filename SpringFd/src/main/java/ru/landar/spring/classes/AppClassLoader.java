package ru.landar.spring.classes;

import java.util.Hashtable;

public class AppClassLoader extends ClassLoader {
	private Hashtable<String, Class<?>> cache;
	private ClassLoader currentLoader;
	public AppClassLoader() {
		cache = new Hashtable<String, Class<?>>();
		currentLoader = null;
    }
	public Class<?> getClass(String className) {
		return cache.get(className);
	}
	protected Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> ret = cache.get(className);
        if (ret != null) return ret; 
        if (currentLoader != null) ret =  currentLoader.loadClass(className);
        if (ret != null) return ret;
        ret =  Thread.currentThread().getContextClassLoader().loadClass(className);
        if (ret != null) return ret;
        throw new ClassNotFoundException(); 
    }
	public Class<?> loadFromByte(byte [] b, String className) {
		Class<?> ret = getClass(className);
		if (ret != null) return ret;
		try { 
			ret = super.defineClass(className, b, 0, b.length);
			if (ret != null) cache.put(className, ret);
		} 
		catch (Exception e) { }
		return ret;
	}
	public void removeClass(String className) {
		cache.remove(className);
	}
	public void setCurrentThreadClassLoader() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader instanceof AppClassLoader) return;
        else {
            currentLoader = loader;
            Thread.currentThread().setContextClassLoader(this);
        }
    }
}
