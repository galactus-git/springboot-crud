package com.springboot.restapi.exception;

import java.util.Date;

public class ErrorMessage {

	private Date timestamp;
	  private String message;
	  private String details;

	  public ErrorMessage(Date timestamp, String message, String details) {
	    super();
	    this.timestamp = timestamp;
	    this.message = message;
	    this.details = details;
	  }
	
	
}
