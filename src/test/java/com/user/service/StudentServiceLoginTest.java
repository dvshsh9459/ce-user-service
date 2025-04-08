package com.user.service;



import static org.mockito.ArgumentMatchers.any;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.user.config.CustomDetailsService;
import com.user.config.JwtHelper;
import com.user.controller.request.StudentLoginRequest;
import com.user.controller.response.AuthResponse;
import com.user.repository.RoleRepository;
import com.user.repository.StudentRepository;
import com.user.repository.entity.Role;
import com.user.repository.entity.Student;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

//@ExtendWith(MockitoExtension.class)
//class StudentServiceLoginTest {
//	@Mock
//	private StudentRepository studentRepository;
//
//	@Mock
//	private RoleRepository roleRepository;
//
//	@Mock
//	private CustomDetailsService customDetailsService;
//
//	@Mock
//	private JwtHelper helper;
//
//	@Mock
//	private PasswordEncoder encoder;
//
//	@InjectMocks
//	private StudentService studentService;
//
//	private Student student;
//	private Role role;
//	private StudentLoginRequest loginRequest;
//	private static final String SECRET_KEY = "MySuperSecretKeyForJWTSigning123";
//
//	@BeforeEach
//	void setUp() {
//		student = new Student();
//		student.setEmail("tes@student.com");
//
//		// Set an actual BCrypt-hashed password
//		String hashedPassword = new BCryptPasswordEncoder().encode("hashedPassword");
//		student.setPassword(hashedPassword);
//
//		role = new Role();
//		role.setRole("STUDENT");
//
//		loginRequest = new StudentLoginRequest();
//		loginRequest.setEmail("tes@student.com");
//		loginRequest.setPassword("hashedPassword"); // Plain password used for login
//		loginRequest.setRole("STUDENT");
//	}
//
//	// Mock the student, role, and login request
//	@Test
//	void test() {
//		// ✅ Mock repositories
//		when(studentRepository.findByEmail(loginRequest.getEmail())).thenReturn(student);
//		when(roleRepository.findByRole(loginRequest.getRole())).thenReturn(role);
//
//		// ✅ Ensure password matches
//		when(encoder.matches(loginRequest.getPassword(), student.getPassword())).thenReturn(true);
//
//		when(customDetailsService.loadUserByUsername(student.getEmail())).thenReturn(mock(UserDetails.class));
//
//		// ✅ Use a fixed secret key for signing and validation
//		byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
//		SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
//
//		String validJwt = Jwts.builder().setSubject("tes@student.com").signWith(secretKey, SignatureAlgorithm.HS256)
//				.compact();
//
//		// ✅ Mock JWT generation
//		when(helper.generateToken(any(), any(), any())).thenReturn(validJwt);
//		when(helper.getOrGenerateToken(any(), any(), any())).thenReturn(validJwt);
//
//		// ✅ Mock JWT claims correctly
//		Claims claims = mock(Claims.class);
//		when(helper.decodeJwt(validJwt)).thenReturn(claims);
//		when(claims.getSubject()).thenReturn("tes@student.com");
//
//		// ✅ Execute Login
//		ResponseEntity<AuthResponse> response = studentService.studentLogin(loginRequest);
//
//		// ✅ Validate Response
//		assertNotNull(response);
//		assertEquals(HttpStatus.OK, response.getStatusCode());
//		assertEquals("Student login Successfully", response.getBody().getMessage());
//	}
//
//}
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class StudentServiceLoginTest {

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private BCryptPasswordEncoder encoder;

	@Mock
	private CustomDetailsService customDetailsService;

	@Mock
	private JwtHelper helper;

	@InjectMocks
	private StudentService studentService;

	private StudentLoginRequest loginRequest;
	private Student student;
	private Role role;
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		loginRequest = new StudentLoginRequest();
		loginRequest.setEmail("test@example.com");
		loginRequest.setPassword("password123");
		loginRequest.setRole("STUDENT");

		role = new Role();
		role.setRole("STUDENT");
		String encodedPassword = new BCryptPasswordEncoder().encode("password123");

		student = Student.builder().email("test@example.com").password(encodedPassword).roles(Set.of(role)).build();

		userDetails = mock(UserDetails.class);
	}

	@Test
	void shouldReturnUnauthorizedForInvalidCredentials() {
	    when(studentRepository.findByEmail(loginRequest.getEmail())).thenReturn(student);
	    when(encoder.matches(loginRequest.getPassword(), student.getPassword())).thenReturn(false);

	    System.out.println("Found student: " + student);
	    System.out.println("Password match result: " + encoder.matches(loginRequest.getPassword(), student.getPassword()));

	    ResponseEntity<AuthResponse> response = studentService.studentLogin(loginRequest);

	    System.out.println("Response Status: " + response.getStatusCode());
	    System.out.println("Response Message: " + response.getBody().getMessage());

	    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	    assertEquals("Login failed ! Invalid email or password", response.getBody().getMessage());
	}


//	@Test
//	void shouldLoginSuccessfully() {
//		String generatedToken = "mockToken";
//
//		when(studentRepository.findByEmail(loginRequest.getEmail())).thenReturn(student);
//		lenient().when(encoder.matches(loginRequest.getPassword(), student.getPassword())).thenReturn(true);
//		when(roleRepository.findByRole(loginRequest.getRole())).thenReturn(role);
//		when(customDetailsService.loadUserByUsername(student.getEmail())).thenReturn(userDetails);
//		when(helper.generateToken(userDetails, student.getPassword(), role)).thenReturn(generatedToken);
//		when(helper.getOrGenerateToken(anyString(), anyString(), any())).thenReturn(generatedToken); // Fixed mock
//
//		Claims claims = mock(Claims.class, RETURNS_DEEP_STUBS);
//		when(claims.getSubject()).thenReturn("test@example.com");
//
//		try (MockedStatic<JwtHelper> mockedStatic = mockStatic(JwtHelper.class)) {
//			mockedStatic.when(() -> JwtHelper.decodeJwt(eq(generatedToken))).thenReturn(claims);
//
//			// Debugging before the test
//			System.out.println("Generated Token before test: " + generatedToken);
//
//			ResponseEntity<AuthResponse> response = studentService.studentLogin(loginRequest);
//
//			// Debugging inside test
//			System.out.println("Generated Token inside test: "
//					+ helper.getOrGenerateToken(student.getEmail(), student.getPassword(), role));
//			System.out.println("Decoded Claims: " + JwtHelper.decodeJwt(generatedToken));
//			System.out.println("Response Status: " + response.getStatusCode());
//
//			assertEquals(HttpStatus.OK, response.getStatusCode());
//			assertEquals("Student login Successfully", response.getBody().getMessage());
//			assertEquals(generatedToken, response.getBody().getToken());
//		}
//	}

}