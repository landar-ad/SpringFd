package ru.landar.spring.model;

import javax.persistence.PreUpdate;
import javax.persistence.PrePersist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;

@Component
public class UnfinishedConstructionListener {
	
	@Autowired
	ObjService objService;
	
	@Autowired
	HelperService hs;
	
	@PrePersist
	public void onPreCreate(UnfinishedConstruction obj) {
		
		AutowireHelper.autowire(this);
	}
	
	@PreUpdate
	public void onPreUpdate(UnfinishedConstruction obj) {
		
		AutowireHelper.autowire(this);
		
	}
}
