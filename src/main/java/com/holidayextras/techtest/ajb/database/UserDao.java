package com.holidayextras.techtest.ajb.database;

import java.util.List;

import com.holidayextras.techtest.ajb.domain.ProtoUser;
import com.holidayextras.techtest.ajb.domain.User;

/**
 * Interface for user DAOs, which implements user management operations on a target data source.
 */
public interface UserDao {
	List<User> getAllUsers();
	List<User> getUsers(int offset);
	List<User> getUsers(int offset, int limit);
	User getUser(int id);
	int createUser(ProtoUser user);
	void createMultipleUsers(List<ProtoUser> users);
	void updateUser(int id, ProtoUser user);
	void deleteAllUsers();
	void deleteUser(int id);
}
