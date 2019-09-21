package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpActionType extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип действия"; }
	public static String multipleTitle() { return "Типы действия"; }
	public static String menuTitle() { return multipleTitle(); }
}
