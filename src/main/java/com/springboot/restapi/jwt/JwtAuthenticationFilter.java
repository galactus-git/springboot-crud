package com.springboot.restapi.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.springboot.restapi.entity.User;
import com.springboot.restapi.service.CustomUserDetailsService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private JWTUtility jwtUtility;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException 
	{

		String requestTokenHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;

		//checking null and format
		if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer "))
		{
			jwtToken = requestTokenHeader.substring(7);

			try {

				username = this.jwtUtility.getUsernameFromToken(jwtToken);

			}
			catch(Exception e){
				e.printStackTrace();
			}

			//security
			if(username != null && SecurityContextHolder.getContext().getAuthentication()==null)
			{
				UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username); 

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		  		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

			}
			else
			{
				System.out.println("Token is not validated...");
			}
			 
		}

		filterChain.doFilter(request, response);
	}
}
