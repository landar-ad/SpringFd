package ru.landar.spring.classes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FieldTitle {
	// Заголовок общий
	public String name() default "";
	public String sp() default "";
	// Для таблицы
	// Заголовок колонки в таблице
	public String nameColumn() default "*";
	// Видимость
	public boolean visible() default true;
	// Сортировка
	public boolean sortable() default true;
	// Фильтрация
	public String filter() default "*";
	// Тип элемента ввода для фильтра
	public String filterType() default "*";
	// Для типа select - список заполнения
	public String filterList() default "*";
	// Для типа select - атрибут для выбора значения
	public String filterAttr() default "*";
	// Для редактирования
	// Заголовок поля
	public String nameField() default "*";
	// Тип элемента редактирования
	public String editType() default "*";
	// Для типа select - список заполнения
	public String attrList() default "*";
	// Обязательное поле
	public boolean required() default false;
	// Ширина поля (1-12)
	public int editLength() default 0;
	// Нередактируемое поле (если true)
	public boolean readOnly() default false;
}
