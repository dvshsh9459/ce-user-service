package com.user.repository.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
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
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class Employee {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Email(message="Enter Valid Email")
	@NotNull(message = "email cannot be null")
	private String email;
	private String name ;
	@JsonIgnore
	@NotEmpty(message = "Passsword Must Not BE Empty")
	private String password;
	@NotNull(message = "Aadhar Card Number cannot be null")
	@Digits(integer = 12, fraction = 0, message = "Aadhar Card Number must be exactly 12 digits")
	@Column(unique = true, nullable = false, length = 12)
	private long aadharCardNo;
	@NotNull(message = "contact number cannot be null")
	private long contactNumber;
	@NotEmpty(message = "Qualification Must Not Be Empty")
	private String qualification;
	private double salary;
	@OneToOne(cascade = CascadeType.PERSIST)
	private User user ;
	

}
