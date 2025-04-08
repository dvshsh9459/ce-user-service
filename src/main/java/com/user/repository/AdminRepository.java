package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.repository.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
 Admin findByEmail(String email); 
}
