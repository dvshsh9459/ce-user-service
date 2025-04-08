package com.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import com.user.repository.entity.Student;

@DataJpaTest
class StudentRepositoryTest {

    @Mock
    private StudentRepository studentRepository;

    @Test
    void testSaveAndFindStudentByEmail() {
        // Arrange: Create and save a student entity
   
        // Act: Retrieve student by email
        Student foundStudent = studentRepository.findByEmail("abhi@gmail");

        // Assert: Check if the retrieved student is the same as the saved one
        assertThat(foundStudent).isNotNull();
       
    }
}
