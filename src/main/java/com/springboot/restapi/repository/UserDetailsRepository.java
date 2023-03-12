package com.springboot.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.restapi.entity.User;
import com.springboot.restapi.entity.UserDetails;

public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> 
{
	Optional<UserDetails> findByEmail(String email);
	
	Boolean existsByEmail(String email);
	
}
