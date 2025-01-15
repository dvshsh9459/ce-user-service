package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.repository.entity.Employee;



public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
	Employee findByEmail(String email);

}
