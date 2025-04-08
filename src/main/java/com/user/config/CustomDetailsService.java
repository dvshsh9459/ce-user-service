package com.user.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user.repository.UserRepository;
import com.user.repository.entity.User;

import jakarta.transaction.Transactional;

@Service
public class CustomDetailsService implements UserDetailsService {

	private UserRepository userRepository;

	public CustomDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user != null) {
			return user;
		}

		throw new UsernameNotFoundException("User not found with username: " + username);
	}
}
