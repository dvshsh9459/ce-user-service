package com.user.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.controller.request.EmployeeLoginRequest;
import com.user.controller.request.EmployeeRegisterRequest;
import com.user.controller.request.ForgetPasswordRequest;
import com.user.controller.request.RemoveEmpRequest;
import com.user.controller.request.UpdatePasswordReq;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.ForgetPassResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.entity.Employee;
import com.user.service.EmployeeService;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

	private EmployeeService employeeService;

	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	@PostMapping("/registeration")
	public ResponseEntity<UserResponse> teacherRegister(@RequestBody EmployeeRegisterRequest registerRequest) {
		return employeeService.teacherRegisteration(registerRequest);
	}

	@GetMapping("/login")
	public ResponseEntity<AuthResponse> teacherLogin(@RequestBody EmployeeLoginRequest loginRequest) {
		return employeeService.employeeLogin(loginRequest);
	}

	@GetMapping("/getAll")
	public List<Employee> getAllEmployees() {
		return employeeService.employees();
	}

	@DeleteMapping("/remove")
	public ResponseEntity<UserResponse> removeEmployee(@RequestBody RemoveEmpRequest empRequest) {
		return employeeService.removeEmployee(empRequest);
	}

	@PutMapping("/updatePassword")
	public ResponseEntity<UserResponse> updatePass(@RequestBody UpdatePasswordReq passwordReq) {
		return employeeService.updatePassword(passwordReq);
	}

	@PutMapping("/forgetPassword")
	public ResponseEntity<ForgetPassResponse> forgetPassword(@RequestBody ForgetPasswordRequest forgetPassword) {
		return employeeService.forgetPassword(forgetPassword);
	}
}
