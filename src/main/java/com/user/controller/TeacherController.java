package com.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.controller.request.TeacherLoginRequest;
import com.user.controller.request.TeacherRegisterRequest;
import com.user.controller.response.UserResponse;
import com.user.service.TeacherService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/teacher")
public class TeacherController {

	@Autowired
	private TeacherService teacherService;

	@PostMapping("/registeration")
	public ResponseEntity<UserResponse> teacherRegister(@RequestBody TeacherRegisterRequest registerRequest) {
		return teacherService.teacherRegisteration(registerRequest);
	}

	@GetMapping("/login")
	public ResponseEntity<UserResponse> teacherLogin(@RequestBody TeacherLoginRequest loginRequest) {
		return teacherService.teacherLogin(loginRequest);
	}

}
