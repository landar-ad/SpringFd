package ru.landar.spring.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IMailing extends IBase {
	private IPerson person;
	
}