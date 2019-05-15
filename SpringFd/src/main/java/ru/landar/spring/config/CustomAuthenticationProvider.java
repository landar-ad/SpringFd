package ru.landar.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import ru.landar.spring.model.IUser;
import ru.landar.spring.repository.UserRepositoryCustomImpl;
import ru.landar.spring.service.UserService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	@Autowired 
	UserService userService;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName(), password = null;
		String roles = null;
		String error = "";
		for (; ;) {
			if (username == null || username.isEmpty()) { error = "Не задан логин"; break; } 
			Object credentials = authentication.getCredentials();
			if (credentials == null) { error = "Не задан пароль"; break; }
			password = credentials.toString();
			IUser user = userService.getUser(username);
			if (user == null) { 
				if (userService.getUsers().size() < 1 && username.equals("admin") && password.equals("admin")) {
					roles = "ROLE_ADMIN";
					break;
				}
				error = "Не найден пользователь '" + username + "'"; break; 
			}
			if (!UserRepositoryCustomImpl.encoder.matches(password, user.getPassword())) { error = "Неверный пароль"; break; }  
			roles = user.getRoles();
			if (roles == null || roles.isEmpty()) roles = "ROLE_USER";
			break;
		}
		if (!error.isEmpty()) throw new BadCredentialsException(error);
		return new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
	/*
	auth.ldapAuthentication()
            .contextSource().url("ldap://portal.mon.gov.ru:389/dc=portal,dc=informika,dc=ru")
            .managerDn("cn=admin,dc=portal,dc=informika,dc=ru").managerPassword("Airieb0t")
            .and()
            .userDnPatterns("uid={0},ou=people");
	*/
}

