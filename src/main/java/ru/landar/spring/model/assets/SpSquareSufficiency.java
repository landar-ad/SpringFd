package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpSquareSufficiency {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Достаточность по площади"; }
	public static String multipleTitle() { return "Достаточность по площади"; }
	public static String menuTitle() { return multipleTitle(); }
}
