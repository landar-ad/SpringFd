package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpStateViewEvent extends IBase {
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "Состояние вида мероприятия"; }
	public static String multipleTitle() { return "Состояния вида мероприятия"; }
}
