package ru.landar.spring.model.fd;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Статус данных акта приема-передачи", multi="Статусы данных акта приема-передачи", voc=true)
public class SpActStatus extends IBase {
}