package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpLandFeature extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Особенность оборота земли"; }
	public static String multipleTitle() { return "Особенности оборота земли"; }
	public static String menuTitle() { return multipleTitle(); }
}
