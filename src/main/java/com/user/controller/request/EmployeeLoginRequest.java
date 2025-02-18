package com.user.controller.request;

import lombok.Data;

@Data
public class EmployeeLoginRequest {
	private String email;
	private String password;
	private String role;

}
