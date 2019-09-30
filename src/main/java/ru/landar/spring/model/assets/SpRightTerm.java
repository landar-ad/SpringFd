package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpRightTerm extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Срок действия права"; }
	public static String multipleTitle() { return "Сроки действия права"; }
	public static String menuTitle() { return multipleTitle(); }
}

