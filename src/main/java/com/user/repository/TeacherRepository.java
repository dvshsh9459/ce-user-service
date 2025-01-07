package com.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user.repository.entity.Teacher;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
	Teacher findByEmail(String email);

}
