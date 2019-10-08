package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Рассылка", multi="Рассылки")
public class IMailing extends IBase {
	private IPerson person;

	@FieldTitle(name="Кому")
	@ManyToOne(targetEntity=IPerson.class, fetch=FetchType.LAZY)
    public IPerson getPerson() { return person; }
    public void setPerson(IPerson person) { this.person = person; }
    
    public static List<ColumnInfo> listColumn() {
   		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
   		Class<?> cl = IFile.class;
   		ret.add(new ColumnInfo("person__name", cl, true, true, "*", "select"));
   		return ret;
   	}
    
    @Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listIPerson", objService.findAll(IPerson.class));
		}
		catch (Exception ex) { }
		return true;
	}
}
