package ru.landar.spring.model;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.UserService;

@Component
public class IBaseListener {
	@Autowired
	UserService userService;
	@PrePersist
	public void onPrePersist(IBase obj) {
		AutowireHelper.autowire(this);
		String principal = userService.getPrincipal();
		obj.setCreator(principal);
		obj.setModifier(principal);
	}
	@PreUpdate
	public void onPreUpdate(IBase obj) {
		AutowireHelper.autowire(this);
		Date d = new Date();
		obj.setMdate(d);
		obj.setModifier(userService.getPrincipal());
	}
}
