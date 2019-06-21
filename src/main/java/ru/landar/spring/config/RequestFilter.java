package ru.landar.spring.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ru.landar.spring.service.HelperService;

@Component
@Order(1)
public class RequestFilter implements Filter {
	@Autowired 
	HelperService hs;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		for (; ;) {
			if (!(request instanceof HttpServletRequest)) break;
			HttpServletRequest r = (HttpServletRequest)request;
			String url = r.getRequestURI(), key = null;
			int k = url.lastIndexOf('/');
			if (k >= 0) key = url.substring(k + 1);
			if (hs.isEmpty(key)) break;
			Map<String, String[]> map = r.getParameterMap();
			if (map.containsKey("p_ret")) break;
			HttpSession session = r.getSession();
			Map<String, String[]> mapSave = new LinkedHashMap<String, String[]>();
			mapSave.putAll(map);
			String clazz = r.getParameter("clazz");
			session.setAttribute(key + (!hs.isEmpty(clazz) ? "_" + clazz : ""), mapSave);
			break;
		}
		chain.doFilter(request, response);
	}
}
