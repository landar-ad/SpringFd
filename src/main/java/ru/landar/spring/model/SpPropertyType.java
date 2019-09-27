package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpPropertyType extends IBase{
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип файла"; }
	public static String multipleTitle() { return "Типы файла"; }
	public static String menuTitle() { return multipleTitle(); }
}
