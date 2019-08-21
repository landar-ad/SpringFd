package ru.landar.spring.controller;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.UserService;

@Controller
public class LoginController {
	@Autowired
	HelperService hs;
	@Autowired
	UserService userService;
	@GetMapping(value = "/logout")
	public String logout(HttpServletRequest request, Model model) {
		try {
			HttpSession session = request.getSession();
			session.invalidate();
		}
		catch (Exception ex) {} 
		return "redirect:/login";
	}
	@GetMapping(value = "/login")
	public String login(@RequestParam("username") Optional<String> usernameParam, HttpServletRequest request, Model model) {
		String referer = request.getHeader("Referer");
	    request.getSession().setAttribute("prior_page", referer);
	    String ip = request.getHeader("X-Forwarded-For");
	    if (hs.isEmpty(ip)) ip = request.getRemoteAddr();
	    request.getSession().setAttribute("ip", ip);
	    request.getSession().setAttribute("browser", request.getHeader("user-agent"));
	    String username = usernameParam.orElse("");
	    if (username.isEmpty()) {
		    Cookie[] cookies = request.getCookies();
		    for (Cookie c : cookies) if ("username".equals(c.getName())) username = c.getValue();
	    }
	    model.addAttribute("username", username);
		return "loginPage";
	}
	@PostMapping(value = "/login")
	public String loginPost(@RequestParam("username") String login, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "loginPage";
	}
	@GetMapping(value = "/changePassword")
	public String changePassword(HttpServletRequest request, Model model) {
		model.addAttribute("p_login", userService.getPrincipal());
		return "changePasswordPage";
	}
	@PostMapping(value = "/changePassword")
	public String changePasswordPost(@RequestParam("old_password") String old_password, @RequestParam("password") String password, @RequestParam("confirm_password") String confirm_password, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		if (hs.isEmpty(old_password)) throw new Exception("Не указан старый пароль");
		if (hs.isEmpty(old_password)) throw new Exception("Старый пароль некорректен");
		if (hs.isEmpty(password)) throw new Exception("Не указан новый пароль");
		if (!password.equals(confirm_password)) throw new Exception("Новый пароль не совпадает с подтверждением");
		return "redirect:/main";
	}
	@RequestMapping(value = "/accessDenied")
	public String accessDenied(HttpServletRequest request, Model model) {
		return "accessDenied";
	}
}
