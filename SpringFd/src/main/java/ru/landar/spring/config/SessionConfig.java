package ru.landar.spring.config;

import java.util.Date;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.landar.spring.model.ISession;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@Configuration
public class SessionConfig {
	@Autowired
	UserService userService;
	@Autowired
	ObjService objService;
	@Autowired
	HelperService hs;
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {               
             	se.getSession().setMaxInactiveInterval(60 * 60);
            }
            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
            	String sessionId = se.getSession().getId();
            	if (!hs.isEmpty(sessionId)) {
	            	ISession session = (ISession)objService.find(ISession.class, "id", sessionId);
	            	if (session != null) {
	            		session.setEnd_time(new Date());
	            		objService.updateObj(session);
	            	}
            	}
            }
        };
    }
    @Bean
    public HttpSessionAttributeListener httpSessionAttributeListener() {
        return new HttpSessionAttributeListener() {
            @Override
            public void attributeAdded(HttpSessionBindingEvent se) {
            	String login = userService.getPrincipal(), sessionId = se.getSession().getId();
            	if (!hs.isEmpty(login) && !hs.isEmpty(sessionId)) {
            		ISession session = (ISession)objService.find(ISession.class, "id", sessionId);
            		if (session == null) {
                		session = new ISession();
                    	session.setId(sessionId);
                    	session.setLogin(login);
                    	session.setIp((String)se.getSession().getAttribute("ip"));
                    	session.setStart_time(new Date());
                    	objService.createObj(session);
                	}
            	}
            }
            @Override
            public void attributeRemoved(HttpSessionBindingEvent se) { 
            }
            @Override
            public void attributeReplaced(HttpSessionBindingEvent se) {
            }
        };
    }
}
