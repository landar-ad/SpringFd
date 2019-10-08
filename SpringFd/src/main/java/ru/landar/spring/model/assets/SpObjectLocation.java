package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Расположение объекта", multi="Расположения объекта", voc=true)
public class SpObjectLocation extends IBase {
	public static boolean listPaginated() { return true; }
}
