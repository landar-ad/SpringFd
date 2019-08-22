package ru.landar.spring.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
 
import ru.landar.spring.model.IUser;
import ru.landar.spring.service.HelperService;
import ru.landar.spring.service.UserService;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	public CustomLoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }
	@Autowired
	HelperService hs;
	@Autowired
	UserService userService;
	
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
    	Cookie cookie = new Cookie("username", request.getParameter("username"));
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
        String new_password = request.getParameter("new_password"), confirm_password = request.getParameter("confirm_password");
        if (!hs.isEmpty(new_password) && new_password.equals(confirm_password)) {
        	IUser user = userService.getUser(userService.getPrincipal());
        	if (user != null) userService.changePassword(user, new_password);
        }
    	HttpSession session = request.getSession();
        if (session != null) {
            String redirectUrl = (String) session.getAttribute("prior_page");
            if (redirectUrl != null) {
                session.removeAttribute("prior_page");
                if (redirectUrl.indexOf("login") < 0) {
                	getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                	return;
                }
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}