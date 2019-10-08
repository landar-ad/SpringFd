package ru.landar.spring.model.fd;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Статус данных реестра", multi="Статусы данных реестра", voc=true)
public class SpReestrStatus extends IBase {
}