package ru.landar.spring.model.assets;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IDepartment;
import ru.landar.spring.model.IFile;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SearchContent;
import ru.landar.spring.model.SpCommon;
import ru.landar.spring.model.fd.SpDocType;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.HelperServiceImpl;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;

@Entity
@EntityListeners(UnfinishedConstructionListener.class)
@PrimaryKeyJoinColumn(name="rn")
public class UnfinishedConstruction extends IBase {
	
	private IOrganization customer;
	private String obj_name;
	private String obj_address;
	private BigDecimal latitude; 
	private BigDecimal longtitude; 
	private SpCommon purpose;
	private BigDecimal obj_capacity;
	private SpCommon obj_capacity_ei;
	private BigDecimal estimated_cost;
	private Integer year_start;
	private Integer year_stop;
	private String doc_basis_fbfund;
	private BigDecimal act_cost_real;
	private BigDecimal act_cost_real_iefb;
	private BigDecimal remain_est_cost; 
	private BigDecimal safund_comcon; 
	private BigDecimal safund_comcon_iefb; 
	private BigDecimal safund_comcon_ievs; 
	private SpCommon safund_pres_obj_source;
	private BigDecimal safund_pres_obj;
	private SpCommon safund_destr_source;
	private BigDecimal safund_destr;
	private String imp_period_event;
	private String prop_rec_object;
	private String rat_writing_obj;
	private String presence_dpd;
	private String prop_sol_dpd;
	private IDepartment depart;
	private SpObjectLocation locobj;
	private SpViewEvent view_event;
	private SpCommon state_view_event;
	private String agnname1;
	private String emppost1;
	private String phone_s1;
	private String phone_m1;
	private String email1;
	private String agnname2;
	private String emppost2;
	private String phone_s2;
	private String phone_m2;
	private String email2;
	private Boolean req_trip; 
	private SpCommon status;
	private SpCompletionPhase complet_build_phs; 
	private BigDecimal complet_build_val;
	private String ident_obj;
	private Boolean faip;
	private SpCommon type_faip;
	private String num_faip; 
	private Date date_faip; 
	private Boolean bs_source;
	private String bs_num; 
	private Date bs_date; 
	private Boolean vs_source;
	private String vs_num;
	private Date vs_date; 
	private Boolean ii_source;
	private String ii_num; 
	private Date ii_date;
	private IFile file_zag;
	private String regnum_fedprop;
	private String regnum_zu;
	private String kadnum_obj;
	private String kadnum_zu;
	private String prop_sol_prov; 
	private Boolean vvod_expl; 
	private List<RDocument> documents;
	
    @Column(length=2000)
    public String getObj_name() { return obj_name; }
    public void setObj_name(String obj_name) { this.obj_name = obj_name; setName(obj_name); }
    
    @Column(length=2000)
    public String getObj_address() { return obj_address; }
    public void setObj_address(String obj_address) { this.obj_address = obj_address; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getObj_capacity() { return obj_capacity; }
    public void setObj_capacity(BigDecimal obj_capacity) { this.obj_capacity = obj_capacity; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getEstimated_cost() { return estimated_cost; }
    public void setEstimated_cost(BigDecimal estimated_cost) { this.estimated_cost = estimated_cost; }
    
    public Integer getYear_start() { return year_start; }
    public void setYear_start(Integer year_start) { this.year_start = year_start; }
    
    public Integer getYear_stop() { return year_stop; }
    public void setYear_stop(Integer year_stop) { this.year_stop = year_stop; }
    
    @Column(length=2000)
    public String getDoc_basis_fbfund() { return doc_basis_fbfund; }
    public void setDoc_basis_fbfund(String doc_basis_fbfund) { this.doc_basis_fbfund = doc_basis_fbfund; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getAct_cost_real() { return act_cost_real; }
    public void setAct_cost_real(BigDecimal act_cost_real) { this.act_cost_real = act_cost_real; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getAct_cost_real_iefb() { return act_cost_real_iefb; }
    public void setAct_cost_real_iefb(BigDecimal act_cost_real_iefb) { this.act_cost_real_iefb = act_cost_real_iefb; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getRemain_est_cost() { return remain_est_cost; }
    public void setRemain_est_cost(BigDecimal remain_est_cost) { this.remain_est_cost = remain_est_cost; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getSafund_comcon() { return safund_comcon; }
    public void setSafund_comcon(BigDecimal safund_comcon) { this.safund_comcon = safund_comcon; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getSafund_comcon_iefb() { return safund_comcon_iefb; }
    public void setSafund_comcon_iefb(BigDecimal safund_comcon_iefb) { this.safund_comcon_iefb = safund_comcon_iefb; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getSafund_comcon_ievs() { return safund_comcon_ievs; }
    public void setSafund_comcon_ievs(BigDecimal safund_comcon_ievs) { this.safund_comcon_ievs = safund_comcon_ievs; }
    
    @Column(length=256)
    public String getImp_period_event() { return imp_period_event; }
    public void setImp_period_event(String imp_period_event) { this.imp_period_event = imp_period_event; }

    @Column(precision=17, scale=2)
    public BigDecimal getSafund_pres_obj() { return safund_pres_obj; }
    public void setSafund_pres_obj(BigDecimal safund_pres_obj) { this.safund_pres_obj = safund_pres_obj; }
    
    @Column(length=200)
    public String getProp_rec_object() { return prop_rec_object; }
    public void setProp_rec_object(String prop_rec_object) { this.prop_rec_object = prop_rec_object; }
    
    @Column(length=600)
    public String getRat_writing_obj() { return rat_writing_obj; }
    public void setRat_writing_obj(String rat_writing_obj) { this.rat_writing_obj = rat_writing_obj; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getSafund_destr() { return safund_destr; }
    public void setSafund_destr(BigDecimal safund_destr) { this.safund_destr = safund_destr; }
    
    @Column(length=400)
    public String getPresence_dpd() { return presence_dpd; }
    public void setPresence_dpd(String presence_dpd) { this.presence_dpd = presence_dpd; }
    
    @Column(length=600)
    public String getProp_sol_dpd() { return prop_sol_dpd; }
    public void setProp_sol_dpd(String prop_sol_dpd) { this.prop_sol_dpd = prop_sol_dpd; }
    
    @ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getCustomer() { return customer; }
    public void setCustomer(IOrganization customer) { this.customer = customer; }
    
    @ManyToOne(targetEntity=IDepartment.class, fetch=FetchType.LAZY)
    public IDepartment getDepart() { return depart; }
    public void setDepart(IDepartment depart) { this.depart = depart; }
    
    @ManyToOne(targetEntity=SpObjectLocation.class, fetch=FetchType.LAZY)
    public SpObjectLocation getLocobj() { return locobj; }
    public void setLocobj(SpObjectLocation locobj) { this.locobj = locobj; }
    
    @ManyToOne(targetEntity=SpViewEvent.class, fetch=FetchType.LAZY)
    public SpViewEvent getView_event() { return view_event; }
    public void setView_event(SpViewEvent view_event) { this.view_event = view_event; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getState_view_event() { return state_view_event; }
    public void setState_view_event(SpCommon state_view_event) { this.state_view_event = state_view_event; }
    
    @Column(length=120)
    public String getAgnname1() { return agnname1; }
    public void setAgnname1(String agnname1) { this.agnname1 = agnname1; }
    
    @Column(length=160)
    public String getEmppost1() { return emppost1; }
    public void setEmppost1(String emppost1) { this.emppost1 = emppost1; }
    
    @Column(length=40)
    public String getPhone_s1() { return phone_s1; }
    public void setPhone_s1(String phone_s1) { this.phone_s1 = phone_s1; }
    
    @Column(length=40)
    public String getPhone_m1() { return phone_m1; }
    public void setPhone_m1(String phone_m1) { this.phone_m1 = phone_m1; }
    
    @Column(length=80)
    public String getEmail1() { return email1; }
    public void setEmail1(String email1) { this.email1 = email1; }
    
    @Column(length=120)
    public String getAgnname2() { return agnname2; }
    public void setAgnname2(String agnname2) { this.agnname2 = agnname2; }
    
    @Column(length=160)
    public String getEmppost2() { return emppost2; }
    public void setEmppost2(String emppost2) { this.emppost2 = emppost2; }
    
    @Column(length=40)
    public String getPhone_s2() { return phone_s2; }
    public void setPhone_s2(String phone_s2) { this.phone_s2 = phone_s2; }
    
    @Column(length=40)
    public String getPhone_m2() { return phone_m2; }
    public void setPhone_m2(String phone_m2) { this.phone_m2 = phone_m2; }
    
    @Column(length=80)
    public String getEmail2() { return email2; }
    public void setEmail2(String email2) { this.email2 = email2; }
    
    public Boolean getReq_trip() { return req_trip; }
    public void setReq_trip(Boolean req_trip) { this.req_trip = req_trip; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getStatus() { return status; }
    public void setStatus(SpCommon status) { this.status = status; }
    
    @ManyToOne(targetEntity=SpCompletionPhase.class, fetch=FetchType.LAZY)
    public SpCompletionPhase getComplet_build_phs() { return complet_build_phs; }
    public void setComplet_build_phs(SpCompletionPhase complet_build_phs) { this.complet_build_phs = complet_build_phs; }
    
    @Column(precision=17, scale=2)
    public BigDecimal getComplet_build_val() { return complet_build_val; }
    public void setComplet_build_val(BigDecimal complet_build_val) { this.complet_build_val = complet_build_val; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getObj_capacity_ei() { return obj_capacity_ei; }
    public void setObj_capacity_ei(SpCommon obj_capacity_ei) { this.obj_capacity_ei = obj_capacity_ei; }
    
    @Column(length=20)
    public String getIdent_obj() { return ident_obj; }
    public void setIdent_obj(String ident_obj) { this.ident_obj = ident_obj; }
    
    public Boolean getFaip() { return faip; }
    public void setFaip(Boolean faip) { this.faip = faip; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getType_faip() { return type_faip; }
    public void setType_faip(SpCommon type_faip) { this.type_faip = type_faip; }
    
    @Column(length=40)
    public String getNum_faip() { return num_faip; }
    public void setNum_faip(String num_faip) { this.num_faip = num_faip; }
    
    @Temporal(TemporalType.DATE)
    public Date getDate_faip() { return date_faip; }
    public void setDate_faip(Date date_faip) { this.date_faip = date_faip; }
    
    public Boolean getBs_source() { return bs_source; }
    public void setBs_source(Boolean bs_source) { this.bs_source = bs_source; }
    
    @Column(length=100)
    public String getBs_num() { return bs_num; }
    public void setBs_num(String bs_num) { this.bs_num = bs_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getBs_date() { return bs_date; }
    public void setBs_date(Date bs_date) { this.bs_date = bs_date; }
    
    public Boolean getVs_source() { return vs_source; }
    public void setVs_source(Boolean vs_source) { this.vs_source = vs_source; }
    
    @Column(length=100)
    public String getVs_num() { return vs_num; }
    public void setVs_num(String vs_num) { this.vs_num = vs_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getVs_date() { return vs_date; }
    public void setVs_date(Date vs_date) { this.vs_date = vs_date; }
    
    public Boolean getIi_source() { return ii_source; }
    public void setIi_source(Boolean ii_source) { this.ii_source = ii_source; }
    
    @Column(length=100)
    public String getIi_num() { return ii_num; }
    public void setIi_num(String ii_num) { this.ii_num = ii_num; }
    
    @Temporal(TemporalType.DATE)
    public Date getIi_date() { return ii_date; }
    public void setIi_date(Date ii_date) { this.ii_date = ii_date; }
    
    @ManyToOne(targetEntity=IFile.class, fetch=FetchType.LAZY)
    public IFile getFile_zag() { return file_zag; }
    public void setFile_zag(IFile file_zag) { this.file_zag = file_zag; }
    
    @Column(precision=17, scale=7)
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    @Column(precision=17, scale=7)
    public BigDecimal getLongtitude() { return longtitude; }
    public void setLongtitude(BigDecimal longtitude) { this.longtitude = longtitude; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSafund_pres_obj_source() { return safund_pres_obj_source; }
    public void setSafund_pres_obj_source(SpCommon safund_pres_obj_source) { this.safund_pres_obj_source = safund_pres_obj_source; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getSafund_destr_source() { return safund_destr_source; }
    public void setSafund_destr_source(SpCommon safund_destr_source) { this.safund_destr_source = safund_destr_source; }
    
    @Column(length=120)
    public String getRegnum_fedprop() { return regnum_fedprop; }
    public void setRegnum_fedprop(String regnum_fedprop) { this.regnum_fedprop = regnum_fedprop; }
    
    @Column(length=120)
    public String getRegnum_zu() { return regnum_zu; }
    public void setRegnum_zu(String regnum_zu) { this.regnum_zu = regnum_zu; }
    
    @Column(length=120)
    public String getKadnum_obj() { return kadnum_obj; }
    public void setKadnum_obj(String kadnum_obj) { this.kadnum_obj = kadnum_obj; }
    
    @Column(length=120)
    public String getKadnum_zu() { return kadnum_zu; }
    public void setKadnum_zu(String kadnum_zu) { this.kadnum_zu = kadnum_zu; }
    
    @Column(length=600)
    public String getProp_sol_prov() { return prop_sol_prov; }
    public void setProp_sol_prov(String prop_sol_prov) { this.prop_sol_prov = prop_sol_prov; }
    
    public Boolean getVvod_expl() { return vvod_expl; }
    public void setVvod_expl(Boolean vvod_expl) { this.vvod_expl = vvod_expl; }
    
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getPurpose() { return purpose; }
    public void setPurpose(SpCommon purpose) { this.purpose = purpose; }
    
    @ManyToMany(targetEntity=RDocument.class, cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    public List<RDocument> getDocuments() { return documents; }
    public void setDocuments(List<RDocument> documents) { this.documents = documents; }
    
    @Autowired
	UserService userService;
	@Autowired
	ObjService objService;
    @Autowired
	HelperService hs;
    @Autowired
	ObjRepositoryCustom objRepository;
	
 	public static String singleTitle() { return "Объект незавершенного капстроительства"; }
	public static String multipleTitle() { return "Объекты незавершенного капстроительства"; }
	public static String menuTitle() { return "Незавершенное капстроительство"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("customer__name", "Учреждение")); 
		ret.add(new ColumnInfo("status__name", "Статус", true, true, "status__rn", "select", "listObjectStatus"));
		ret.add(new ColumnInfo("obj_name", "Наименование объекта"));
		ret.add(new ColumnInfo("ident_obj", "Идентификатор объекта"));
		ret.add(new ColumnInfo("locobj__name", "Расположение объекта", true, true, "locobj__rn", "select", "listObjectLocation"));
		ret.add(new ColumnInfo("obj_address", "Адрес объекта", false));
		ret.add(new ColumnInfo("latitude", "Широта", false));
		ret.add(new ColumnInfo("longtitude", "Долгота", false));
		ret.add(new ColumnInfo("purpose__name", "Назначение объекта", false, true, "purpose__rn", "select", "listPurpose"));
		ret.add(new ColumnInfo("obj_capacity", "Мощность объекта", false));
		ret.add(new ColumnInfo("obj_capacity_ei__name", "Единица измерения", false, true, "obj_capacity_ei__rn", "select", "listMeasureUnit"));
		ret.add(new ColumnInfo("view_event__name", "Мероприятие", true, true, "view_event__rn", "select", "listViewEvent"));
		ret.add(new ColumnInfo("state_view_event__name", "Состояние мероприятия", false, true, "state_view_event__rn", "select", "listStateViewEvent"));
		ret.add(new ColumnInfo("estimated_cost", "Сметная стоимость (тыс.руб.)"));
		ret.add(new ColumnInfo("year_start", "Начало строительства (год)"));
		ret.add(new ColumnInfo("year_stop", "Окончание строительства (год)"));
		ret.add(new ColumnInfo("complet_build_phs__name", "Стадия строительства", false, true, "complet_build_phs__rn", "select", "listCompletionPhase"));
		ret.add(new ColumnInfo("complet_build_val", "Готовность (%)"));
		ret.add(new ColumnInfo("doc_basis_fbfund", "Документ-основание", false));
		ret.add(new ColumnInfo("act_cost_real", "Фактические расходы", false));
		ret.add(new ColumnInfo("act_cost_real_iefb", "Фактические расходы в том числе из федерального бюджета", false));
		ret.add(new ColumnInfo("remain_est_cost", "Остаток сметной стоимости", false));
		ret.add(new ColumnInfo("safund_comcon", "Для завершения строительства", false));
		ret.add(new ColumnInfo("safund_comcon_iefb", "Для завершения строительства из федерального бюджета", false));
		ret.add(new ColumnInfo("safund_comcon_ievs", "Для завершения строительства из внебюджетных источников", false));
		ret.add(new ColumnInfo("safund_pres_obj", "На консервацию объекта", false));
		ret.add(new ColumnInfo("safund_pres_obj_source__name", "Источник финансирования консервации объекта", false, true, "safund_pres_obj_source__rn", "select", "listFundsSource"));
		ret.add(new ColumnInfo("safund_destr", "На снос объекта", false));
		ret.add(new ColumnInfo("safund_destr_source__name", "Источник финансирования сноса объекта", false, true, "safund_destr_source__rn", "select", "listFundsSource"));
		ret.add(new ColumnInfo("imp_period_event", "Срок реализации мероприятия", false));
		ret.add(new ColumnInfo("prop_rec_object", "Предлагаемый получатель объекта", false));
		ret.add(new ColumnInfo("rat_writing_obj", "Обоснование необходимости списания", false));
		ret.add(new ColumnInfo("presence_dpd", "Наличие проектной документации", false));
		ret.add(new ColumnInfo("prop_sol_dpd", "Предлагаемые решения организации", false));
		ret.add(new ColumnInfo("prop_sol_prov", "Предлагаемые решения по результатам проверки", false));
		ret.add(new ColumnInfo("depart__name", "Департамент", false)); 
		ret.add(new ColumnInfo("agnname1", "ФИО контактного лица 1", false));
		ret.add(new ColumnInfo("emppost1", "Должность контактного лица 1", false));
		ret.add(new ColumnInfo("phone_s1", "Телефон гор. контактного лица 1", false));
		ret.add(new ColumnInfo("phone_m1", "Телефон моб. контактного лица 1", false));
		ret.add(new ColumnInfo("email1", "Электронная почта контактного лица 1", false));
		ret.add(new ColumnInfo("agnname2", "ФИО контактного лица 2", false));
		ret.add(new ColumnInfo("emppost2", "Должность контактного лица 2", false));
		ret.add(new ColumnInfo("phone_s2", "Телефон гор. контактного лица 2", false));
		ret.add(new ColumnInfo("phone_m2", "Телефон моб. контактного лица 2", false));
		ret.add(new ColumnInfo("email2", "Электронная почта контактного лица 2", false));
		ret.add(new ColumnInfo("bs_source", "Бюджетные средства", false));
		ret.add(new ColumnInfo("bs_num", "Бюджетные средства: письмо №", false));
		ret.add(new ColumnInfo("bs_date", "Бюджетные средства: дата", false));
		ret.add(new ColumnInfo("vs_source", "Внебюджетные источники", false));
		ret.add(new ColumnInfo("vs_num", "Внебюджетные источники: письмо №", false));
		ret.add(new ColumnInfo("vs_date", "Внебюджетные источники: дата", false));
		ret.add(new ColumnInfo("ii_source", "Иные источники", false));
		ret.add(new ColumnInfo("ii_num", "Иные источники: письмо №", false));
		ret.add(new ColumnInfo("ii_date", "Иные источники: дата", false));
		ret.add(new ColumnInfo("faip", "ФАИП"));
		ret.add(new ColumnInfo("type_faip__name", "Тип документа ФАИП", false, true, "type_faip__rn", "select", "listTypeFaip"));
		ret.add(new ColumnInfo("num_faip", "Номер документа ФАИП", false));
		ret.add(new ColumnInfo("date_faip", "Дата документа ФАИП", false));
		ret.add(new ColumnInfo("regnum_fedprop", "Реестровый номер федерального имущества", false));
		ret.add(new ColumnInfo("regnum_zu", "Реестровый номер земельного участка", false));
		ret.add(new ColumnInfo("kadnum_obj", "Кадастровый номер объекта", false));
		ret.add(new ColumnInfo("kadnum_zu", "Кадастровый номер земельного участка", false));
		ret.add(new ColumnInfo("req_trip", "Требуется выезд", false));
		ret.add(new ColumnInfo("vvod_expl", "Введен в эксплуатацию"));
		
		return ret;
	}
	public static boolean listPaginated() { return true; }
	public static String spCode(String attr) {
		String ret = null;
		if ("type_faip".equals(attr)) ret = "sp_tfaip"; 
		else if ("safund_pres_obj_source".equals(attr) || "safund_destr_source".equals(attr)) ret = "sp_if";
		else if ("purpose".equals(attr)) ret = "sp_nazn"; 
		else if ("obj_capacity_ei".equals(attr)) ret = "sp_ei";
		else if ("status".equals(attr)) ret = "sp_os";
		else if ("state_view_event".equals(attr)) ret = "sp_svm";
		else ret = (String)HelperServiceImpl.invokeStatic(RProperty.class.getSuperclass(), "spCode", attr);
		return ret;
	}
	@Override
	public Object onListAddFilter(List<String> listAttr, List<Object> listValue, Map<String, String[]> mapParam) {
		Object ret = super.onListAddFilter(listAttr, listValue, mapParam);
		if (ret != null) return ret;
		IUser user = userService.getUser((String)null);
		if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
		String roles = user.getRoles();
		if (roles.indexOf("ADMIN") < 0) {
			IOrganization org = user.getOrg();
			if (org == null) throw new SecurityException("У пользователя " + user.getLogin() + " не задана организация");
			listAttr.add("customer__rn");
			listValue.add(org.getRn());
		}
		return true;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listObjectStatus", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_os"}));
			model.addAttribute("listViewEvent", objService.findAll(SpViewEvent.class));
			model.addAttribute("listOrg", objService.findAll(IOrganization.class));
			model.addAttribute("listDepartment", objService.findAll(IDepartment.class));
			model.addAttribute("listStateViewEvent", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_svm"}));
			model.addAttribute("listCompletionPhase", objService.findAll(SpCompletionPhase.class));
			model.addAttribute("listPurpose", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_nazn"}));
			model.addAttribute("listMeasureUnit", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_ei"}));
			model.addAttribute("listFundsSource", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_if"}));
			model.addAttribute("listTypeFaip", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_tfaip"}));
			model.addAttribute("listObjectLocation", objService.findAll(SpObjectLocation.class));
		}
		catch (Exception ex) { }
		return true;
	}
    @Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	setStatus((SpCommon)objRepository.find(SpCommon.class, new String[] {"code", "sp_code"}, new Object[] {"0", "sp_os"}));
     	IUser user = userService.getUser((String)null);
     	if (user != null) {
	     	IOrganization org = user.getOrg();
	     	if (org != null) setCustomer(org);
     	}
     	return true;
    }
    @Override
    public Object onUpdate() throws Exception {
    	Object ret = super.onUpdate();
    	if (ret != null) return ret;
    	if (getView_event() == null) return false;;
		SpViewEvent ve = getView_event();
		String veDoctype = ve.getDoctype();
		if (hs.isEmpty(veDoctype)) return false;
		boolean update = false;
		String[] doctypes = veDoctype.split(",");
		List<RDocument> listDoc = getDocuments();
		if (listDoc == null) {
			listDoc = new ArrayList<RDocument>();
			setDocuments(listDoc);
			update = true;
		}
		for (String doctype : doctypes) {
			boolean b = false;
			for (RDocument doc : listDoc) {
				if (doctype.equals(doc.getDoctype().getCode())) {
					b = true;
					break;
				}
			}
			if (b) continue;
			SpRDocType spDoctype = (SpRDocType)objService.getObjByCode(SpDocType.class, doctype);
			if (spDoctype == null) continue;
			RDocument doc = new RDocument();
			doc.setDoctype(spDoctype);
			doc.setApsend(true);
			doc.setPrim_apsend("Документ отсутствует");
			doc.setParent(this);
			doc = (RDocument)objRepository.createObj(doc);
			if (doc != null) {
				listDoc.add(doc);
				update = true;
			}
		}
		if (update) objRepository.saveObj(this);
		return true;
    }
    @Override
    public Object onCheckRights(Operation op) { 
     	Object ret = invoke("onCheckRights", op);
    	if (ret != null) return ret;
    	if (getRn() == null) return true;
    	if (op == Operation.update || op == Operation.delete) {
	     	IUser user = userService.getUser((String)null);
			if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
			String roles = user.getRoles();
			if (roles.indexOf("ADMIN") >= 0) return true;
			IOrganization org = user.getOrg();
			if (org == null) throw new SecurityException("У пользователя " + user.getLogin() + " не указана организация");
			return getCustomer() != null && org.getRn().compareTo(getCustomer().getRn()) == 0;
    	}
    	return true;
    }
    @Override
    public Object onBuildContent() {
    	Object ret = super.onBuildContent();
    	if (ret != null) return ret;
    	ret = new SearchContent();
    	SearchContent sc = (SearchContent)ret;
    	sc.setId("" + getRn());
    	sc.setClazz(getClazz());
    	sc.setName(getName());
    	String content = getCustomer() != null ? "Заказчик, застройщик: " + getCustomer().getName() : "";
    	if (!hs.isEmpty(getObj_name())) {
    		if (!content.isEmpty()) content += ". ";
    		content += "Наименование объекта: " + getObj_name();
    	}
    	if (!hs.isEmpty(getObj_address())) {
    		if (!content.isEmpty()) content += ". ";
    		content += "Адрес: " + getObj_address();
    	}
    	if (!hs.isEmpty(getIdent_obj())) {
    		if (!content.isEmpty()) content += ". ";
    		content += "Идентификатор объекта: " + getIdent_obj();
    	}
    	sc.setContent(content);
    	return ret;
    }
}
