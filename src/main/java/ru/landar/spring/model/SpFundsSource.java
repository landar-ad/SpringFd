package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpFundsSource extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Источник финансирования"; }
	public static String multipleTitle() { return "Источники финансирования"; }
	public static String menuTitle() { return multipleTitle(); }
}