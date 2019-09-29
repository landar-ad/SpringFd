package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class RLand extends RProperty {
	private String сad_num;
	private Date cad_date;
	private Integer area;
	private String fp_reg_num;
	private Date fp_in_date;
	private Date fp_out_date;
	private String address;
	private Integer dist_ns;
	private SpLandCategory category;
	private String usage;
	
	@Column(length=18)
    public String getCad_num() { return сad_num; }
    public void setCad_num(String сad_num) { this.сad_num = сad_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getCad_date() { return cad_date; }
    public void setCad_date(Date cad_date) { this.cad_date = cad_date; }
    
    public Integer getArea() { return area; }
    public void setArea(Integer area) { this.area = area; }
    
    @Column(length=12)
    public String getFp_reg_num() { return fp_reg_num; }
    public void setFp_reg_num(String fp_reg_num) { this.fp_reg_num = fp_reg_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getFp_in_date() { return fp_in_date; }
    public void setFp_in_date(Date fp_in_date) { this.fp_in_date = fp_in_date; }
    
    @Temporal(TemporalType.DATE)
    public Date getFp_out_date() { return fp_out_date; }
    public void setFp_out_date(Date fp_out_date) { this.fp_out_date = fp_out_date; }
    
    @Column(length=512)
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Integer getDist_ns() { return dist_ns; }
    public void setDist_ns(Integer dist_ns) { this.dist_ns = dist_ns; }
    
    @ManyToOne(targetEntity=SpLandCategory.class, fetch=FetchType.LAZY)
    public SpLandCategory getCategory() { return category; }
    public void setCategory(SpLandCategory category) { this.category = category; }
    
    @Column(length=1024)
    public String getUsage() { return usage; }
    public void setUsage(String usage) { this.usage = usage; }
	
	public static String singleTitle() { return "Земельный участок"; }
	public static String multipleTitle() { return "Земельные участки"; }
	public static String menuTitle() { return multipleTitle(); }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("org__name", "Подвед, учреждение")); 
		ret.add(new ColumnInfo("inv_number", "Инвентарный номер"));
		ret.add(new ColumnInfo("cad_num", "Кадастровый номер"));
		ret.add(new ColumnInfo("cad_date", "Дата постановки на кадастровый учет"));
		ret.add(new ColumnInfo("area", "Площадь земельного участка, кв.м"));
		ret.add(new ColumnInfo("fp_reg_num", "РНФИ"));
		ret.add(new ColumnInfo("fp_in_date", "Дата РНФИ"));
		ret.add(new ColumnInfo("fp_out_date", "Дата выбытия"));
		ret.add(new ColumnInfo("address", "Полный адрес"));
		ret.add(new ColumnInfo("dist_ns", "Расстояние до ближайшего населенного пункта, м"));
		ret.add(new ColumnInfo("category__name", "Категория земель", true, true, "category__rn", "select", "listLandCategory"));
		ret.add(new ColumnInfo("usage", "Разрешенное использование (назначение)"));
		
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	
    	return ret;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listLandCategory", objService.findAll(SpLandCategory.class));
			
		}
		catch (Exception ex) { }
		return true;
	}
}
