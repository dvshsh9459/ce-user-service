package com.user.repository.entity;

<<<<<<< HEAD

=======
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
>>>>>>> 074214fce5c8092971d5ed2c219f75ca857e75aa
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
<<<<<<< HEAD

=======
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
>>>>>>> 074214fce5c8092971d5ed2c219f75ca857e75aa
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
<<<<<<< HEAD
public class Admin extends User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int adminId;

=======
@Builder
public class Admin  {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Email(message="Enter Valid Email")
	private String email;
	@NotEmpty(message="Password Must Not Be Empty")
	private String password;
	@JsonIgnore
	@Enumerated(EnumType.STRING)
	private Role role ;
	@OneToOne(cascade = CascadeType.PERSIST)
	private User user;
>>>>>>> 074214fce5c8092971d5ed2c219f75ca857e75aa
}
