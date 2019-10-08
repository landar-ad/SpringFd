package ru.landar.spring.model.fd;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Статус данных документа", multi="Статусы данных документа", voc=true)
public class SpDocStatus extends IBase {
}