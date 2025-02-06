package com.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.controller.request.ForgetPasswordRequest;
import com.user.controller.request.RemoveStuRequest;
import com.user.controller.request.StudentLoginRequest;
import com.user.controller.request.StudentRegRequest;
import com.user.controller.request.UpdatePasswordReq;
import com.user.controller.response.AuthResponse;
import com.user.controller.response.ForgetPassResponse;
import com.user.controller.response.UserResponse;
import com.user.repository.entity.Student;
import com.user.service.StudentService;

@RestController
@RequestMapping("/student")
public class StudentController {

	@Autowired
	private StudentService studentService;

	@PostMapping("/registeration")
	public ResponseEntity<UserResponse> registerStudent(@RequestBody StudentRegRequest studentRegRequest) {
		return studentService.studentRegistration(studentRegRequest);
	}

	@GetMapping("/login")
	public ResponseEntity<AuthResponse> loginStudent(@RequestBody StudentLoginRequest loginRequest) {
		return studentService.studentLogin(loginRequest);
	}

	@GetMapping("/getAll")
	public List<Student> getMethodName() {
		return studentService.getAllStudents();
	}

	@DeleteMapping("/remove")
	public ResponseEntity<UserResponse> removeStudent(@RequestBody RemoveStuRequest removeStuRequest) {
		return studentService.removeStudent(removeStuRequest);
	}

	@PutMapping("/updatePassword")
	public ResponseEntity<UserResponse> updatePassword(@RequestBody UpdatePasswordReq passwordReq) {
		return studentService.updatePassword(passwordReq);
	}

	@PutMapping("/forgetPassword")
	public ResponseEntity<ForgetPassResponse> forgetPassword(@RequestBody ForgetPasswordRequest forgetPassword) {
		return studentService.forgetPassword(forgetPassword);
	}

}
