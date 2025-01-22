package com.user.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.config.CustomDetailsService;
import com.user.config.JwtHelper;
import com.user.controller.request.ForgetPassword;
import com.user.controller.request.RemoveStuRequest;
import com.user.controller.request.StudentLoginRequest;
import com.user.controller.request.StudentRegRequest;
import com.user.controller.request.UpdatePasswordReq;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.ForgetPassResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.JwtRepository;
import com.user.repository.StudentRepository;

import com.user.repository.entity.JwtToken;
import com.user.repository.entity.Role;
import com.user.repository.entity.Student;

import io.jsonwebtoken.Claims;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private JwtHelper helper;
	@Autowired
	private CustomDetailsService customDetailsService;

	public ResponseEntity<UserResponse> studentRegistration(StudentRegRequest regRequest) {
		// Check if the student already exists by email
		Student existingStudent = studentRepository.findByEmail(regRequest.getEmail());
		if (existingStudent != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("User already exists", false, HttpStatus.CONFLICT.value()));
		}

		Student student = Student.builder().email(regRequest.getEmail()).aadharCardNo(regRequest.getAadharCardNo())
				.qualification(regRequest.getQualification()).role(Role.STUDENT).build();
		System.out.println(student);

		studentRepository.save(student);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("User registered successfully", true, HttpStatus.OK.value()));
	}

	public ResponseEntity<AuthResponse> studentLogin(StudentLoginRequest loginRequest) {
		Student student = studentRepository.findByEmail(loginRequest.getEmail());
		String token = null;
		if (student != null && encoder.matches(loginRequest.getPassword(), student.getPassword())) {
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

	public ResponseEntity<UserResponse> updatePassword(UpdatePasswordReq passwordReq) {
		Student student = studentRepository.findByEmail(passwordReq.getEmail());
		if (student != null && encoder.matches(passwordReq.getOldPassword(), student.getPassword())) {
			if (passwordReq.getNewPassword().equals(passwordReq.getConfirmPassword())) {
				student.setPassword(encoder.encode(passwordReq.getNewPassword()));
				studentRepository.save(student);
				return ResponseEntity.status(HttpStatus.OK)
						.body(new UserResponse("Password updated Successfully", true, HttpStatus.OK.value()));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponse(
						"New Password And Confirm Password Does Not Match", true, HttpStatus.BAD_REQUEST.value()));

			}
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new UserResponse("Invalid Email Or Password", false, HttpStatus.BAD_REQUEST.value()));

	}

	public ResponseEntity<ForgetPassResponse> forgetPassword(ForgetPassword forgetPassword) {
		Student student = studentRepository.findByEmail(forgetPassword.getEmail());
		if (student == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ForgetPassResponse("Student Not Found ", " ", HttpStatus.NOT_FOUND.value()));

		}
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		int length = 10;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		System.out.println(sb.toString());
		student.setPassword(encoder.encode(sb));
		studentRepository.save(student);
		JwtToken jwtToken = jwtRepository.findByEmail(forgetPassword.getEmail());
		jwtRepository.delete(jwtToken);
		return ResponseEntity.status(HttpStatus.OK).body(new ForgetPassResponse("Password Forget Successfully",
				"Login Password is " + sb.toString(), HttpStatus.OK.value()));

	}

}
