package com.user.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
	  private String message;
	    private boolean success;
	    private int status;
	    private String token;
	    private String expiresAt; 

}
