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
import com.user.repository.UserRepository;

import com.user.repository.entity.Role;
import com.user.repository.entity.User;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AdminService {
	@Autowired
	private CustomDetailsService customDetailsService;

	@Autowired
	private JwtHelper helper;

	@Autowired
	private UserRepository userRepository;

	public ResponseEntity<AuthResponse> adminLogin(AdminLoginRequest loginRequest) {
		User user = userRepository.findByEmail(loginRequest.getEmail());
		if (user != null && user.getPassword().equals(loginRequest.getPassword())) {
			UserDetails details = customDetailsService.loadUserByUsername(user.getEmail());
			String token = helper.generateToken(details, user.getPassword(), user.getRole());
			String existingtoken = helper.getOrGenerateToken(user.getEmail(), user.getPassword(), user.getRole());
			Claims claims1 = JwtHelper.decodeJwt(existingtoken);
			Claims claims2 = JwtHelper.decodeJwt(token);
			System.out.println(token);
			if (claims1.getSubject().equals(claims2.getSubject())) {
				log.info("Admin login successfully with email: {}", loginRequest.getEmail());
				return ResponseEntity.status(HttpStatus.OK)
						.body(new AuthResponse("Admin Login Successfully", true, HttpStatus.OK.value(), token));
			}
		}
		log.warn("Admin login failed!! with email: {}", loginRequest.getEmail());

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse(
				"Admin login failed!! invalid email or password", true, HttpStatus.UNAUTHORIZED.value(), null));
	}

}
