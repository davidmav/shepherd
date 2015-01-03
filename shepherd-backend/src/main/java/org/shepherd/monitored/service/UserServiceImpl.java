package org.shepherd.monitored.service;

import org.shepherd.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements  UserService{

	@Override
	public User authenticate(String userName, String password) {
		
		User user = new User();
		user.setRole("admin");

		return user;
	}

}
