package com.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.user.controller.request.ForgetPasswordRequest;
import com.user.controller.response.ForgetPassResponse;
import com.user.repository.StudentRepository;
import com.user.repository.entity.Student;
@ExtendWith(MockitoExtension.class)
class StudentForgetPassTest {
	 @Mock
	    private StudentRepository studentRepository;

	 

	    @InjectMocks
	    private StudentService studentService;

	    private ForgetPasswordRequest request;
	    private Student student;

	    @BeforeEach
	    void setup() {
	        request = new ForgetPasswordRequest();
	        request.setEmail("test@example.com");

	        student = Student.builder()
	                .email("test@example.com")
	                .password("oldEncodedPass")
	                .build();
	    }

	    @Test
	    void shouldReturnNotFoundWhenStudentIsNull() {
	        when(studentRepository.findByEmail(request.getEmail())).thenReturn(null);

	        ResponseEntity<ForgetPassResponse> response = studentService.forgetPassword(request);

	        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	        assertEquals("Student Not Found ", response.getBody().getMessage());
	    }

}
