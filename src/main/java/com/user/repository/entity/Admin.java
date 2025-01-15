package com.user.repository.entity;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
public class Admin extends User  {/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
