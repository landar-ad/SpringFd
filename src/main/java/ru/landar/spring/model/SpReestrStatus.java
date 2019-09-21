package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpReestrStatus extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Статус данных реестра"; }
	public static String multipleTitle() { return "Статусы данных реестра"; }
	public static String menuTitle() { return multipleTitle(); }
}