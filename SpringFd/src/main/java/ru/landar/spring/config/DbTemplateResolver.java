package ru.landar.spring.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import ru.landar.spring.service.HelperService;

public class DbTemplateResolver extends StringTemplateResolver {
	@Autowired 
	HelperService hs;
	public DbTemplateResolver() {
		Set<String> set = new HashSet<String>();
		set.add("*");
		setResolvablePatterns(set);
    }
	@Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
		AutowireHelper.autowire(this);
        String ts = hs.loadTemplate(template);
		return ts != null ? super.computeTemplateResource(configuration, ownerTemplate, ts, templateResolutionAttributes) : null;
    }
}
