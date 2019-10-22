package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Прикрепленный документ",multi="Прикрепленные документы")
public class Item_RDocument extends IBase {
	private RDocument doc;
	
	@FieldTitle(name="Документ")
	@ManyToOne(targetEntity=RDocument.class)
    public RDocument getDoc() { return doc; }
    public void setDoc(RDocument doc) { this.doc = doc; updateName(); }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = Item_RDocument.class;
		ret.add(new ColumnInfo("doc__name", cl));
		return ret;
	}
    
    private void updateName() {
    	AutowireHelper.autowire(this);
		String name = "";
		IBase p = getParentProxy();
		if (p != null) name += p.getName();
		if (!name.isEmpty()) name += " <-> ";
		if (doc != null) name += doc.getName();
		setName(name);
	}
}
