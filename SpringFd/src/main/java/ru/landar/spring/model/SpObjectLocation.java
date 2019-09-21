package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpObjectLocation extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Расположение объекта"; }
	public static String multipleTitle() { return "Расположения объекта"; }
	public static boolean listPaginated() { return true; }
}
