package com.holidayextras.techtest.ajb.domain;

/**
 * POJO to hold a response to be serialized and returned from creation endpoints.
 */
public class CreationResponse {
	private int newId;

	public CreationResponse() {
	}

	public CreationResponse(int newId) {
		this.newId = newId;
	}

	public int getNewId() {
		return newId;
	}

	public void setNewId(int newId) {
		this.newId = newId;
	}
}
