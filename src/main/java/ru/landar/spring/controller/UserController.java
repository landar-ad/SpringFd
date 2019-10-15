package ru.landar.spring.controller;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.ObjectChanged;
import ru.landar.spring.classes.ChangeInfo;
import ru.landar.spring.classes.Operation;
import ru.landar.spring.model.IOrganization;
import ru.landar.spring.model.IPerson;
import ru.landar.spring.model.IRole;
import ru.landar.spring.model.IUser;
import ru.landar.spring.repository.ObjRepositoryCustom;
import ru.landar.spring.repository.UserRepositoryCustom;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.ObjService;
import ru.landar.spring.service.UserService;

@Controller
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {
	@Autowired
	ObjService objService;  
	@Autowired
	ObjRepositoryCustom objRepository;
	@Autowired
	UserService userService;
	@Autowired
	HelperService hs;
	@Autowired
	UserRepositoryCustom userRepositoryCustom;
	@Autowired
    private PlatformTransactionManager transactionManager;
	@Resource(name = "getObjectChanged")
    ObjectChanged objectChanged;
	// Данные пользователя
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public String userPost(@RequestParam("login") String login, 
						   @RequestParam("password") String password, 
						   @RequestParam("role_user") Optional<Boolean> role_userParam, 
						   @RequestParam("role_admin") Optional<Boolean> role_userAdmin,
						   @RequestParam("role_df") Optional<Boolean> role_userDf,
						   @RequestParam("disabled") Optional<Boolean> disabledParam,
						   @RequestParam("org") Optional<Integer> orgParam,
						   @RequestParam("person") Optional<Integer> personParam,
						   HttpServletRequest request,
						   Model model) {
		String ip = (String)request.getSession().getAttribute("ip"), browser = (String)request.getSession().getAttribute("browser");
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
		// Список ролей
		List<IRole> lr = user.getList_roles();
		List<String> listNames = Collections.list((Enumeration<String>)request.getParameterNames());
		for (String p : listNames) {
			if (!p.startsWith("list_roles__rn")) continue;
			String[] vs = request.getParameterValues(p);
			for (String v : vs) {
				try { 
					IRole role = (IRole)objService.find(IRole.class, Integer.valueOf(v));
					if (role != null) lr.add(role);
				} 
				catch (Exception ex) { }
			}
		}
		user.setList_roles(lr);
		// Проверка прав на редактирование
		if (!hs.checkRights(user, Operation.update)) throw new SecurityException("Вы не имеете право на редактирование пользователя " + user.getLogin());
		TransactionStatus ts = transactionManager.getTransaction(new DefaultTransactionDefinition());    	
    	try {
    		user = userRepositoryCustom.addUser(user);
    		// Запись в журнал
			List<ChangeInfo> lci = objectChanged.getObjectChanges();
			for (ChangeInfo ci : lci) objRepository.writeLog(userService.getPrincipal(), ci.getRn(), ci.getClazz(), ci.getValue(), ci.getOp(), ip, browser);
			transactionManager.commit(ts);
    	}
    	catch (Exception ex) {
    		transactionManager.rollback(ts);
    		throw ex;
    	}
		return "redirect:/listObj?clazz=IUser&rn=" + user.getRn();
	}
}
