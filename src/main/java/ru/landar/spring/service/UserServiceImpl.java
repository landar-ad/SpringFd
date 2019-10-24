package ru.landar.spring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.landar.spring.model.IRole;
import ru.landar.spring.model.IUser;
import ru.landar.spring.repository.UserRepository;
import ru.landar.spring.repository.UserRepositoryCustom;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserRepositoryCustom userRepositoryCustom;
	@Override
	public String getPrincipal() {
		String principal = "";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) principal = authentication.getName();
		return principal;
	}
	@Override
	public List<IUser> getUsers() {
		return userRepository.findAll();
	}
	@Override
	public IUser getUser(Integer rn) {
		return userRepository.findById(rn).orElse(null);
	}
	@Override
	public IUser getUser(String username) {
		if (username == null || username.isEmpty()) username = getPrincipal();
		return userRepository.find(username);
	}
	@Override
	public String getRoles(String username) {
		String roles = "";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			if (username != null && !username.isEmpty() && !username.equals(authentication.getName())) roles = userRepositoryCustom.getRoles(username);
			else {
				if (authentication.getAuthorities() != null) {
					for (GrantedAuthority ga : authentication.getAuthorities()) {
						String role = ga.getAuthority();
						if (role != null && !role.isEmpty()) {
							if (!roles.isEmpty()) roles += ",";
							roles += role;
						}
					}
				}
			}
		}
		return roles;
	}
	@Override
	public boolean isAdmin(String username) { return hasBaseRole(username, "ROLE_ADMIN"); }
	@Override
	public boolean isDF(String username) { return hasBaseRole(username, "ROLE_DF"); }
	@Override
	public boolean isUser(String username) { return hasBaseRole(username, "ROLE_USER"); }
	@Override
	public boolean hasBaseRole(String username, String role) {
		IUser user = getUser(username);
		String roles = user != null ? user.getRoles() : getRoles(username);
		return roles.indexOf(role) >= 0;
	}
	@Override
	public IRole getRole(String username, String code) {
		if (code == null) return null;
		IUser user = getUser(username);
		if (user == null) return null;
		List<IRole> roles = user.getList_roles();
		if (roles.size() < 1) return null;
		for (IRole role : roles) if (code.equals(role.getCode())) return role;
		return null;
	}
	@Override
	public List<IRole> getList_roles(String username) {
		IUser user = getUser(username);
		if (user == null) return null;
		return user.getList_roles();
	}
	@Override
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@Transactional
	public IUser addUser(IUser user) {
		return userRepositoryCustom.addUser(user);
	}
	@Override
	@Transactional
	public IUser changePassword(IUser user, String password) {
		return userRepositoryCustom.changePassword(user, password);
	}
}
