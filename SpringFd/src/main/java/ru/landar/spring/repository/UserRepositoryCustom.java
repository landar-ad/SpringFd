package ru.landar.spring.repository;

import org.springframework.stereotype.Repository;

import ru.landar.spring.model.IUser;

@Repository
public interface UserRepositoryCustom {

	IUser addUser(IUser user);
	String getRoles(String username);
}
