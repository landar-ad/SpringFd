package ru.landar.spring.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.IUser;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {
	@Autowired
	ObjService objService;  
	@Autowired
	UserService userService;
	@Autowired
	HelperService hs;
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public String userPost(@RequestParam("login") String login, 
						   @RequestParam("password") String password, 
						   @RequestParam("role_user") Optional<Boolean> role_userParam, 
						   @RequestParam("role_admin") Optional<Boolean> role_userAdmin,
						   @RequestParam("role_df") Optional<Boolean> role_userDf,
						   @RequestParam("disabled") Optional<Boolean> disabledParam,
						   @RequestParam("org") Optional<Integer> orgParam,
						   @RequestParam("person") Optional<Integer> personParam,
						   Model model) {
		
		IUser user = new IUser();
		user.setLogin(login);
		if (!hs.isEmpty(password)) user.setPassword(password);
		boolean role_user = role_userParam.orElse(false);
		boolean role_admin = role_userAdmin.orElse(false);
		boolean role_df = role_userDf.orElse(false);
		String roles = "";
		if (role_user) roles += "ROLE_USER";
		if (role_admin) {
			if (!hs.isEmpty(roles)) roles += ',';
			roles += "ROLE_ADMIN";
		}
		if (role_df) {
			if (!hs.isEmpty(roles)) roles += ',';
			roles += "ROLE_DF";
		}
		if (hs.isEmpty(roles)) roles = "ROLE_USER";
		user.setRoles(roles);
		user.setDisabled(disabledParam.orElse(false));
		Integer org_rn = orgParam.orElse(null);
		IOrganization org = org_rn != null ? (IOrganization)objService.find(IOrganization.class, org_rn) : null;
		user.setOrg(org);
		Integer person_rn = personParam.orElse(null);
		IPerson person = person_rn != null ? (IPerson)objService.find(IPerson.class, person_rn) : null;
		user.setPerson(person);
		if (!hs.checkRights(user, Operation.update)) throw new SecurityException("Вы не имеете право на редактирование пользователя " + user.getLogin());
		user = userService.addUser(user);
		return "redirect:/listObj?clazz=IUser&rn=" + user.getRn();
	}
}
