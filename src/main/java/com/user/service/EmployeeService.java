package com.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.user.config.CustomDetailsService;
import com.user.config.JwtHelper;
import com.user.controller.request.EmployeeLoginRequest;
import com.user.controller.request.EmployeeRegisterRequest;
import com.user.controller.request.RemoveEmpRequest;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.EmployeeRepository;

import com.user.repository.UserRepository;
import com.user.repository.entity.Employee;

import com.user.repository.entity.Role;
import com.user.repository.entity.User;

import io.jsonwebtoken.Claims;

@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private JwtHelper helper;
	@Autowired
	private CustomDetailsService customDetailsService;

	public ResponseEntity<UserResponse> teacherRegisteration(EmployeeRegisterRequest registerRequest) {
		Employee existedEmployee = employeeRepository.findByEmail(registerRequest.getEmail());
		if (existedEmployee != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT)
					.body(new UserResponse("User Already exists ", false, HttpStatus.CONFLICT.value()));
		}
		User user = User.builder().email(registerRequest.getEmail()).password(registerRequest.getPassword()) // Consider
				.role(Role.EMPLOYEE).build();
		user = userRepository.save(user);

		Employee employee = Employee.builder().name(registerRequest.getName()).email(registerRequest.getEmail())
				.password(registerRequest.getPassword()).aadharCardNo(registerRequest.getAadharCardNo())
				.contactNumber(registerRequest.getContactNumber()).qualification(registerRequest.getQualification())
				.user(user).salary(registerRequest.getSalary()).role(Role.EMPLOYEE).build();

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

}
