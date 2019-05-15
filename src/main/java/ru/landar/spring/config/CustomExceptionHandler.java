package ru.landar.spring.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import ru.landar.spring.service.UserService;

@ControllerAdvice
class CustomExceptionHandler {
	@Autowired
	UserService userService;
	public static final String DEFAULT_ERROR_VIEW = "exceptionPage";
	@ExceptionHandler(value = Exception.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null) throw e;
		ModelAndView mav = new ModelAndView();
		List<String> listMessage = new ArrayList<String>();
		listMessage.add("Исключение - " + e.getClass().getSimpleName() + ": " + e.getMessage());
		if (userService.isAdmin(null)) {
			for (StackTraceElement te : e.getStackTrace()) listMessage.add(te.toString());
		}
		mav.addObject("listMessage", listMessage);
		mav.setViewName(DEFAULT_ERROR_VIEW);
		return mav;
	}
}