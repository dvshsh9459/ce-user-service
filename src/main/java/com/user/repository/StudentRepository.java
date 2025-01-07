package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.repository.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
Student findByEmail(String email);
}
