package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.repository.entity.JwtToken;
@Repository
public interface JwtRepository extends JpaRepository<JwtToken, Integer> {
	JwtToken findByEmail(String email);

}
