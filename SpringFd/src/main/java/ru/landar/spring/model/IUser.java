package ru.landar.spring.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import ru.landar.spring.classes.ButtonInfo;
import ru.landar.spring.classes.ColumnInfo;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;

@Entity
@PrimaryKeyJoinColumn(name="rn")
public class IUser extends IBase {
	private String login;
	private String password;
	private String roles;
	private Boolean disabled;
	private IOrganization org;
	private IPerson person;
	
	@Column(length=50, nullable=false, unique=true)
    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; setName(login); }
    
    @Column(length=256, nullable=false)
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    @Column(length=256)
    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }
    
    public Boolean getDisabled() { return disabled; }
    public void setDisabled(Boolean disabled) { this.disabled = disabled; }
    
    @ManyToOne(targetEntity=IOrganization.class, fetch=FetchType.LAZY)
    public IOrganization getOrg() { return org; }
    public void setOrg(IOrganization org) { this.org = org; }
    
    @ManyToOne(targetEntity=IPerson.class, fetch=FetchType.LAZY)
    public IPerson getPerson() { return person; }
    public void setPerson(IPerson person) { this.person = person; }
       
    @Autowired
	ObjService objService;
    @Autowired
	HelperService hs;
    // Функции класса
	@Override
	public Object onAddAttributes(Model model, boolean list) {
		Object ret = super.onAddAttributes(model, list);
		if (ret != null) return ret;
		try {
			model.addAttribute("listOrg", objService.findAll(IOrganization.class));
			model.addAttribute("listPerson", objService.findAll(IPerson.class));
			if (!list) {
				boolean role_user = false, role_admin = false, role_df = false;
				String roles = getRoles();
				if (!hs.isEmpty(roles)) {
					String[] rs = roles.split(",");
					for (String r : rs) {
						if (r.indexOf("USER") > 0) role_user = true;
						if (r.indexOf("ADMIN") > 0) role_admin = true;
						if (r.indexOf("DF") > 0) role_df = true;
					}
				}
				model.addAttribute("role_user", role_user);
				model.addAttribute("role_admin", role_admin);
				model.addAttribute("role_df", role_df);
				model.addAttribute("p_title", getRn() == null ? "Новый пользователь" : "Данные пользователя " + getLogin());
			}
		}
		catch (Exception ex) { }
		return true;
	}
	public List<ButtonInfo> listButton() {
		List<ButtonInfo> ret = new ArrayList<ButtonInfo>();
		if (userService.isAdmin(null)) ret.add(new ButtonInfo("edit", "Редактировать", "edit"));
		ret.add(new ButtonInfo("view", "Просмотреть", "readme"));
		if (userService.isAdmin(null)) ret.add(new ButtonInfo("add", "Добавить", "plus-circle"));
		if (userService.isAdmin(null)) ret.add(new ButtonInfo("remove", "Удалить", "trash"));
		return ret;
	}
    // Статические функции
	public static String singleTitle() { return "Пользователь"; }
	public static String multipleTitle() { return "Пользователи"; }
	public static List<ColumnInfo> listColumn() {
		List<ColumnInfo> ret = new ArrayList<ColumnInfo>();
		ret.add(new ColumnInfo("login", "Логин"));
		ret.add(new ColumnInfo("roles", "Роли"));
		ret.add(new ColumnInfo("org__name", "Организация", true, true, "org__rn", "select", "listOrg"));
		ret.add(new ColumnInfo("person__name", "Контрагент (физическое лицо)", true, true, "person__rn", "select", "listPerson"));
		ret.add(new ColumnInfo("disabled", "Заблокирован"));
		return ret;
	}
	public static boolean listPaginated() { return true; }
}
