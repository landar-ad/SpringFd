package ru.landar.spring.model.assets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.LockModeType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.classes.FieldTitle;
import ru.landar.spring.classes.ObjectTitle;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.config.AutowireHelper;
import ru.landar.spring.model.IBase;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.IUser;
import ru.landar.spring.model.SpCommon;

@Entity
@PrimaryKeyJoinColumn(name="rn")
@ObjectTitle(single="Заявка", multi="Заявки")
public class RClaim extends IBase {
	private IOrganization co_org;
	private SpCommon za_type;
	private String za_num;
	private Date za_date;
	private String za_sod;
	private Boolean za_poppr;
	private String prim;
	private SpCommon za_stat;
	private String za_zc;
	private IPerson za_ol;
	private Date za_srz;
	private Date za_spo;
	private RClaim za_opr;
	private Boolean za_vzg;
	private List<Item_RProperty> list_oz;
	private List<Item_RDocument> list_doc;
	
	@FieldTitle(name="Принадлежность")
	@ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getCo_org() { return co_org; }
    public void setCo_org(IOrganization co_org) { this.co_org = co_org; }
    
    @FieldTitle(name="Тип заявки", sp="sp_type_za")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getZa_type() { return za_type; }
    public void setZa_type(SpCommon za_type) { this.za_type = za_type; }
    
    @FieldTitle(name="Номер заявки")
    @Column(length=30)
    public String getZa_num() { return za_num; }
    public void setZa_num(String za_num) { this.za_num = za_num; updateName(); }
    
    @FieldTitle(name="Дата заявки")
    @Temporal(TemporalType.DATE)
    public Date getZa_date() { return za_date; }
    public void setZa_date(Date za_date) { this.za_date = za_date; updateName(); }
    
    @FieldTitle(name="Содержание заявки", editType="textarea")
    @Column(length=2000)
    public String getZa_sod() { return za_sod; }
    public void setZa_sod(String za_sod) { this.za_sod = za_sod; }
    
    @FieldTitle(name="Признак применения оценки последствий принятия решений")
    public Boolean getZa_poppr() { return za_poppr; }
    public void setZa_poppr(Boolean za_poppr) { this.za_poppr = za_poppr; }
    
    @FieldTitle(name="Примечание", editType="textarea")
    @Column(length=4000)
    public String getPrim() { return prim; }
    public void setPrim(String prim) { this.prim = prim; }
    
    @FieldTitle(name="Статус заявки", sp="sp_stat_za")
    @ManyToOne(targetEntity=SpCommon.class, fetch=FetchType.LAZY)
    public SpCommon getZa_stat() { return za_stat; }
    public void setZa_stat(SpCommon za_stat) { this.za_stat = za_stat; }
    
    @FieldTitle(name="Заключение членов комиссии", editType="textarea")
    @Column(length=4000)
    public String getZa_zc() { return za_zc; }
    public void setZa_zc(String za_zc) { this.za_zc = za_zc; }
    
    @FieldTitle(name="Ответственное лицо")
    @ManyToOne(targetEntity=IPerson.class, fetch=FetchType.LAZY)
    public IPerson getZa_ol() { return za_ol; }
    public void setZa_ol(IPerson za_ol) { this.za_ol = za_ol; }
    
    @FieldTitle(name="Срок рассмотрения заявки")
    @Temporal(TemporalType.DATE)
    public Date getZa_srz() { return za_srz; }
    public void setZa_srz(Date za_srz) { this.za_srz = za_srz; }
    
    @FieldTitle(name="Срок предоставления отчета о реализации вынесенного решения")
    @Temporal(TemporalType.DATE)
    public Date getZa_spo() { return za_spo; }
    public void setZa_spo(Date za_spo) { this.za_spo = za_spo; }
    
    @FieldTitle(name="Заявка на оценку принятия решений")
    @ManyToOne(targetEntity=RClaim.class, fetch=FetchType.LAZY)
    public RClaim getZa_opr() { return za_opr; }
    public void setZa_opr(RClaim za_opr) { this.za_opr = za_opr; }
    
    @FieldTitle(name="Возможность заочного голосования")
    public Boolean getZa_vzg() { return za_vzg; }
    public void setZa_vzg(Boolean za_vzg) { this.za_vzg = za_vzg; }
	    
    @FieldTitle(name="Объекты заявки")
    @OneToMany(targetEntity=Item_RProperty.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Item_RProperty> getList_oz() { return list_oz != null ? list_oz : new ArrayList<Item_RProperty>(); }
    public void setList_oz(List<Item_RProperty> list_oz) { this.list_oz = list_oz; }
    
    @FieldTitle(name="Документы заявки")
    @OneToMany(targetEntity=Item_RDocument.class, cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    public List<Item_RDocument> getList_doc() { return list_doc != null ? list_doc : new ArrayList<Item_RDocument>(); }
    public void setList_doc(List<Item_RDocument> list_doc) { this.list_doc = list_doc; }
    
    private void updateName() {
    	if (hs == null) AutowireHelper.autowire(this);
    	String name = "";
    	if (!hs.isEmptyTrim(getZa_num())) name = getZa_num();
    	if (getZa_date() != null) {
    		if (!hs.isEmpty(name)) name += " от ";
    		name += hs.getPropertyString(this, "za_date");
    	}
    	setName(name);
    }
    
    public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		Class<?> cl = RClaim.class;
		ret.add(new ColumnInfo("co_org__name", cl)); 
		ret.add(new ColumnInfo("za_type", cl));
		ret.add(new ColumnInfo("za_num", cl));
		ret.add(new ColumnInfo("za_date", cl));
		ret.add(new ColumnInfo("za_sod", cl));
		ret.add(new ColumnInfo("za_poppr", cl));
		ret.add(new ColumnInfo("prim", cl));
		ret.add(new ColumnInfo("za_stat", cl));
		ret.add(new ColumnInfo("za_zc", cl));
		ret.add(new ColumnInfo("za_ol", cl));
		ret.add(new ColumnInfo("za_srz", cl));
		ret.add(new ColumnInfo("za_spo", cl));
		ret.add(new ColumnInfo("za_opr__name", cl));
		ret.add(new ColumnInfo("za_vzg", cl));
		return ret;
	}
	public static boolean listPaginated() { return true; }
	@Override
    public Object onNew() {
     	Object ret = super.onNew();
    	if (ret != null) return ret;
    	IUser user = userService.getUser((String)null);
    	if (user == null) throw new SecurityException("Вы не зарегистрированы в системе");
    	if (getCo_org() == null) hs.setProperty(this, "co_org", user.getOrg());
    	if (getZa_ol() == null) hs.setProperty(this, "za_ol", user.getPerson());
    	if (getZa_date() == null) hs.setProperty(this, "za_date", new Date());
    	if (getZa_stat() == null) hs.setProperty(this, "za_stat", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_stat_za", "1"})); 
    	return this;
	}
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listSp_type_za", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_type_za"}));
			model.addAttribute("listSp_stat_za", objService.findAll(SpCommon.class, null, new String[] {"sp_code"}, new Object[] {"sp_stat_za"}));
		}
		catch (Exception ex) { }
		return null;
	}
	@Override
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = super.listButton();
		if (ret == null) ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("send", "Отправить", null, "primary"));
		ret.add(new ButtonInfo("correct", "На доработку", null, "primary"));
		ret.add(new ButtonInfo("accept", "Принять", null, "primary"));
		ret.add(new ButtonInfo("confirm", "Утвердить", null, "primary"));
		ret.add(new ButtonInfo("refuse", "Отклонить", null, "primary"));
		return ret;
	}
	@Override
	public List<ButtonInfo> detailsButton() {
		List<ButtonInfo> ret = super.detailsButton();
		if (ret == null) ret = new ArrayList<ButtonInfo>();
		ret.add(new ButtonInfo("create_popr", "Создать заявку ПОПР", null, "primary"));
		return ret;
	}
	@Override
    public Object onCheckRights(Operation op) { 
    	Object ret = invoke("onCheckRights", op);
     	if (ret != null) return ret;
    	Integer rn = getRn();
    	if (op == Operation.create) return true;
    	if (op == Operation.load) return true;
    	if (rn == null) return true;
    	if (op == Operation.update) {
    		if (userService.isAdmin(null)) return true;
    		if (statusCode() == 1 || statusCode() == 3) return true;
    	}
    	if (op == Operation.delete) {
    		if (userService.isAdmin(null)) return true;
    		if (statusCode() == 1) return true;
    	}
    	return false;
    }
    @Override
    public Object onCheckExecute(String param) { 
     	Object ret = invoke("onCheckExecute", param);
     	if (ret != null) return ret;
     	if ("add".equals(param)) return onCheckRights(Operation.create);
    	if (getRn() == null) return false;
    	if ("edit".equals(param)) return onCheckRights(Operation.update);
		else if ("remove".equals(param)) return onCheckRights(Operation.delete);
		else if ("view".equals(param)) return onCheckRights(Operation.load);
		else if ("send".equals(param)) {
			if (userService.isAdmin(null)) return true;
			// Условия завершения подготовки заявки
			if ((statusCode() == 1 || statusCode() == 3) && 
				getCo_org() != null &
				!hs.isEmptyTrim(getZa_num()) &
				getZa_date() != null &&
				getList_doc().size() > 0 && 
				getList_oz().size() > 0)  return true;
		}
		else if ("create_popr".equals(param)) {
			if (userService.isAdmin(null)) return true;
			if (statusCode() == 1) return true;
		}
		else if ("accept".equals(param)) {
			if (userService.isAdmin(null)) return true;
			// Условия завершения проверки заявки
			if (statusCode() == 2)  return true;
		}
		else if ("correct".equals(param)) {
			if (userService.isAdmin(null)) return true;
			// Условия перевода на корректировку
			if (statusCode() == 2)  return true;
		}
		else if ("confirm".equals(param)) {
			if (userService.isAdmin(null)) return true;
			// Условия перевода на корректировку
			if (statusCode() == 4)  return true;
		}
		else if ("refuse".equals(param)) {
			if (userService.isAdmin(null)) return true;
			// Условия перевода на корректировку
			if (statusCode() == 4)  return true;
		}
		return false;
    }
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void send(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("send")) return;
    	hs.setProperty(this, "za_stat", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_stat_za", "2"}));
	}
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public String create_popr(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("create_popr")) return null;
    	RClaim popr = new RClaim();
    	hs.setProperty(popr, "co_org", getCo_org());
    	hs.setProperty(popr, "za_poppr", true);
    	hs.setProperty(popr, "za_type", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_type_za", "1"}));
    	hs.setProperty(popr, "za_ol", getZa_ol());
    	hs.invoke(popr, "onNew");
    	popr = (RClaim)objRepository.createObj(popr);
    	hs.setProperty(this, "za_opr", popr);
    	return "/detailsObj?rn=" + popr.getRn();
	}
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void accept(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("accept")) return;
    	hs.setProperty(this, "za_stat", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_stat_za", "7"}));
	}
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void correct(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("correct")) return;
    	hs.setProperty(this, "za_stat", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_stat_za", "3"}));
	}
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void confirm(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("confirm")) return;
    	hs.setProperty(this, "za_stat", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_stat_za", "6"}));
	}
	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public void refuse(HttpServletRequest request) throws Exception {
    	AutowireHelper.autowire(this);
    	if (!(Boolean)onCheckExecute("refuse")) return;
    	hs.setProperty(this, "za_stat", objRepository.find(SpCommon.class, new String[] {"sp_code", "code"}, new Object[] {"sp_stat_za", "5"}));
	}
	protected int statusCode() {
    	int ret = 1; 
    	try { ret = Integer.valueOf(getZa_stat().getCode()); } catch (Exception ex) { }
    	return ret;
    }
}
