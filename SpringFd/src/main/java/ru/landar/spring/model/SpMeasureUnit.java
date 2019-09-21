package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpMeasureUnit extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Единица измерения"; }
	public static String multipleTitle() { return "Единицы измерения"; }
}
