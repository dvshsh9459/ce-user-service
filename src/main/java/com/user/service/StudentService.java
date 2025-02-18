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
import com.user.controller.request.ForgetPasswordRequest;
import com.user.controller.request.RemoveStuRequest;
import com.user.controller.request.StudentLoginRequest;
import com.user.controller.request.StudentRegRequest;
import com.user.controller.request.UpdatePasswordReq;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.ForgetPassResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.JwtRepository;
import com.user.repository.RoleRepository;
import com.user.repository.StudentRepository;

import com.user.repository.entity.JwtToken;
import com.user.repository.entity.Role;
import com.user.repository.entity.Student;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;
	@Autowired
	private JwtHelper helper;
	@Autowired
	private CustomDetailsService customDetailsService;


	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	@Autowired
	private JwtRepository jwtRepository;

	@Autowired
	private RoleRepository roleRepository;

	public ResponseEntity<UserResponse> studentRegistration(StudentRegRequest regRequest) {
		// Check if the student already exists by email
		Student existingStudent = studentRepository.findByEmail(regRequest.getEmail());
		if (existingStudent != null) {
			log.warn("Student registration attempt failed: User already exists with email {}", regRequest.getEmail());
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("Student already exists", false, HttpStatus.CONFLICT.value()));
		}

      
		Role role = roleRepository.findByRole(regRequest.getRole());
		Student student = Student.builder().email(regRequest.getEmail())
				.password(encoder.encode(regRequest.getPassword())).role(role).build();


		System.out.println(student);

		studentRepository.save(student);
		log.info("Student register Successfully with email {}", regRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Student registered successfully", true, HttpStatus.OK.value()));
	}

	public ResponseEntity<AuthResponse> studentLogin(StudentLoginRequest loginRequest) {
		Student student = studentRepository.findByEmail(loginRequest.getEmail());
		Role role= roleRepository.findByRole(loginRequest.getRole());
		String token = null;
		if (student != null && encoder.matches(loginRequest.getPassword(), student.getPassword())) {
			UserDetails details = customDetailsService.loadUserByUsername(student.getEmail());
			token = helper.generateToken(details, student.getPassword(),role);
			String existingtoken = helper.getOrGenerateToken(student.getEmail(), student.getPassword(), role);

			Claims claims1 = JwtHelper.decodeJwt(existingtoken);
			Claims claims2 = JwtHelper.decodeJwt(token);
			System.out.println(token);
			if (claims1.getSubject().equals(claims2.getSubject())) {
				log.info("Student Login Successfully With Email:{}", loginRequest.getEmail());
				return ResponseEntity.status(HttpStatus.OK)
						.body(new AuthResponse("Student login Successfully", true, HttpStatus.OK.value(), token));
			}
		}
		log.warn("Login attempt failed: Invalid email or password for email {}", loginRequest.getEmail());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"Login failed ! Inavlid email or password", false, HttpStatus.UNAUTHORIZED.value(), token));
	}

	public List<Student> getAllStudents() {
		return studentRepository.findAll();
	}

	public ResponseEntity<UserResponse> removeStudent(RemoveStuRequest removeStuRequest) {
		Student student = studentRepository.findByEmail(removeStuRequest.getEmail());
		if (student == null) {
			log.warn("Attempted to remove Student, but no student found with email: {}", removeStuRequest.getEmail());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new UserResponse("User Does Not Exists", false, HttpStatus.NOT_FOUND.value()));
		}

		studentRepository.delete(student);
		log.info("Student removed successfully with email:{}", removeStuRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Student Removed Successfully", true, HttpStatus.OK.value()));

	}

	public ResponseEntity<UserResponse> updatePassword(UpdatePasswordReq passwordReq) {
		Student student = studentRepository.findByEmail(passwordReq.getEmail());
		if (student != null && encoder.matches(passwordReq.getOldPassword(), student.getPassword())) {
			if (passwordReq.getNewPassword().equals(passwordReq.getConfirmPassword())) {
				student.setPassword(encoder.encode(passwordReq.getNewPassword()));
				studentRepository.save(student);
				log.info("Password updated successfully for email:{}", passwordReq.getEmail());
				return ResponseEntity.status(HttpStatus.OK)
						.body(new UserResponse("Password updated Successfully", true, HttpStatus.OK.value()));
			} else {
				log.error("Password update failed: New password and confirm password do not match for request: {}",
						passwordReq.getEmail());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponse(
						"New Password And Confirm Password does not match", true, HttpStatus.BAD_REQUEST.value()));

			}
		}
		log.error("Password update failed: Email Or Password do not match for request: {}", passwordReq.getEmail());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new UserResponse("Invalid Email Or Password", false, HttpStatus.BAD_REQUEST.value()));

	}

	public ResponseEntity<ForgetPassResponse> forgetPassword(ForgetPasswordRequest forgetPassword) {
		Student student = studentRepository.findByEmail(forgetPassword.getEmail());
		if (student == null) {
			log.warn("Password reset attempt failed: No employee found with email {}", forgetPassword.getEmail());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ForgetPassResponse("Student Not Found ", " ", HttpStatus.NOT_FOUND.value()));

		}
		String pass = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		Random rnd = new Random();
		int length = 10;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(pass.charAt(rnd.nextInt(pass.length())));
		}
		System.out.println(sb.toString());
		student.setPassword(encoder.encode(sb));
		studentRepository.save(student);
		JwtToken jwtToken = jwtRepository.findByEmail(forgetPassword.getEmail());
		jwtRepository.delete(jwtToken);
		log.info("Password reset Successsfully with email:{}", forgetPassword.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(new ForgetPassResponse("Password Forget Successfully",
				"Password For Login is " + sb.toString(), HttpStatus.OK.value()));

	}

}
