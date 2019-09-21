package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpAgentType extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип контрагента"; }
	public static String multipleTitle() { return "Типы контрагента"; }
	public static String menuTitle() { return multipleTitle(); }
}
