package ru.landar.spring.config;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectAround {
	@Pointcut(value="execution(public void ru.landar.spring.model.*.set*(..))")
	void setterObj() {
		int a = 0;
	}
	@Around("setterObj()")
	public void doAround() {
		int b = 1;
	}
}
