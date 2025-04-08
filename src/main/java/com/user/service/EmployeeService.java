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
import com.user.controller.request.EmployeeLoginRequest;
import com.user.controller.request.EmployeeRegisterRequest;
import com.user.controller.request.ForgetPasswordRequest;
import com.user.controller.request.RemoveEmpRequest;
import com.user.controller.request.UpdatePasswordReq;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.ForgetPassResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.EmployeeRepository;

import com.user.repository.RoleRepository;

import com.user.repository.entity.Employee;

import com.user.repository.entity.Role;
import com.user.service.kafkaeventservice.EmailProducer;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {

	private final JwtHelper helper;
	private RoleRepository roleRepository;
	private Random random = new Random();

	private EmployeeRepository employeeRepository;
	private EmailProducer emailProducer;

	public EmployeeService(JwtHelper helper, EmployeeRepository employeeRepository, RoleRepository roleRepository,
			EmailProducer emailProducer) {
		this.helper = helper;
		this.roleRepository = roleRepository;
		this.employeeRepository = employeeRepository;
		this.emailProducer = emailProducer;
	}

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public ResponseEntity<UserResponse> teacherRegisteration(EmployeeRegisterRequest registerRequest) {
		Employee existedEmployee = employeeRepository.findByEmail(registerRequest.getEmail());
		if (existedEmployee != null) {
			log.warn("Registeration failed !! Employee already exists with email:{}", registerRequest.getEmail());
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("Employee already exists ", false, HttpStatus.CONFLICT.value()));
		}

		Role role = roleRepository.findByRole(registerRequest.getRoles());
		if (role == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new UserResponse("Role not found", false, HttpStatus.BAD_REQUEST.value()));
		}
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		String verificationCode = GenerateVerification.generateVerificationCode();

		Employee employee = Employee.builder().email(registerRequest.getEmail())
				.password(encoder.encode(registerRequest.getPassword())).roles(roles).isVerified(false)
				.verificationCode(verificationCode).build();

		employeeRepository.save(employee);
		emailProducer.sendVerificationEmail(registerRequest.getEmail(), verificationCode);
		log.info("Employee register successfully with email:{}", registerRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Verification code sent to email. Please verify.", true, HttpStatus.OK.value()));
	}

	public ResponseEntity<UserResponse> verifyStudent(String email, String code) {
		Employee employee = employeeRepository.findByEmail(email);
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new UserResponse("Student not found", false, HttpStatus.NOT_FOUND.value()));
		}

		if (employee.getVerificationCode().equals(code)) {
			employee.setVerified(true);
			employee.setVerificationCode(null); // Remove code after verification
			employeeRepository.save(employee);
			return ResponseEntity.status(HttpStatus.OK)
					.body(new UserResponse("Email verified successfully!", true, HttpStatus.OK.value()));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new UserResponse("Invalid verification code", false, HttpStatus.BAD_REQUEST.value()));
		}
	}

	public ResponseEntity<AuthResponse> employeeLogin(EmployeeLoginRequest loginRequest) {
		Employee employee = employeeRepository.findByEmail(loginRequest.getEmail());
		String token = null;
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new AuthResponse("User Not Found With email ", false, HttpStatus.NOT_FOUND.value(), token, null));

		}
		boolean hasRole = employee.getRoles().stream()
				.anyMatch(r -> r.getRole().equalsIgnoreCase(loginRequest.getRole()));

		if (!hasRole) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
					"User does not have the specified role", false, HttpStatus.UNAUTHORIZED.value(), token, null));
		}
		if (encoder.matches(loginRequest.getPassword(), employee.getPassword())) {
			// Generate token only with email
			token = helper.generateToken(employee.getEmail(), employee.getRoles());
			Date expiry = helper.getExpirationDate(token);

			log.info("Employee login successfully with email: {}", loginRequest.getEmail());

			return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse("Employee login successfully", true,
					HttpStatus.OK.value(), token, expiry.toString()));
		}

		log.warn("Employee login failed with email: {}", loginRequest.getEmail());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new AuthResponse("Employee login failed! Invalid email or password", false,
						HttpStatus.UNAUTHORIZED.value(), null, null));
	}

	public List<Employee> employees() {
		return employeeRepository.findAll();
	}

	public ResponseEntity<UserResponse> removeEmployee(RemoveEmpRequest empRequest) {
		Employee employee = employeeRepository.findByEmail(empRequest.getEmail());
		if (employee == null) {
			log.warn("Attempted to remove employee, but no employee found with email: {}", empRequest.getEmail());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new UserResponse("Employee does not exists", false, HttpStatus.NOT_FOUND.value()));
		}
		employeeRepository.delete(employee);
		log.info("Employee removed successfully with email:{}", empRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Employee removed successfully", true, HttpStatus.OK.value()));

	}

	public ResponseEntity<UserResponse> updatePassword(UpdatePasswordReq passwordReq) {
		Employee employee = employeeRepository.findByEmail(passwordReq.getEmail());
		if (employee != null && encoder.matches(passwordReq.getOldPassword(), employee.getPassword())) {
			if (passwordReq.getNewPassword().equals(passwordReq.getConfirmPassword())) {
				employee.setPassword(encoder.encode(passwordReq.getNewPassword()));
				employeeRepository.save(employee);
				log.warn("Password Updated For Email:{}", passwordReq.getEmail());
				return ResponseEntity.status(HttpStatus.OK)
						.body(new UserResponse("Password updated Successfully", true, HttpStatus.OK.value()));
			} else {
				log.error("Password update failed: New password and confirm password do not match for request: {}",
						passwordReq.getEmail());

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserResponse(
						"New Password And Confirm Password Does Not Match", true, HttpStatus.BAD_REQUEST.value()));

			}
		}
		log.error("Password update failed: Email Or Password do not match for request: {}", passwordReq.getEmail());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new UserResponse("Invalid Email Or Password", false, HttpStatus.BAD_REQUEST.value()));

	}

	public ResponseEntity<ForgetPassResponse> forgetPassword(ForgetPasswordRequest forgetPassword) {
		Employee employee = employeeRepository.findByEmail(forgetPassword.getEmail());
		if (employee == null) {
			log.warn("Password reset attempt failed: No employee found with email {}", forgetPassword.getEmail());
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ForgetPassResponse("Employee Not Found ", " ", HttpStatus.NOT_FOUND.value()));

		}
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

		int length = 10;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(AB.charAt(random.nextInt(AB.length())));
		}

		employee.setPassword(encoder.encode(sb));
		employeeRepository.save(employee);
		log.info("Password reset Successsfully with email:{}", forgetPassword.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(new ForgetPassResponse("Password Forget Successfully",
				"Login Password is " + sb.toString(), HttpStatus.OK.value()));

	}

}
