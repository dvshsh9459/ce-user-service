package com.user.controller.request;

import lombok.Data;

@Data
public class EmployeeRegisterRequest {

	private String email;
	private String name;
	private String password;
	private long aadharCardNo;
	private long contactNumber;
	private String qualification;
	private double salary;

}
