package com.user.controller.request;

import lombok.Data;

@Data
public class StudentLoginRequest {

	private String email;
	private String password;
	private String role;
}
