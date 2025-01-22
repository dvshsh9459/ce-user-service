package com.user.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserException.class)
public ResponseEntity<MyErrorClass> getException(UserException exception){
	MyErrorClass errorClass =	MyErrorClass.builder().message(exception.getMessage()).localDateTime(LocalDateTime.now()).build();
	return new ResponseEntity<MyErrorClass>(errorClass,HttpStatus.BAD_GATEWAY);
		
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MyErrorClass> getException(MethodArgumentNotValidException exception){
		MyErrorClass errorClass =	MyErrorClass.builder().message(exception.getMessage()).localDateTime(LocalDateTime.now()).build();
		return new ResponseEntity<MyErrorClass>(errorClass,HttpStatus.BAD_GATEWAY);
			
		}
	@ExceptionHandler(UserException.class)
	public ResponseEntity<MyErrorClass> getException(ConstraintViolationException exception){
		MyErrorClass errorClass =	MyErrorClass.builder().message(exception.getMessage()).localDateTime(LocalDateTime.now()).build();
		return new ResponseEntity<MyErrorClass>(errorClass,HttpStatus.BAD_GATEWAY);
			
		}
}
