package com.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.user.repository.entity.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthConfig authenticationConfig;
	@Autowired
	private JwtAuthFilter authenticationFilter;
	@Autowired
	private CustomDetailsService customUserDetailsService;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(requests -> requests

				.requestMatchers("/student/registeration", "/student/login", "/admin/login", "/employee/registeration",
						"/employee/forgetPassword", "/student/forgetPassword", "/employee/login", "/v3/api-docs",
						"/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html",
						"/webjars/**", "/swagger-ui/**")
				.permitAll().requestMatchers("/employee/remove", "/student/remove", "/employee/getAll")
				.hasAnyAuthority(Role.ADMIN.name()).requestMatchers("/student/getAll")
				.hasAnyAuthority(Role.ADMIN.name(), Role.EMPLOYEE.name()).requestMatchers("/student/updatePassword")
				.hasAnyAuthority(Role.STUDENT.name()).requestMatchers("/employee/updatePassword")
				.hasAnyAuthority(Role.EMPLOYEE.name())

				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				.anyRequest().authenticated())
				.exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationConfig))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailsService);
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		return provider;
	}

}
