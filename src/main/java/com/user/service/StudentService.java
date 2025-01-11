package com.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.user.controller.request.StudentLoginRequest;
import com.user.controller.request.StudentRegRequest;
import com.user.controller.response.UserResponse;
import com.user.repository.StudentRepository;
import com.user.repository.entity.Student;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;





		Student existedStudent = studentRepository.findByEmail(regRequest.getEmail());
		if (existedStudent != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("User Already exists ", false, HttpStatus.CONFLICT.value()));
		}
		Student student = new Student();
		student.setEmail(regRequest.getEmail());
		student.setPassword(regRequest.getPassword());
		student.setContactNo(regRequest.getPhoneNo());
		studentRepository.save(student);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("User Register Successfully ", true, HttpStatus.OK.value()));

	}

	public ResponseEntity<UserResponse> studentLogin(StudentLoginRequest loginRequest) {
		Student student = studentRepository.findByEmail(loginRequest.getEmail());
		if (student != null && student.getPassword().equals(loginRequest.getPassword())) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new UserResponse("Student login Successfully", true, HttpStatus.OK.value()));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
				new UserResponse("Login failed ! Inavlid email or password", false, HttpStatus.UNAUTHORIZED.value()));
	}

}
