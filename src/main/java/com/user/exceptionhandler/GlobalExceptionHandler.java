package com.user.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	
	@ExceptionHandler(UserException.class)
	public ResponseEntity<String> getException(UserException userException){
		return new ResponseEntity<String>("user not found ",HttpStatus.BAD_GATEWAY);
		 
	}
	

}
