package ru.landar.spring.config;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectAround {
	@Pointcut(value="execution(public * ru.landar.spring.model.*.set*(..))")
	void findObj() {
		
	}
	@Around("findObj()")
	public void doAround() {

	}
}
