package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.repository.entity.JwtToken;

public interface JwtRepository extends JpaRepository<JwtToken, Integer> {
	JwtToken findByEmail(String email);

}
