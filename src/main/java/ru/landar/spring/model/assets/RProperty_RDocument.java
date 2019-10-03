package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.model.IBase;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RProperty_RDocument extends IBase {
	private RDocument doc;
	
	@ManyToOne(targetEntity=RDocument.class)
    public RDocument getDoc() { return doc; }
    public void setDoc(RDocument doc) { this.doc = doc; updateName(); }
    
    private void updateName() {
		String name = "";
		if (getParent() != null) name += getParent().getName();
		if (doc != null) {
			if (!name.isEmpty()) name += " <-> ";
			name += doc.getName();
		}
		setName(name);
	}
}
