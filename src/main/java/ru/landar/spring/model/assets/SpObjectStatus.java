package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpObjectStatus extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Статус данных объекта"; }
	public static String multipleTitle() { return "Статусы данных объекта"; }
	public static String menuTitle() { return multipleTitle(); }
}