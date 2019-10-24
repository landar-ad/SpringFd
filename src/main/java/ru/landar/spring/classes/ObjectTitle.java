package ru.landar.spring.classes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ObjectTitle {
	public String single() default "";
	public String multi() default "";
	public String menu() default "";
	public boolean voc() default false;
	public boolean system() default false;
}