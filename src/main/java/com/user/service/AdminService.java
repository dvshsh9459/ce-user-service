package com.user.service;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.config.JwtHelper;
import com.user.controller.request.AdminLoginRequest;
import com.user.controller.response.AuthResponse;
import com.user.repository.AdminRepository;

import com.user.repository.entity.Admin;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {
	private final JwtHelper helper;

	private AdminRepository adminRepository;
	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public AdminService(JwtHelper helper, AdminRepository adminRepository) {

		this.helper = helper;
		this.adminRepository = adminRepository;
	}

	public ResponseEntity<AuthResponse> adminLogin(AdminLoginRequest loginRequest) {
		Admin admin = adminRepository.findByEmail(loginRequest.getEmail());
		String token = null;
		boolean hasRole = admin.getRoles().stream().anyMatch(r -> r.getRole().equalsIgnoreCase(loginRequest.getRole()));

		if (!hasRole) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
					"User does not have the specified role", false, HttpStatus.UNAUTHORIZED.value(), token, null));
		}
		if (admin != null && encoder.matches(loginRequest.getPassword(), admin.getPassword())) {
			// Generate token only with email
			token = helper.generateToken(admin.getEmail(), admin.getRoles());
			Date expiry = helper.getExpirationDate(token);

			log.info("User login successfully with email: {}", loginRequest.getEmail());

			return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse("Employee login successfully", true,
					HttpStatus.OK.value(), token, expiry.toString()));
		}

		log.warn("User login failed with email: {}", loginRequest.getEmail());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"User login failed! Invalid email or password", false, HttpStatus.UNAUTHORIZED.value(), null, null));
	}
}
