package ru.landar.spring.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController  {
	@RequestMapping("/error")
	public String handleError(HttpServletRequest req, Model model) {
		int statusCode = 0;
		try { statusCode = Integer.valueOf(req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString()); } catch (Exception ex) { }
	    model.addAttribute("status", statusCode);
	    return "errorPage";
	}
    @Override
    public String getErrorPath() {
    	return "/error";
    }
}
