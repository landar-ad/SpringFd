package ru.landar.spring;

import org.springframework.context.annotation.Scope;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ObjectChanged {
	
}
