package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.repository.entity.Student;
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
	Student findByEmail(String email);
}
