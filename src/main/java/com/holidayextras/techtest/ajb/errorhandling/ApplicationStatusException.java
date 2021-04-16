package com.holidayextras.techtest.ajb.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Application-specific extension of the default Spring @{code ResponseStatusException} allowing HTTP status to be specified against exceptions.
 * This has two benefits: (a) we can identify errors thrown from the application rather than from Spring framework code
 * (in order to prevent information leakage), and (b) we can append additional fields to the exception class for i18n.
 */
@SuppressWarnings("java:S110") // Extension from Spring-managed class
public class ApplicationStatusException extends ResponseStatusException {
	private static final long serialVersionUID = -6562468128884457649L;
	private final String errorCode;
	private final String erroneousValue;
	public ApplicationStatusException(String errorCode, HttpStatus status, String reason, String erroneousValue, Throwable cause) {
		super(status, reason, cause);
		this.errorCode = errorCode;
		this.erroneousValue = erroneousValue;
	}
	public ApplicationStatusException(String errorCode, HttpStatus status, String reason, String erroneousValue) {
		super(status, reason);
		this.errorCode = errorCode;
		this.erroneousValue = erroneousValue;
	}
	public ApplicationStatusException(String errorCode, HttpStatus status, String reason, Throwable cause) {
		this(errorCode, status, reason, null, cause);
	}
	public ApplicationStatusException(String errorCode, HttpStatus status, String reason) {
		this(errorCode, status, reason, (String)null);
	}
	public String getErrorCode() {
		return errorCode;
	}
	public String getErroneousValue() {
		return erroneousValue;
	}
}
