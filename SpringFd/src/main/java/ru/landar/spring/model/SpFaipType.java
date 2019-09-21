package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpFaipType extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Тип ФАИП"; }
	public static String multipleTitle() { return "Типы ФАИП"; }
}