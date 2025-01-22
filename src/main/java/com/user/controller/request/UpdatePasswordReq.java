package com.user.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordReq {
	private String email;
	private String oldPassword;
	private String newPassword;
	private String confirmPassword;

}
