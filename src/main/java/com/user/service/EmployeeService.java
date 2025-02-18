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
import com.user.controller.request.EmployeeLoginRequest;
import com.user.controller.request.EmployeeRegisterRequest;
import com.user.controller.request.ForgetPasswordRequest;
import com.user.controller.request.RemoveEmpRequest;
import com.user.controller.request.UpdatePasswordReq;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.ForgetPassResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.EmployeeRepository;
import com.user.repository.JwtRepository;
import com.user.repository.RoleRepository;
import com.user.repository.entity.Employee;
import com.user.repository.entity.JwtToken;
import com.user.repository.entity.Role;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private JwtHelper helper;
	@Autowired
	private CustomDetailsService customDetailsService;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	@Autowired
	private JwtRepository jwtRepository;
	@Autowired
	private RoleRepository roleRepository;

	public ResponseEntity<UserResponse> teacherRegisteration(EmployeeRegisterRequest registerRequest) {
		Employee existedEmployee = employeeRepository.findByEmail(registerRequest.getEmail());
		if (existedEmployee != null) {
			log.warn("Registeration failed !! Employee already exists with email:{}", registerRequest.getEmail());
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("Employee already exists ", false, HttpStatus.CONFLICT.value()));
		}
         
		
		Role role = roleRepository.findByRole("EMPLOYEE");
		Employee employee = Employee.builder().email(registerRequest.getEmail())
				.password(encoder.encode(registerRequest.getPassword())).role(role).build();

		employeeRepository.save(employee);
		log.info("Employee register successfully with email:{}", registerRequest.getEmail());
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Employee register successfully ", true, HttpStatus.OK.value()));

	}

	public ResponseEntity<AuthResponse> teacherLogin(EmployeeLoginRequest loginRequest) {
		Employee employee = employeeRepository.findByEmail(loginRequest.getEmail());
		Role role= roleRepository.findByRole("Employee");
		String token = null;
		if (employee != null && employee.getPassword().equals(loginRequest.getPassword())) {
			UserDetails details = customDetailsService.loadUserByUsername(employee.getEmail());
			token = helper.generateToken(details, employee.getPassword(),role);
			String existingtoken = helper.getOrGenerateToken(employee.getEmail(), employee.getPassword(),
					role);

			Claims claims1 = JwtHelper.decodeJwt(existingtoken);
			Claims claims2 = JwtHelper.decodeJwt(token);
			System.out.println(token);
			if (claims1.getSubject().equals(claims2.getSubject())) {
				log.info("Employee login successfully with email:{}", loginRequest.getEmail());
				return ResponseEntity.status(HttpStatus.OK)
						.body(new AuthResponse("Employee login successfully", true, HttpStatus.OK.value(), token));
			}
		}
		log.warn("Employee login failed with email:{}", loginRequest.getEmail());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"Employee login failed ! invalid email and password", false, HttpStatus.UNAUTHORIZED.value(), token));

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
		Random rnd = new Random();
		int length = 10;
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
		System.out.println(sb.toString());
		employee.setPassword(encoder.encode(sb));
		employeeRepository.save(employee);
		JwtToken jwtToken = jwtRepository.findByEmail(forgetPassword.getEmail());
		jwtRepository.delete(jwtToken);
		log.info("Password reset Successsfully with email:{}", forgetPassword.getEmail());
		return ResponseEntity.status(HttpStatus.OK).body(new ForgetPassResponse("Password Forget Successfully",
				"Login Password is " + sb.toString(), HttpStatus.OK.value()));

	}

}
