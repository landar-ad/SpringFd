package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpObjectPurpose extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Назначение объекта"; }
	public static String multipleTitle() { return "Назначения объекта"; }
	public static String menuTitle() { return multipleTitle(); }
}
