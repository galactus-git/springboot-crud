package com.springboot.restapi.api;

public class AuthResquest {

	private String email;
    private String username;
    private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public AuthResquest(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public AuthResquest() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
