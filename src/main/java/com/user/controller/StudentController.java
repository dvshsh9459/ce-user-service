package com.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.controller.request.StudentLoginRequest;
import com.user.controller.request.StudentRegRequest;
import com.user.controller.response.UserResponse;
import com.user.service.StudentService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentService studentService;

	@PostMapping("/registeration")
	public ResponseEntity<UserResponse> registerStudent(@RequestBody StudentRegRequest regRequest) {
		return studentService.studentRegisteration(regRequest);
	}

	@GetMapping("/login")
	public ResponseEntity<UserResponse> loginStudent(@RequestBody StudentLoginRequest loginRequest) {
		return studentService.studentLogin(loginRequest);
	}

}
