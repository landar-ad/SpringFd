package ru.landar.spring.config;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

public class HtmlTemplateResolver  extends StringTemplateResolver {
	public HtmlTemplateResolver() {
		Set<String> set = new HashSet<String>();
		set.add("*");
		setResolvablePatterns(set);
    }
	@Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, Map<String, Object> templateResolutionAttributes) {
		return template.indexOf("<html") >= 0 &&  template.indexOf("www.thymeleaf.org") >= 0 
				? super.computeTemplateResource(configuration, ownerTemplate, template, templateResolutionAttributes) 
				: null;
    }
}
