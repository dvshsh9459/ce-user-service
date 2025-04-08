package com.user.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import com.user.config.JwtHelper;
import com.user.controller.request.ForgetPasswordRequest;
import com.user.controller.request.RemoveStuRequest;
import com.user.controller.request.StudentLoginRequest;
import com.user.controller.request.StudentRegRequest;
import com.user.controller.request.UpdatePasswordReq;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.ForgetPassResponse;
import com.user.controller.response.UserResponse;

import com.user.repository.RoleRepository;
import com.user.repository.StudentRepository;
import com.user.repository.entity.Role;
import com.user.repository.entity.Student;

import com.user.service.kafkaeventservice.EmailProducer;
import com.user.service.kafkaeventservice.LoginEventProducer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StudentService {

	private final JwtHelper helper;
	private RoleRepository roleRepository;

	private StudentRepository studentRepository;
	private EmailProducer emailProducer;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	private LoginEventProducer loginEventProducer;
	private Random random = new Random();
	public StudentService(JwtHelper helper, StudentRepository studentRepository, RoleRepository roleRepository,
			EmailProducer emailProducer, LoginEventProducer loginEventProducer) {
		this.helper = helper;
		this.roleRepository = roleRepository;
		this.studentRepository = studentRepository;
		this.emailProducer = emailProducer;
		this.loginEventProducer = loginEventProducer;

	}

	public ResponseEntity<UserResponse> studentRegistration(StudentRegRequest regRequest) {
		// Check if the student already exists by email
		Student existingStudent = studentRepository.findByEmail(regRequest.getEmail());
		if (existingStudent != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("Student already exists", false, HttpStatus.CONFLICT.value()));
		}

		// Check if role exists
		Role role = roleRepository.findByRole(regRequest.getRole());
		if (role == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new UserResponse("Role not found", false, HttpStatus.BAD_REQUEST.value()));
		}

		Set<Role> roles = new HashSet<>();
		roles.add(role);

		// Generate a random verification code
		String verificationCode = GenerateVerification.generateVerificationCode();

		// Create student (not yet verified)
		Student student = Student.builder().email(regRequest.getEmail())
				.password(encoder.encode(regRequest.getPassword())).roles(roles).verificationCode(verificationCode)
				.isVerified(false) // Initially false
				.build();

		studentRepository.save(student);
		emailProducer.sendVerificationEmail(regRequest.getEmail(), verificationCode);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Verification code sent to email. Please verify.", true, HttpStatus.OK.value()));
	}
	
	

	// Verify the student's email
	public ResponseEntity<UserResponse> verifyStudent(String email, String code) {
		Student student = studentRepository.findByEmail(email);
		if (student == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new UserResponse("Student not found", false, HttpStatus.NOT_FOUND.value()));
		}

		if (student.getVerificationCode().equals(code)) {
			student.setVerified(true);
			student.setVerificationCode(null); // Remove code after verification
			studentRepository.save(student);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new UserResponse("Email verified successfully!", true, HttpStatus.OK.value()));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new UserResponse("Invalid verification code", false, HttpStatus.BAD_REQUEST.value()));
		}
	}

	public ResponseEntity<AuthResponse> studentLogin(StudentLoginRequest loginRequest) {
		Student student = studentRepository.findByEmail(loginRequest.getEmail());
		String token = null;
		if (student == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new AuthResponse("User Not Found With email {}", false, HttpStatus.NOT_FOUND.value(), token, null));
		}
		boolean hasRole = student.getRoles().stream()
				.anyMatch(r -> r.getRole().equalsIgnoreCase(loginRequest.getRole()));

		if (!hasRole) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
					"User does not have the specified role", false, HttpStatus.UNAUTHORIZED.value(), token, null));
		}

		if (encoder.matches(loginRequest.getPassword(), student.getPassword())) {
			// Generate token only with email
			token = helper.generateToken(student.getEmail(), student.getRoles());
			Date expiry = helper.getExpirationDate(token);

			log.info("User login successfully with email: {}", loginRequest.getEmail());

			return ResponseEntity.status(HttpStatus.OK).body(
					new AuthResponse("User login successfully", true, HttpStatus.OK.value(), token, expiry.toString()));
		}

		log.warn("User login failed with email: {}", loginRequest.getEmail());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"User login failed! Invalid email or password", false, HttpStatus.UNAUTHORIZED.value(), null, null));
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
		log.info("User removed successfully with email:{}", removeStuRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("User Removed Successfully", true, HttpStatus.OK.value()));

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
			log.warn("Password reset attempt failed: No User found with email {}", forgetPassword.getEmail());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ForgetPassResponse("User Not Found ", " ", HttpStatus.NOT_FOUND.value()));

		}
		String pass = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		int length = 10;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(pass.charAt(random.nextInt(pass.length())));
		}
		student.setPassword(encoder.encode(sb));
		studentRepository.save(student);
		log.info("Password reset Successsfully with email:{}", forgetPassword.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(new ForgetPassResponse("Password Forget Successfully",
				"Password For Login is " + sb.toString(), HttpStatus.OK.value()));

	}

}
