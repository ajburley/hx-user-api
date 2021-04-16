package com.holidayextras.techtest.ajb.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.holidayextras.techtest.ajb.database.UserDao;
import com.holidayextras.techtest.ajb.domain.CreationResponse;
import com.holidayextras.techtest.ajb.domain.ProtoUser;
import com.holidayextras.techtest.ajb.domain.User;
import com.holidayextras.techtest.ajb.errorhandling.ApplicationStatusException;

/**
 * Controller for user management (CRUD) operations.
 */
@Controller
@RequestMapping("/user-mgmt")
public class UserController {
	@Autowired
	private UserDao userDao;
	
	@GetMapping("/users")
	@ResponseBody
	public List<User> getUsers(@RequestParam(name = "offset", defaultValue = "0") int offset,
			@RequestParam(name = "limit", required = false) Integer limit) {
		if (offset < 0) {
			throw new ApplicationStatusException("UC-001", HttpStatus.BAD_REQUEST, "Offset must be non-negative, if supplied.", Integer.toString(offset));
		} else if ((limit != null) && (limit <= 0)) {
			throw new ApplicationStatusException("UC-002", HttpStatus.BAD_REQUEST, "Limit must be positive, if supplied.", limit.toString());
		}
		if ((offset == 0) && (limit == null)) {
			return userDao.getAllUsers();
		} else if (limit == null) {
			return userDao.getUsers(offset);
		} else {
			return userDao.getUsers(offset, limit);
		}
	}
	
	@GetMapping("/users/{id}")
	@ResponseBody
	public User getUser(@PathVariable("id") int id) {
		return userDao.getUser(id);
	}
	
	private Optional<ApplicationStatusException> badRequestOptional(String errorCode, String message, String erroneousValue) {
		return Optional.of(new ApplicationStatusException(errorCode, HttpStatus.BAD_REQUEST, message, erroneousValue));
	}
	
	private Optional<ApplicationStatusException> validateProtoUser(ProtoUser user) {
		if (!user.getEmail().contains("@")) {
			return badRequestOptional("UC-003", "Email must contain @ symbol.", user.getEmail());
		}
		if (user.getEmail().startsWith("@") || user.getEmail().endsWith("@")) {
			return badRequestOptional("UC-004", "Email must not start or end with @ symbol.", user.getEmail());
		}
		var emailParts = user.getEmail().split("@");
		if (emailParts.length > 2) {
			return badRequestOptional("UC-005", "Email must contain only one @ symbol.", user.getEmail());
		}
		var emailDomain = emailParts[1]; 
		if (!emailDomain.contains(".")) {
			return badRequestOptional("UC-006", "Email domain must contain a dot.", user.getEmail());
		}
		if (emailDomain.startsWith(".") || emailDomain.endsWith(".")) {
			return badRequestOptional("UC-007", "Email domain must not start or end with a dot.", user.getEmail());
		}
		if (!user.getGivenName().chars().allMatch(Character::isLetter)) {
			return badRequestOptional("UC-008", "Given name can only contain letters.", user.getGivenName());
		}
		if (!user.getFamilyName().replace("'", "").chars().allMatch(Character::isLetter)) {
			return badRequestOptional("UC-009", "Family name can only contain letters.", user.getFamilyName());
		}
		return Optional.empty();
	}
	
	@PostMapping("/users")
	@ResponseBody
	public CreationResponse createUser(@RequestBody ProtoUser user) {
		var optError = validateProtoUser(user);
		if (optError.isPresent()) {
			throw optError.get();
		}
		return new CreationResponse(userDao.createUser(user));
	}
	
	@PutMapping("/users/{id}")
	@ResponseBody
	public String updateUser(@PathVariable("id") int id, @RequestBody ProtoUser user) {
		var optError = validateProtoUser(user);
		if (optError.isPresent()) {
			throw optError.get();
		}
		userDao.updateUser(id, user);
		return "";
	}
	
	@DeleteMapping("/users/{id}")
	@ResponseBody
	public String deleteUser(@PathVariable("id") int id) {
		userDao.deleteUser(id);
		return "";
	}
}
