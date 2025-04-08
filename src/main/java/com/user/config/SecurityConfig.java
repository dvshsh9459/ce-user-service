package com.user.config;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private JwtAuthConfig authenticationConfig;

	private JwtAuthFilter authenticationFilter;

	private CustomDetailsService customUserDetailsService;

	public SecurityConfig(JwtAuthConfig authenticationConfig, JwtAuthFilter authenticationFilter,
			CustomDetailsService customUserDetailsService) {
		this.authenticationConfig = authenticationConfig;
		this.authenticationFilter = authenticationFilter;
		this.customUserDetailsService = customUserDetailsService;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(requests -> requests

				.requestMatchers("/student/registeration", "/student/verify", "/student/login", "/admin/login",
						"/employee/registeration", "/employee/forgetPassword", "/student/forgetPassword",
						"/employee/login", "/v3/api-docs", "/configuration/ui", "/swagger-resources/**",
						"/configuration/security", "/swagger-ui.html", "/webjars/**", "/swagger-ui/**")
				.permitAll().requestMatchers("/employee/remove", "/student/remove", "/employee/getAll")
				.hasAnyAuthority("ADMIN").requestMatchers("/student/getAll").hasAnyAuthority("ADMIN", "EMPLOYEE")
				.requestMatchers("/student/updatePassword").hasAnyAuthority("STUDENT")
				.requestMatchers("/employee/updatePassword").hasAnyAuthority("EMPLOYEE")

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
