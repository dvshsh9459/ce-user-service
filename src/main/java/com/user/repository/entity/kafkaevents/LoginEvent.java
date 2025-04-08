package com.user.repository.entity.kafkaevents;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginEvent {
	private String email;
	private String status; // SUCCESS / FAILED
	private String role;
	private String message;
	private Date timestamp;
}