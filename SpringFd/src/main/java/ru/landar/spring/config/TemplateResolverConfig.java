package ru.landar.spring.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class TemplateResolverConfig {
    @Autowired
    private SpringTemplateEngine templateEngine;
    @PostConstruct
    public void addResolver() {
    	StringTemplateResolver resolver = new HtmlTemplateResolver();
    	resolver.setTemplateMode("HTML");
        resolver.setOrder(0);
        resolver.setCacheable(false);
        templateEngine.addTemplateResolver(resolver);
        
        resolver = new DbTemplateResolver();
    	resolver.setTemplateMode("HTML");
        resolver.setOrder(1);
        resolver.setCacheable(false);
        templateEngine.addTemplateResolver(resolver);
    }
}
