package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpMeasureUnit extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Единица измерения"; }
	public static String multipleTitle() { return "Единицы измерения"; }
	public static String menuTitle() { return multipleTitle(); }
}
