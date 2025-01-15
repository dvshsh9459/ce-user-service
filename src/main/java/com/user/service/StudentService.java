package com.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.user.config.CustomDetailsService;
import com.user.config.JwtHelper;
import com.user.controller.request.RemoveStuRequest;
import com.user.controller.request.StudentLoginRequest;
import com.user.controller.request.StudentRegRequest;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.StudentRepository;
import com.user.repository.UserRepository;
import com.user.repository.entity.Role;
import com.user.repository.entity.Student;
import com.user.repository.entity.User;

import io.jsonwebtoken.Claims;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private JwtHelper helper;
	@Autowired
	private CustomDetailsService customDetailsService;
	@Autowired
	private UserRepository userRepository;

	public ResponseEntity<UserResponse> studentRegistration(StudentRegRequest regRequest) {
		// Check if the student already exists by email
		Student existingStudent = studentRepository.findByEmail(regRequest.getEmail());
		if (existingStudent != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("User already exists", false, HttpStatus.CONFLICT.value()));
		}

		User user = User.builder().email(regRequest.getEmail()).password(regRequest.getPassword()).role(Role.STUDENT)
				.build();
		user = userRepository.save(user);
		Student student = Student.builder().email(regRequest.getEmail()).aadharCardNo(regRequest.getAadharCardNo())
				.contactNo(regRequest.getContactNo()).name(regRequest.getName()).password(regRequest.getPassword())
				.qualification(regRequest.getQualification()).role(Role.STUDENT).build();
		System.out.println(student);

		studentRepository.save(student);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("User registered successfully", true, HttpStatus.OK.value()));
	}

	public ResponseEntity<AuthResponse> studentLogin(StudentLoginRequest loginRequest) {
		Student student = studentRepository.findByEmail(loginRequest.getEmail());
		String token = null;
		if (student != null && student.getPassword().equals(loginRequest.getPassword())) {
			UserDetails details = customDetailsService.loadUserByUsername(student.getEmail());
			token = helper.generateToken(details, student.getPassword(), Role.STUDENT);
			String existingtoken = helper.getOrGenerateToken(student.getEmail(), student.getPassword(), Role.STUDENT);

			Claims claims1 = JwtHelper.decodeJwt(existingtoken);
			Claims claims2 = JwtHelper.decodeJwt(token);
			System.out.println(token);
			if (claims1.getSubject().equals(claims2.getSubject())) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new AuthResponse("Student login Successfully", true, HttpStatus.OK.value(), token));
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"Login failed ! Inavlid email or password", false, HttpStatus.UNAUTHORIZED.value(), token));
	}

	public List<Student> getAllStudents() {
		return studentRepository.findAll();
	}

	public ResponseEntity<UserResponse> removeStudent(RemoveStuRequest removeStuRequest) {
		Student student = studentRepository.findByEmail(removeStuRequest.getEmail());
		if (student == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new UserResponse("User Does Not Exists", false, HttpStatus.NOT_FOUND.value()));
		}

		studentRepository.delete(student);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Student Removed Successfully", true, HttpStatus.OK.value()));

	}


}
