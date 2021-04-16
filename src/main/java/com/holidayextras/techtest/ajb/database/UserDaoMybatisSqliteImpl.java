package com.holidayextras.techtest.ajb.database;

import java.time.Instant;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.holidayextras.techtest.ajb.domain.ProtoUser;
import com.holidayextras.techtest.ajb.domain.User;

/**
 * Specific implementation of {@code UserDao} using MyBatis to access an SQLite database.
 */
@Component
public class UserDaoMybatisSqliteImpl implements UserDao {
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	@Override
	public List<User> getAllUsers() {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			var userMapper = session.getMapper(UserMapper.class);
			return userMapper.getAllUsers();
		}
	}
	
	@Override
	public List<User> getUsers(int offset) {
		if (offset == 0) {
			return getAllUsers();
		} else {
			try (SqlSession session = sqlSessionFactory.openSession()) {
				var userMapper = session.getMapper(UserMapper.class);
				return userMapper.getUsersFromOffset(offset);
			}
		}
	}

	@Override
	public List<User> getUsers(int offset, int limit) {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			var userMapper = session.getMapper(UserMapper.class);
			if (offset == 0) {
				return userMapper.getAllUsersWithLimit(limit);
			} else {
				return userMapper.getUsersFromOffsetWithLimit(offset, limit);
			}
		}
	}

	@Override
	public User getUser(int id) {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			var userMapper = session.getMapper(UserMapper.class);
			return userMapper.getUser(id);
		}
	}

	@Override
	public int createUser(ProtoUser user) {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			var userMapper = session.getMapper(UserMapper.class);
			userMapper.addUser(user, Instant.now().toEpochMilli());
			session.commit();
			return userMapper.getLatestKey();
		}
	}

	@Override
	public void createMultipleUsers(List<ProtoUser> users) {
		if (users != null) {
			for (ProtoUser user : users) {
				createUser(user);
			}
		}
	}

	@Override
	public void updateUser(int id, ProtoUser user) {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			var userMapper = session.getMapper(UserMapper.class);
			userMapper.updateUser(id, user);
			session.commit();
		}
	}

	@Override
	public void deleteAllUsers() {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			var userMapper = session.getMapper(UserMapper.class);
			userMapper.deleteAllUsers();
			session.commit();
		}
	}

	@Override
	public void deleteUser(int id) {
		try (SqlSession session = sqlSessionFactory.openSession()) {
			var userMapper = session.getMapper(UserMapper.class);
			userMapper.deleteUser(id);
			session.commit();
		}
	}
}
