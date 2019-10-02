package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpPropertyDivision extends IBase{
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Раздел учета"; }
	public static String multipleTitle() { return "Разделы учета"; }
	public static String menuTitle() { return multipleTitle(); }
}
