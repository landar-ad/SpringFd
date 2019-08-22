package ru.landar.spring.service;

import java.util.List;

import ru.landar.spring.model.IUser;

public interface UserService {
	
	String getPrincipal();
	List<IUser> getUsers();
	IUser addUser(IUser user);
	IUser changePassword(IUser user, String password);
	String getRoles(String username);
	IUser getUser(Integer rn);
	IUser getUser(String username);
	boolean isAdmin(String username);
}
