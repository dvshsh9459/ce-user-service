package com.user.repository.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
//@AllArgsConstructor
//@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
public class Employee extends User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private String name ;
//	@NotNull(message = "Aadhar Card Number cannot be null")
//	@Digits(integer = 12, fraction = 0, message = "Aadhar Card Number must be exactly 12 digits")
//	@Column(unique = true, nullable = false, length = 12)
//	private long aadharCardNo;
//	@NotNull(message = "contact number cannot be null")
//	private long contactNumber;
//	@NotEmpty(message = "Qualification Must Not Be Empty")
//	private String qualification;
//	private double salary;
	
	

}
