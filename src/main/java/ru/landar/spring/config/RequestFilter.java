package ru.landar.spring.config;

import java.io.IOException;
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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		for (; ;) {
			if (!(request instanceof HttpServletRequest)) break;
			HttpServletRequest r = (HttpServletRequest)request;
			String url = r.getRequestURI(), key = null;
			if (url.indexOf("listObj") >= 0) key = "listObj";
			if (url.indexOf("detailsObj") >= 0) key = "detailsObj";
			if (!hs.isEmpty(key)) {
				Map<String, String[]> map = r.getParameterMap();
				if (!map.containsKey("clazz")) break;
				String clazz = r.getParameter("clazz");
				if (hs.isEmpty(clazz)) break;
				HttpSession session = r.getSession();
				session.setAttribute(key + "_" + clazz, map);
			}
			break;
		}
		chain.doFilter(request, response);
	}
}
