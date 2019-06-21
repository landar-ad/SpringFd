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

@Controller
public class LoginController {
	@Autowired
	HelperService hs;
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
	@RequestMapping(value = "/accessDenied")
	public String accessDenied(HttpServletRequest request, Model model) {
		return "accessDenied";
	}
}
