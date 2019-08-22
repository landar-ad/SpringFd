package ru.landar.spring.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.UserService;

@Controller
public class NavigateController {
	@Autowired
	private UserService userService;
	@Autowired
	private HelperService hs;
	@RequestMapping(value = "/")
	public String root(HttpServletRequest request, Model model) {
		return "redirect:main";
	}
	@RequestMapping(value = "/main")
	public String main(HttpServletRequest request, Model model) {
		setMainModel(model, "Главная страница");
		return "mainPage";
	}
	@GetMapping(value = "/return")
	public String ret(HttpServletRequest request, @RequestParam("redirect") Optional<String> paramRedirect, Model model) {
		HttpSession session = request.getSession();
		String redirect = paramRedirect.orElse(null);
		if (!hs.isEmpty(redirect)) {
			if (!redirect.startsWith("/")) redirect = "/" + redirect;
			redirect += "?p_ret=1"; 
		}
		else redirect = request.getHeader("Referer");
		if (hs.isEmpty(redirect)) redirect = "/main";
		return "redirect:" + redirect; 
	}
	private void setMainModel(Model model, String title) {
		String login = userService.getPrincipal();
		model.addAttribute("p_login", login);
		model.addAttribute("p_roles", userService.getRoles(login));
		model.addAttribute("p_title", title);
	}
}
