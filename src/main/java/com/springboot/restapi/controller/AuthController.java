package com.springboot.restapi.controller;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.restapi.api.AuthResponse;
import com.springboot.restapi.entity.Role;
import com.springboot.restapi.entity.User;
import com.springboot.restapi.exception.ResourceNotFoundException;
import com.springboot.restapi.jwt.JWTUtility;
import com.springboot.restapi.payload.LoginDto;
import com.springboot.restapi.payload.SignUpDto;
import com.springboot.restapi.repository.RoleRepository;
import com.springboot.restapi.repository.UserRepository;


@RestController
@RequestMapping("/api/auth")
public class AuthController 
{

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JWTUtility jwtUtility;

	@PostMapping("/in")
	public ResponseEntity<String> authenticateUser(@RequestBody LoginDto loginDto)
	{
		try
		{
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginDto.getUsername(), loginDto.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		catch(BadCredentialsException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser11(@RequestBody LoginDto loginDto)
	{

		try
		{
			User user = userRepository.findByUsername(loginDto.getUsername());
			if(user != null)
			{
				final String token = jwtUtility.generateToken(user);
				user.setToken(token);
				//userRepository.save(user);
				return ResponseEntity.ok(new AuthResponse(token));            }
			else
			{
				throw new AuthenticationCredentialsNotFoundException("Invalid User");
			}
		}
		catch (AuthenticationCredentialsNotFoundException foundEx)
		{
			throw new AuthenticationCredentialsNotFoundException(foundEx.getMessage());
		}

		catch(Exception e)
		{
			return new ResponseEntity<>("Invalid User",HttpStatus.BAD_REQUEST);
			//throw new ApplicationException("Invalid User",ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto)
	{

		// add check for username exists in a DB
		if(userRepository.existsByUsername(signUpDto.getUsername())){
			return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
		}

		// add check for email exists in DB
		if(userRepository.existsByEmail(signUpDto.getEmail())){
			return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
		}

		// create user object
		User user = new User();
		user.setName(signUpDto.getName());
		user.setUsername(signUpDto.getUsername());
		user.setEmail(signUpDto.getEmail());
		user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

		Role roles = roleRepository.findByName("ROLE_ADMIN").get();
		user.setRoles(Collections.singleton(roles));

		userRepository.save(user);

		return new ResponseEntity<>("User registered successfully", HttpStatus.OK);     
	}


	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateUser(@PathVariable long id,@RequestBody User userDetails)
	{
		User updateUser = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not exists with id" + id));

		if(updateUser.isLoggedOut()==true)
		{
			return new ResponseEntity<>("User logged out, please log in again for update/delete...",HttpStatus.BAD_REQUEST);
		}
		if(updateUser.isDeleted() == true)
		{
			return new ResponseEntity<>("User is already Deleted",HttpStatus.OK);
		}
		updateUser.setName(userDetails.getName());
		updateUser.setUsername(userDetails.getUsername());
		updateUser.setEmail(userDetails.getEmail());
		updateUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));

		userRepository.save(updateUser);

		return ResponseEntity.ok(updateUser);

	}

	@PutMapping("/delete/{id}")
	public ResponseEntity<?>deleteUser(@PathVariable long id)
	{

		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + id));

		if(user.isLoggedOut()==true)
		{
			return new ResponseEntity<>("User logged out, please log in again for update/delete...",HttpStatus.BAD_REQUEST);
		}

		if(user.isDeleted() == true)
		{
			return new ResponseEntity<>("User is already Deleted",HttpStatus.OK);
		}
		user.setDeleted(true);
		userRepository.save(user);

		return new ResponseEntity<>("User deleted Successfully",HttpStatus.OK); 

	}

	@PutMapping("/logout/{id}")
	public ResponseEntity<String>logoutUser(@PathVariable long id)
	{
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not exist with id: " + id));

		if(user.isDeleted() == true)
		{
			return new ResponseEntity<>("Sorry, User is Deleted...",HttpStatus.BAD_REQUEST);
		}

		if(user.isLoggedOut() == true)
		{
			return new ResponseEntity<>("User is already Logged Out...",HttpStatus.BAD_REQUEST);
		}

		user.setLoggedOut(true);

		userRepository.save(user);

		return new ResponseEntity<>("User Logged Out Successfully...",HttpStatus.OK); 

	}


	@PostMapping("/hello")
	public String Hello() {
		return "Hello World";
	}


}