package com.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.controller.request.EmployeeLoginRequest;
import com.user.controller.request.EmployeeRegisterRequest;
import com.user.controller.request.RemoveEmpRequest;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.entity.Employee;
import com.user.service.EmployeeService;

@RestController
@RequestMapping("/teacher")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@PostMapping("/registeration")
	public ResponseEntity<UserResponse> teacherRegister(@RequestBody EmployeeRegisterRequest registerRequest) {
		return employeeService.teacherRegisteration(registerRequest);
	}

	@GetMapping("/login")
	public ResponseEntity<AuthResponse> teacherLogin(@RequestBody EmployeeLoginRequest loginRequest) {
		return employeeService.teacherLogin(loginRequest);
	}

	@GetMapping("/getAll")
	public List<Employee> getAllEmployees() {
		return employeeService.employees();
	}

	@DeleteMapping("/remove")
	public ResponseEntity<UserResponse> removeEmployee(@RequestBody RemoveEmpRequest empRequest) {
		return employeeService.removeEmployee(empRequest);
	}
}
