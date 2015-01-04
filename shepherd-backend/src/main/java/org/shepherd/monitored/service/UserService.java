package org.shepherd.monitored.service;

import org.shepherd.domain.User;

public interface UserService {

	public User authenticate(String userName, String password);
}
