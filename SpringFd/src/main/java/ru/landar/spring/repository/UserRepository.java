package ru.landar.spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.landar.spring.model.IUser;

@Repository
public interface UserRepository extends JpaRepository<IUser, Integer> {
	
	@Query("select u from IUser u where u.login = ?1")
	IUser find(String login);
}
