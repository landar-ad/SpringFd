package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class SpKBK extends IBase {
	private String kbk;
	private String kosgu;
	private IDepartment depart;
	
	@Column(length=20)
	public String getKbk() { return kbk; }
    public void setKbk(String kbk) { this.kbk = kbk; updateCode(); }
    
    @Column(length=3)
	public String getKosgu() { return kosgu; }
    public void setKosgu(String kosgu) { this.kosgu = kosgu; updateCode(); }
    
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
    
	public static boolean isVoc() { return true; }
	public static String singleTitle() { return "КБК"; }
	public static String multipleTitle() { return "КБК"; }
	public static List<ColumnInfo> listColumn() {
    	List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("kbk", "КБК")); 
		ret.add(new ColumnInfo("kosgu", "КОСГУ"));
		ret.add(new ColumnInfo("depart__code", "Код департамента"));
		ret.add(new ColumnInfo("name", "Наименование"));
		return ret;
	}
}
