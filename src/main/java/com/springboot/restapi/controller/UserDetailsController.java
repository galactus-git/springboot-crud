package com.springboot.restapi.controller;


import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.restapi.api.AuthResponse;
import com.springboot.restapi.api.AuthResquest;
import com.springboot.restapi.entity.User;
import com.springboot.restapi.entity.UserDetails;
import com.springboot.restapi.exception.ResourceNotFoundException;
import com.springboot.restapi.jwt.JwtTokenUtil;
import com.springboot.restapi.payload.UserDetailsDto;
import com.springboot.restapi.repository.UserDetailsRepository;
import com.springboot.restapi.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class UserDetailsController 
{
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@PostMapping("/info")
	public ResponseEntity<?> registerUser(@RequestBody UserDetailsDto userDetailsDto)
	{

		// add check for email exists in a DB
		if(userDetailsRepository.existsByEmail(userDetailsDto.getEmail()))
		{
			return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
		}

		// create user object
		UserDetails userDetails = new UserDetails();

		userDetails.setEmail(userDetailsDto.getEmail());
		userDetails.setPrimaryAddress(userDetailsDto.getPrimaryAddress());
		userDetails.setSecondaryAddress(userDetailsDto.getSecondaryAddress());
		userDetails.setState(userDetailsDto.getState());
		userDetails.setCountry(userDetailsDto.getCountry());

		User theUser = userRepository.findByEmail("xyz@gmail.com").get();
		userDetails.setUser(Collections.singleton(theUser));

		userDetailsRepository.save(userDetails);

		return new ResponseEntity<>("User registered successfully", HttpStatus.OK);     
	} 

	@PostMapping("/updateInfo/{id}")
	public ResponseEntity<User> updateUser(@PathVariable long id,@RequestBody UserDetailsDto userDetailsDto) 
	{

		//		if(userRepository.existsByEmail(user.getEmail()))
		//		{
		User updateUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not exists with id" + id));

		// create user object
		UserDetails userDetails = new UserDetails();

		//userDetails.setEmail(userDetailsDto.getEmail());
		userDetails.setPrimaryAddress(userDetailsDto.getPrimaryAddress());
		userDetails.setSecondaryAddress(userDetailsDto.getSecondaryAddress());
		userDetails.setState(userDetailsDto.getState());
		userDetails.setCountry(userDetailsDto.getCountry());

		//User theUser = userRepository.findByEmail("abc@gmail.com").get();
		User theUser = userRepository.findById(id).get();
		userDetails.setUser(Collections.singleton(theUser));

		userDetailsRepository.save(userDetails);

		return new ResponseEntity<User>(HttpStatus.OK);
	}

	//		}
	//		else
	//		{
	//			return new ResponseEntity<>("Your Email ID is not matching...!", HttpStatus.OK);
	//		}


	@PutMapping("/addDetails/{id}")
	public ResponseEntity<User> updateUser2(@PathVariable long id, @RequestBody UserDetailsDto user)
	{
		User updateUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not exists with id" + id));

		if(updateUser.isDeleted()==true)
		{
			return new ResponseEntity<>(HttpStatus.valueOf("User already deleted...!"));
		}

		UserDetails userDetails = new UserDetails();

		userDetails.setPrimaryAddress(user.getPrimaryAddress());
		userDetails.setSecondaryAddress(user.getSecondaryAddress());
		userDetails.setState(user.getState());
		userDetails.setCountry(user.getCountry());

		//User theUser = userRepository.findByEmail("abc@gmail.com").get();
		User theUser = userRepository.findById(id).get();
		userDetails.setUser(Collections.singleton(theUser));

		userDetailsRepository.save(userDetails);

		return new ResponseEntity<User>(HttpStatus.OK);
	}

	@PutMapping("/deleteUser/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable long id){

		UserDetails deleteUser = userDetailsRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + id));

		//User user=new User();

		if(deleteUser.isDeleted()==true)
		{
			return new ResponseEntity<>("Ooops, User already deleted...!",HttpStatus.OK);
		}                                                                    

		deleteUser.setDeleted(true);

		userDetailsRepository.save(deleteUser);

		return new ResponseEntity<>("User deleted successfully...",HttpStatus.OK);

	}
	
	@PostMapping("/loginD")
    public ResponseEntity<?> authenticateUser1(@RequestBody UserDetailsDto userDetailsDto)
    {
		 Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
				 userDetailsDto.getEmail(), userDetailsDto.getState()));
	    	
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        
	        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
        
    }


}
