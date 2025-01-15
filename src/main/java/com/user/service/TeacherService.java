package com.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.user.controller.request.TeacherRegisterRequest;
import com.user.controller.response.UserResponse;
import com.user.repository.TeacherRepository;
import com.user.repository.entity.Employee;

@Service
public class TeacherService {
	@Autowired
	private TeacherRepository repository;

	public ResponseEntity<UserResponse> teacherRegisteration(TeacherRegisterRequest registerRequest) {
		Employee existedStudent = repository.findByEmail(registerRequest.getEmail());
		if (existedStudent != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("User Already exists ", false, HttpStatus.CONFLICT.value()));
		}

		Employee employee = new Employee();
	
		employee.setEmail(registerRequest.getEmail());
		employee.setPassword(registerRequest.getPassword());
		employee.setContactNumber(registerRequest.getPhoneNo());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("User Register Successfully ", true, HttpStatus.OK.value()));

	}

}
