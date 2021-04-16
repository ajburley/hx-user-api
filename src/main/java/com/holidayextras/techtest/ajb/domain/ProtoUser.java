package com.holidayextras.techtest.ajb.domain;

/**
 * POJO to specify a user which has been detailed but does not (yet) represent a concrete record in the database.
 */
public class ProtoUser {
	private String email;
	private String givenName;
	private String familyName;
	
	public ProtoUser() {
	}

	public ProtoUser(String email, String givenName, String familyName) {
		this.email = email;
		this.givenName = givenName;
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
}