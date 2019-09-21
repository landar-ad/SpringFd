package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpDocStatus extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Статус данных документа"; }
	public static String multipleTitle() { return "Статусы данных документа"; }
	public static String menuTitle() { return multipleTitle(); }
}