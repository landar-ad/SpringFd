package ru.landar.spring.model.purchase;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="КБК", multi="КБК", voc=true)
public class SpKBK extends IBase {
	private String kbk;
	private String kosgu;
	private IDepartment depart;
	
	@FieldTitle(name="КБК")
	@Column(length=20)
	public String getKbk() { return kbk; }
    public void setKbk(String kbk) { this.kbk = kbk; updateCode(); }
    
    @FieldTitle(name="КОСГУ")
    @Column(length=3)
	public String getKosgu() { return kosgu; }
    public void setKosgu(String kosgu) { this.kosgu = kosgu; updateCode(); }
    
    @FieldTitle(name="Код департамента")
    @ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getDepart() { return depart; }
    public void setDepart(IDepartment depart) { this.depart = depart; }
	
    private void updateCode() { 
    	String code = null;
    	if (kbk != null && !kbk.isEmpty()) code = kbk;
    	if (kosgu != null && !kosgu.isEmpty()) {
    		if (code == null || code.isEmpty()) code = "";
    		code += kosgu; 
    	}
    	setCode(code);
    }
	public static boolean listPaginated() { return true; }
	public static List<ColumnInfo> listColumn() {
    	List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
    	Class<?> cl = SpKBK.class;
		ret.add(new ColumnInfo("kbk", cl)); 
		ret.add(new ColumnInfo("kosgu", cl));
		ret.add(new ColumnInfo("depart__code", cl));
		ret.add(new ColumnInfo("name", cl));
		return ret;
	}
}
