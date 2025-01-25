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
import com.user.repository.entity.Employee;
import com.user.repository.entity.JwtToken;
import com.user.repository.entity.Role;

import io.jsonwebtoken.Claims;

@Service
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

	public ResponseEntity<UserResponse> teacherRegisteration(EmployeeRegisterRequest registerRequest) {
		Employee existedEmployee = employeeRepository.findByEmail(registerRequest.getEmail());
		if (existedEmployee != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("User Already exists ", false, HttpStatus.CONFLICT.value()));
		}

		Employee employee = Employee.builder().name(registerRequest.getName()).email(registerRequest.getEmail())
				.password(encoder.encode(registerRequest.getPassword())).aadharCardNo(registerRequest.getAadharCardNo())
				.contactNumber(registerRequest.getContactNumber()).qualification(registerRequest.getQualification())
				.salary(registerRequest.getSalary()).role(Role.EMPLOYEE).build();

		employeeRepository.save(employee);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("User Register Successfully ", true, HttpStatus.OK.value()));

	}

	public ResponseEntity<AuthResponse> teacherLogin(EmployeeLoginRequest loginRequest) {
		Employee employee = employeeRepository.findByEmail(loginRequest.getEmail());
		String token = null;
		if (employee != null && employee.getPassword().equals(loginRequest.getPassword())) {
			UserDetails details = customDetailsService.loadUserByUsername(employee.getEmail());
			token = helper.generateToken(details, employee.getPassword(), Role.EMPLOYEE);
			String existingtoken = helper.getOrGenerateToken(employee.getEmail(), employee.getPassword(),
					Role.EMPLOYEE);

			Claims claims1 = JwtHelper.decodeJwt(existingtoken);
			Claims claims2 = JwtHelper.decodeJwt(token);
			System.out.println(token);
			if (claims1.getSubject().equals(claims2.getSubject())) {
				return ResponseEntity.status(HttpStatus.OK)
						.body(new AuthResponse("Student login Successfully", true, HttpStatus.OK.value(), token));
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"User Login Failed ! invalid email and password", false, HttpStatus.UNAUTHORIZED.value(), token));

	}

	public List<Employee> employees() {
		return employeeRepository.findAll();
	}

	public ResponseEntity<UserResponse> removeEmployee(RemoveEmpRequest empRequest) {
		Employee employee = employeeRepository.findByEmail(empRequest.getEmail());
		if (employee == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new UserResponse("Employee Does Not Exists", false, HttpStatus.NOT_FOUND.value()));
		}
		employeeRepository.delete(employee);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new UserResponse("Employee Removed Successfully", true, HttpStatus.OK.value()));

	}

	public ResponseEntity<UserResponse> updatePassword(UpdatePasswordReq passwordReq) {
		Employee employee = employeeRepository.findByEmail(passwordReq.getEmail());
		if (employee != null && encoder.matches(passwordReq.getOldPassword(), employee.getPassword())) {
			if (passwordReq.getNewPassword().equals(passwordReq.getConfirmPassword())) {
				employee.setPassword(encoder.encode(passwordReq.getNewPassword()));
				employeeRepository.save(employee);
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

	public ResponseEntity<ForgetPassResponse> forgetPassword(ForgetPasswordRequest forgetPassword) {
		Employee employee = employeeRepository.findByEmail(forgetPassword.getEmail());
		if (employee == null) {
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
		return ResponseEntity.status(HttpStatus.OK).body(new ForgetPassResponse("Password Forget Successfully",
				"Login Password is " + sb.toString(), HttpStatus.OK.value()));

	}

}
