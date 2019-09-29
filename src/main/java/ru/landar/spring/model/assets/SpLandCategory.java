package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpLandCategory {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Категория земель"; }
	public static String multipleTitle() { return "Категории земель"; }
	public static String menuTitle() { return multipleTitle(); }
}
