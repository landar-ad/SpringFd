package ru.landar.spring.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CustomErrorController implements ErrorController  {
	@RequestMapping("/error")
	public ModelAndView handleError(HttpServletRequest req, Model model) {
		int statusCode = 0;
		try { statusCode = Integer.valueOf(req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString()); } catch (Exception ex) { }
		ModelAndView mav = new ModelAndView();
		mav.addObject("status", statusCode);
		mav.setStatus(HttpStatus.valueOf(statusCode));
		mav.setViewName("errorPage");
	    return mav;
	}
    @Override
    public String getErrorPath() {
    	return "/error";
    }
}
