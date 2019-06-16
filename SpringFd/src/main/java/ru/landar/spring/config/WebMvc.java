package ru.landar.spring.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
  
@Configuration
public class WebMvc implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        List<ResourceHandlerRegistration> l = new ArrayList<ResourceHandlerRegistration>();
    	l.add(registry.addResourceHandler("static/jquery/**").addResourceLocations("classpath:/META-INF/resources/webjars/jquery/3.3.1-2/"));
    	l.add(registry.addResourceHandler("static/popper/**").addResourceLocations("classpath:/META-INF/resources/webjars/popper.js/1.14.6/umd/"));
    	l.add(registry.addResourceHandler("static/moment/**").addResourceLocations("classpath:/META-INF/resources/webjars/momentjs/2.24.0/"));
    	l.add(registry.addResourceHandler("static/bootstrap/**").addResourceLocations("classpath:/META-INF/resources/webjars/bootstrap/4.3.1/"));
    	l.add(registry.addResourceHandler("static/bootstrap-datepicker/**").addResourceLocations("classpath:/META-INF/resources/webjars/bootstrap-datepicker/1.8.0/"));
    	l.add(registry.addResourceHandler("static/tempusdominus-bootstrap-4/**").addResourceLocations("classpath:/META-INF/resources/webjars/tempusdominus-bootstrap-4/5.1.2/"));
    	l.add(registry.addResourceHandler("static/jquery-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/jquery-ui/1.12.1/"));
    	l.add(registry.addResourceHandler("static/font-awesome/**").addResourceLocations("classpath:/META-INF/resources/webjars/font-awesome/5.7.2/"));
    	
    	l.add(registry.addResourceHandler("static/css/**").addResourceLocations("classpath:/static/css/"));
    	l.add(registry.addResourceHandler("static/js/**").addResourceLocations("classpath:static/js/"));
    	l.add(registry.addResourceHandler("static/images/**").addResourceLocations("classpath:static/images/"));
    	l.add(registry.addResourceHandler("static/less/**").addResourceLocations("classpath:static/less/"));
    	for (ResourceHandlerRegistration h : l) {
    		CacheControl cacheControl = CacheControl.maxAge(1, TimeUnit.HOURS);
    		h.setCacheControl(cacheControl);
    	}
    }
}