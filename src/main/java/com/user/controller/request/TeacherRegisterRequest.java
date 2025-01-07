package com.user.controller.request;

import lombok.Data;

@Data
public class TeacherRegisterRequest {

	private String email;
	private String password;
	private long phoneNo;

}
