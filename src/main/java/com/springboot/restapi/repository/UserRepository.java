package com.springboot.restapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.restapi.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    User findByUsername(String username);
   
    //User findByUsernameOrEmail(String username, String password);
    
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
	//boolean activateDeactivateUser(User user);
	//User authenticateUser(String username, String password);
    
}

