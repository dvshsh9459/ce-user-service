package com.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.user.controller.request.TeacherLoginRequest;
import com.user.controller.request.TeacherRegisterRequest;
import com.user.controller.response.UserResponse;
import com.user.repository.TeacherRepository;
import com.user.repository.entity.Teacher;

@Service
public class TeacherService {
	@Autowired
	private TeacherRepository repository;

	public ResponseEntity<UserResponse> teacherRegisteration(TeacherRegisterRequest registerRequest) {
		Teacher existedStudent = repository.findByEmail(registerRequest.getEmail());
		if (existedStudent != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("User Already exists ", false, HttpStatus.CONFLICT.value()));
		}

		Teacher teacher = new Teacher();

		teacher.setEmail(registerRequest.getEmail());
		teacher.setPassword(registerRequest.getPassword());
		teacher.setPhoneNo(registerRequest.getPhoneNo());
		repository.save(teacher);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("User Register Successfully ", true, HttpStatus.OK.value()));

	}

	public ResponseEntity<UserResponse> teacherLogin(TeacherLoginRequest loginRequest) {
		Teacher teacher = repository.findByEmail(loginRequest.getEmail());
		if (teacher != null && teacher.getPassword().equals(loginRequest.getPassword())) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new UserResponse("User login Successfully", true, HttpStatus.OK.value()));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserResponse(
				"User Login Failed ! invalid email and password", false, HttpStatus.UNAUTHORIZED.value()));
	}

}
