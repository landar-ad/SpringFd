package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpPropertyDivision extends IBase{
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип имущества"; }
	public static String multipleTitle() { return "Типы имущества"; }
	public static String menuTitle() { return multipleTitle(); }
}
