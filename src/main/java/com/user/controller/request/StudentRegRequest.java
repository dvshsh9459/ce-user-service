package com.user.controller.request;

import lombok.Data;

@Data
public class StudentRegRequest {

	private String email;
	private String password;
	private long phoneNo;
	private String education;
	private String city;

}
