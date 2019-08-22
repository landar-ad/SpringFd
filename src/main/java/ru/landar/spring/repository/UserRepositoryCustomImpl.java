package ru.landar.spring.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import ru.landar.spring.model.IUser;
import ru.landar.spring.service.HelperService;

@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	public static PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	
	@Autowired 
	UserRepository userRepository;
	
	@Autowired 
	HelperService hs;
	
	@Override
	public IUser addUser(IUser user) {
		String login = user.getLogin();
		if (hs.isEmpty(login)) return null; 
		String password = user.getPassword();
		IUser userOld = userRepository.find(login);
		if (userOld != null) {
			userOld.setRoles(user.getRoles());
			userOld.setDisabled(user.getDisabled());
			userOld.setOrg(user.getOrg());
			userOld.setPerson(user.getPerson());
			user = userOld;
		}
		if (!hs.isEmpty(password)) user.setPassword(encoder.encode(password));
		return userRepository.saveAndFlush(user);
	}
	@Override
	public String getRoles(String username) {
		IUser user = userRepository.find(username);
		return user != null ? user.getRoles() : "";
	}
	@Override
	public IUser changePassword(IUser user, String password) {
		if (!hs.isEmpty(password)) {
			user.setPassword(encoder.encode(password));
			user = userRepository.saveAndFlush(user);
		}
		return user;
	}
}
