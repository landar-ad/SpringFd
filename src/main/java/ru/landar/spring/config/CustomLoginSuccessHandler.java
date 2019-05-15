package ru.landar.spring.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	public CustomLoginSuccessHandler(String defaultTargetUrl) {
        setDefaultTargetUrl(defaultTargetUrl);
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
    	Cookie cookie = new Cookie("username", request.getParameter("username"));
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
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