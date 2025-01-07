package com.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.user.controller.request.AdminLoginRequest;
import com.user.controller.response.UserResponse;
import com.user.repository.AdminRepository;
import com.user.repository.entity.Admin;

@Service
public class AdminService {
	@Autowired
	private AdminRepository adminRepository;

	public ResponseEntity<UserResponse> adminLogin(AdminLoginRequest loginRequest) {
		Admin admin = adminRepository.findByEmail(loginRequest.getEmail());

		if (admin != null && !admin.getPassword().equals(loginRequest.getPassword())) {
			return ResponseEntity.status(HttpStatus.OK)
					.body(new UserResponse("Admin Login Successfully", true, HttpStatus.OK.value()));
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserResponse(
				"Admin Login failed!! invalid email or password", true, HttpStatus.UNAUTHORIZED.value()));
	}

}
