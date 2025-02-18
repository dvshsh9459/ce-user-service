package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.repository.entity.Role;

public interface RoleRepository  extends JpaRepository<Role, Integer>{
	Role findByRole(String role);

}
