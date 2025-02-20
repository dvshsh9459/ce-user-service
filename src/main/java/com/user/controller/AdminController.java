package com.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.controller.request.AdminLoginRequest;
import com.user.controller.response.AuthResponse;


import com.user.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@GetMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody AdminLoginRequest adminLoginRequest) {
		return adminService.adminLogin(adminLoginRequest);
	}
}
