package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpObjectLocation extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Расположение объекта"; }
	public static String multipleTitle() { return "Расположения объекта"; }
	public static String menuTitle() { return multipleTitle(); }
	public static boolean listPaginated() { return true; }
}
