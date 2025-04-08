package com.user.service;

import static org.mockito.Mockito.*;

import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.user.controller.request.StudentRegRequest;
import com.user.controller.response.UserResponse;
import com.user.repository.RoleRepository;
import com.user.repository.StudentRepository;
import com.user.repository.entity.Role;
import com.user.repository.entity.Student;
import com.user.service.kafkaeventservice.EmailProducer;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@ExtendWith(MockitoExtension.class)
//class StudentServiceTest {
//
//	@Mock
//	private StudentRepository studentRepository;
//
//	@Mock
//	private RoleRepository roleRepository;
//
//	@Mock
//	private BCryptPasswordEncoder encoder;
//
//	@InjectMocks
//	private StudentService studentService;
//
//	private StudentRegRequest regRequest;
//	private Student student;
//	private Role role;
//
//	@BeforeEach
//	void setUp() {
//		regRequest = new StudentRegRequest();
//		regRequest.setEmail("test@example.com");
//		regRequest.setPassword("password123");
//		regRequest.setRole("STUDENT");
//
//		role = new Role();
//		role.setRole("STUDENT");
//
//		student = Student.builder().email("test@example.com").password("encodedPassword").roles(new HashSet<>())
//				.build();
//	}
//
//	@Test
//	void shouldReturnConflictWhenStudentAlreadyExists() {
//		when(studentRepository.findByEmail(regRequest.getEmail())).thenReturn(student);
//
//		ResponseEntity<UserResponse> response = studentService.studentRegistration(regRequest);
//
//		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
//		assertEquals("Student already exists", response.getBody().getMessage());
//	}
//
//	@Test
//	void shouldReturnBadRequestWhenRoleNotFound() {
//		when(studentRepository.findByEmail(regRequest.getEmail())).thenReturn(null);
//		when(roleRepository.findByRole(regRequest.getRole())).thenReturn(null);
//
//		ResponseEntity<UserResponse> response = studentService.studentRegistration(regRequest);
//
//		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//		assertEquals("Role not found", response.getBody().getMessage());
//	}
//
//	@Test
//	void shouldRegisterStudentSuccessfully() {
//		when(studentRepository.findByEmail(regRequest.getEmail())).thenReturn(null);
//		when(roleRepository.findByRole(regRequest.getRole())).thenReturn(role);
//		lenient().when(encoder.encode(regRequest.getPassword())).thenReturn("encodedPassword");
//		ResponseEntity<UserResponse> response = studentService.studentRegistration(regRequest);
//
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		assertEquals("Student registered successfully", response.getBody().getMessage());
//		verify(studentRepository, times(1)).save(any(Student.class));
//	}
//}
    

import java.util.Optional;


import org.springframework.security.crypto.password.PasswordEncoder;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private EmailProducer emailProducer;

    @InjectMocks
    private StudentService studentService; // Assuming this is the class name containing the methods

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStudentRegistration_ExistingStudent() {
        // Arrange
        StudentRegRequest request = new StudentRegRequest("test@example.com", "password", "STUDENT");
        when(studentRepository.findByEmail("test@example.com")).thenReturn(new Student());

        // Act
        ResponseEntity<UserResponse> response = studentService.studentRegistration(request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Student already exists", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testStudentRegistration_RoleNotFound() {
        // Arrange
        StudentRegRequest request = new StudentRegRequest("test@example.com", "password", "STUDENT");
        when(studentRepository.findByEmail("test@example.com")).thenReturn(null);
        when(roleRepository.findByRole("STUDENT")).thenReturn(null);

        // Act
        ResponseEntity<UserResponse> response = studentService.studentRegistration(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Role not found", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testStudentRegistration_Successful() {
        // Arrange
        StudentRegRequest request = new StudentRegRequest("test@example.com", "password", "STUDENT");
        Role role = new Role(0, "STUDENT");
        when(studentRepository.findByEmail("test@example.com")).thenReturn(null);
        when(roleRepository.findByRole("STUDENT")).thenReturn(role);
        when(encoder.encode("password")).thenReturn("encodedPassword");

        // Act
        ResponseEntity<UserResponse> response = studentService.studentRegistration(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Verification code sent to email. Please verify.", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        verify(studentRepository).save(any(Student.class));
        verify(emailProducer).sendVerificationEmail(eq("test@example.com"), anyString());
    }

    @Test
    void testGenerateVerificationCode() {
        // Act
        String code = studentService.generateVerificationCode();

        // Assert
        assertNotNull(code);
        assertEquals(6, code.length());
        assertTrue(code.matches("\\d{6}")); // Check if it's a 6-digit number
    }

    @Test
    void testVerifyStudent_StudentNotFound() {
        // Arrange
        when(studentRepository.findByEmail("test@example.com")).thenReturn(null);

        // Act
        ResponseEntity<UserResponse> response = studentService.verifyStudent("test@example.com", "123456");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Student not found", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testVerifyStudent_InvalidCode() {
        // Arrange
        Student student = Student.builder()
                .email("test@example.com")
                .verificationCode("123456")
                .isVerified(false)
                .build();
        when(studentRepository.findByEmail("test@example.com")).thenReturn(student);

        // Act
        ResponseEntity<UserResponse> response = studentService.verifyStudent("test@example.com", "654321");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid verification code", response.getBody().getMessage());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testVerifyStudent_Successful() {
        // Arrange
        Student student = Student.builder()
                .email("test@example.com")
                .verificationCode("123456")
                .isVerified(false)
                .build();
        when(studentRepository.findByEmail("test@example.com")).thenReturn(student);

        // Act
        ResponseEntity<UserResponse> response = studentService.verifyStudent("test@example.com", "123456");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email verified successfully!", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());
        assertTrue(student.isVerified());
        assertNull(student.getVerificationCode());
        verify(studentRepository, times(1)).save(student); // Once in registration, once in verification
    }
}