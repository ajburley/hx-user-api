package com.holidayextras.techtest.ajb.testdata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.holidayextras.techtest.ajb.domain.ProtoUser;

/**
 * POJO to represent a set of test data which may either consist of a specific set of test users, or an instruction to create a specified number of random users.
 */
public class TestDataset {
	private boolean activated = false;
	private boolean random = false;
	private int numUsers = 0;
	
	@JsonIgnore
	private List<ProtoUser> users = null;

	public TestDataset() {
	}

	public TestDataset(boolean activated, boolean random, int numUsers, List<ProtoUser> users) {
		this.activated = activated;
		this.random = random;
		this.numUsers = numUsers;
		this.users = users;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public List<ProtoUser> getUsers() {
		return users;
	}

	public void setUsers(List<ProtoUser> users) {
		this.users = users;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public int getNumUsers() {
		if (random) {
			return numUsers;
		} else {
			return users.size();
		}
	}

	public void setNumUsers(int numUsers) {
		this.numUsers = numUsers;
	}
}
