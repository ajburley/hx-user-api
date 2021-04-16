package com.holidayextras.techtest.ajb.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

/**
 * This class takes application exceptions and converts them into the format expected by the client.
 * It also prevents information leakage by mapping non-application exceptions to generic error codes.
 */
@ControllerAdvice
public class ApplicationExceptionHandler {
	private static final String DEFAULT_ERROR = "Unknown error occurred";
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ClientError> handleException(Exception ex) {
		if (ex instanceof ApplicationStatusException) {
			var ase = (ApplicationStatusException)ex;
			if (ase.getErroneousValue() == null) {
				return new ResponseEntity<>(new ClientError(ase.getErrorCode(), ase.getReason()), ase.getStatus());
			} else {
				return new ResponseEntity<>(new ClientError(ase.getErrorCode(), ase.getReason(), ase.getErroneousValue()), ase.getStatus());
			}
		} else if (ex instanceof MethodArgumentTypeMismatchException) {
			var matme = (MethodArgumentTypeMismatchException)ex;
			if ((((MethodArgumentTypeMismatchException) ex).getValue() instanceof String)
					&& (int.class.equals(matme.getRequiredType()))) {
				return new ResponseEntity<>(new ClientError("GEN-002", "Expected an integer, but got: "+matme.getValue(), (String) matme.getValue()), HttpStatus.BAD_REQUEST);
			}
		} else {
			InvalidFormatException ife = null;
			if (ex instanceof InvalidFormatException) {
				ife = (InvalidFormatException) ex;
			} else if (ex.getCause() instanceof InvalidFormatException) {
				ife = (InvalidFormatException) ex.getCause();
			}
			if ((ife != null)
				&& (ife.getValue() instanceof String)
				&& (ife.getTargetType().equals(int.class))) {
				return new ResponseEntity<>(new ClientError("GEN-002", "Expected an integer, but got: "+ife.getValue(), (String) ife.getValue()), HttpStatus.BAD_REQUEST);
			}
		}
		return new ResponseEntity<>(new ClientError("GEN-001", DEFAULT_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
