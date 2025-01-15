package com.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.user.config.CustomDetailsService;
import com.user.config.JwtHelper;
import com.user.controller.request.AdminLoginRequest;
import com.user.controller.response.AuthResponse;
import com.user.repository.AdminRepository;
import com.user.repository.UserRepository;
import com.user.repository.entity.Admin;
import com.user.repository.entity.Role;
import com.user.repository.entity.User;

import io.jsonwebtoken.Claims;

@Service
public class AdminService {
	@Autowired
	private CustomDetailsService customDetailsService;

	@Autowired
	private JwtHelper helper;
	@Autowired
	private AdminRepository adminRepository;
	@Autowired
	private UserRepository userRepository;

	public ResponseEntity<AuthResponse> adminLogin(AdminLoginRequest loginRequest) {
		Admin admin = adminRepository.findByEmail(loginRequest.getEmail());

		if (admin != null && admin.getPassword().equals(loginRequest.getPassword())) {
			User user = userRepository.findByEmail(admin.getEmail());
			if (user == null) {
				// Create a new user entry for the admin
				user = new User();
				user.setEmail(admin.getEmail());
				user.setPassword(admin.getPassword()); // Preferably store hashed password
				user.setRole(Role.ADMIN); // Mark as admin
				userRepository.save(user);
				adminRepository.save(admin);
			}
			UserDetails details = customDetailsService.loadUserByUsername(admin.getEmail());
			String token = helper.generateToken(details, admin.getPassword(), Role.ADMIN);
			String existingtoken = helper.getOrGenerateToken(admin.getEmail(), admin.getPassword(), Role.ADMIN);
			Claims claims1 = JwtHelper.decodeJwt(existingtoken);
			Claims claims2 = JwtHelper.decodeJwt(token);
			System.out.println(token);
			if (claims1.getSubject().equals(claims2.getSubject())) {

				return ResponseEntity.status(HttpStatus.OK)
						.body(new AuthResponse("Admin Login Successfully", true, HttpStatus.OK.value(), token));
			}
		}

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"Admin Login failed!! invalid email or password", true, HttpStatus.UNAUTHORIZED.value(), null));
	}

}
