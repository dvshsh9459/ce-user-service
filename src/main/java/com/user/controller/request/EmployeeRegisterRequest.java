package com.user.controller.request;

import lombok.Data;

@Data
public class EmployeeRegisterRequest {

	private String email;
	private String password;
	private String roles;

}
