package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpFaipType extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип ФАИП"; }
	public static String multipleTitle() { return "Типы ФАИП"; }
	public static String menuTitle() { return multipleTitle(); }
}