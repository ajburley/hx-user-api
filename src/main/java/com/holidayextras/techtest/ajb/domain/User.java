package com.holidayextras.techtest.ajb.domain;

import java.time.Instant;

/**
 * Full user POJO which represents a user which has been added to the database.
 */
public class User extends ProtoUser {
	private int id;
	private Instant created;
	public User(int id, String email, String givenName, String familyName, Instant created) {
		super(email, givenName, familyName);
		this.id = id;
		this.created = created;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Instant getCreated() {
		return created;
	}
	public void setCreated(Instant created) {
		this.created = created;
	}
}
