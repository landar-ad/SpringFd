package ru.landar.spring.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class AutowireHelper implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private AutowireHelper() {
    }

    public static void autowire(Object classToAutowire) {
        
    	AutowireHelper.applicationContext.getAutowireCapableBeanFactory().autowireBean(classToAutowire);
    }

    public static void autowireClass(Class<?> classToAutowire) {
        
    	AutowireHelper.applicationContext.getAutowireCapableBeanFactory().autowire(classToAutowire, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
    }
    
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
		AutowireHelper.applicationContext = applicationContext;
	}
}
