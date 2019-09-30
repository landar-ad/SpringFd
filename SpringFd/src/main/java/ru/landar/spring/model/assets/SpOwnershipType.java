package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpOwnershipType {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Вид собственности"; }
	public static String multipleTitle() { return "Виды собственности"; }
	public static String menuTitle() { return multipleTitle(); }
}

