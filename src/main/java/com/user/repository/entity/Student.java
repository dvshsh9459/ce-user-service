package com.user.repository.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student  {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Email(message="Enter Valid Email")
	private String email;
	@NotEmpty(message="Password Must Not Be Empty")
	private String password;
	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private Role role;
	@NotEmpty(message = "name is mendatory")
	private String name;
	@NotNull(message = "Aadhar Card Number cannot be null")
	@Digits(integer = 12, fraction = 0, message = "Aadhar Card Number must be exactly 12 digits")
	@Column(unique = true, nullable = false, length = 12)
	private long aadharCardNo;
	@NotEmpty(message =  "Qualification Must Not Be Null")
	private String qualification;
	private long contactNo;
	@OneToOne(cascade = CascadeType.PERSIST)
	private User user ;
	
	

}
