package com.holidayextras.techtest.ajb.errorhandling;

/**
 * POJO to represent the error object format expected by the client.
 */
public class ClientError {
	private String errorCode;
	private String message;
	private String erroneousValue;

	public ClientError() {
	}

	public ClientError(String errorCode, String message, String erroneousValue) {
		this.errorCode = errorCode;
		this.message = message;
		this.erroneousValue = erroneousValue;
	}
	public ClientError(String errorCode, String message) {
		this(errorCode, message, null);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErroneousValue() {
		return erroneousValue;
	}

	public void setErroneousValue(String erroneousValue) {
		this.erroneousValue = erroneousValue;
	}
}
