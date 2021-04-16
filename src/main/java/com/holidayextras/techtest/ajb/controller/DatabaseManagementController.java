package com.holidayextras.techtest.ajb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.holidayextras.techtest.ajb.database.UserDao;
import com.holidayextras.techtest.ajb.domain.CreationResponse;
import com.holidayextras.techtest.ajb.domain.ProtoUser;
import com.holidayextras.techtest.ajb.domain.UserStats;
import com.holidayextras.techtest.ajb.errorhandling.ApplicationStatusException;
import com.holidayextras.techtest.ajb.testdata.RandomUserGenerator;
import com.holidayextras.techtest.ajb.testdata.TestData;
import com.holidayextras.techtest.ajb.testdata.TestDataset;

/**
 * Controller for database management operations: dealing with user stats, and creating/activating test data (which is held in-memory, at least until activation).
 */
@Controller
@RequestMapping("/db-mgmt")
public class DatabaseManagementController {
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private TestData testData;
	
	@Autowired
	private RandomUserGenerator randomUserGenerator;
	
	@PostMapping("/user-stats")
	@ResponseBody
	public String updateUserStats(@RequestBody UserStats stats) {
		if (stats.getNumUsers() == 0) {
			userDao.deleteAllUsers();
			testData.deactivateAll();
			return "";
		} else {
			throw new ApplicationStatusException("DMC-001", HttpStatus.BAD_REQUEST, "Cannot update user count to non-zero value. Add or remove specific users instead.", Integer.toString(stats.getNumUsers()));
		}
	}
	
	private void activateTestData(TestDataset dataset) {
		List<ProtoUser> usersToCreate;
		if (dataset.isRandom()) {
			usersToCreate = randomUserGenerator.generateRandomUsers(dataset.getNumUsers());
		} else {
			usersToCreate = dataset.getUsers();
		}
		userDao.createMultipleUsers(usersToCreate);
	}
	
	@PostMapping("/test-datasets")
	@ResponseBody
	public CreationResponse createTestDataset(@RequestBody TestDataset dataset) {
		if (!dataset.isRandom()) {
			throw new ApplicationStatusException("DMC-002", HttpStatus.BAD_REQUEST, "Cannot create non-random datasets.", "false");
		} else if (dataset.getNumUsers() <= 0) {
			throw new ApplicationStatusException("DMC-003", HttpStatus.BAD_REQUEST, "Must provide a positive number of users.", Integer.toString(dataset.getNumUsers()));
		} else {
			var newId = testData.addTestData(dataset);
			if (dataset.isActivated()) {
				activateTestData(dataset);
			}
			return new CreationResponse(newId);
		}
	}

	@PostMapping("/test-datasets/{id}/activated")
	@ResponseBody
	public String updateTestDataset(@PathVariable("id") int id, @RequestBody TestDataset dataset) {
		var currentTestDataset = testData.getTestDataset(id);
		if (currentTestDataset == null) {
			throw new ApplicationStatusException("DMC-004", HttpStatus.BAD_REQUEST, "Invalid test dataset ID supplied.", Integer.toString(id));
		} else if (dataset.isActivated() != currentTestDataset.isActivated()) {
			if (dataset.isActivated()) {
				testData.markAsActivated(id);
				activateTestData(currentTestDataset);
			} else {
				throw new ApplicationStatusException("DMC-005", HttpStatus.BAD_REQUEST, "Cannot deactivate an already-activated dataset.", "false");
			}
		}
		return "";
	}
}
