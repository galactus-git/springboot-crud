package com.springboot.restapi.service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.restapi.entity.Role;
import com.springboot.restapi.entity.User;
import com.springboot.restapi.payload.UserDetailsImpl;
import com.springboot.restapi.repository.UserDetailsRepository;
import com.springboot.restapi.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private PasswordEncoder passwordEncoder;
	
    private UserDetailsRepository userDetailsRepository;;
    
    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
      return new org.springframework.security.core.userdetails.User(user.getEmail(),
            user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }
	
//	public User findByUsername(String username)
//	{
//		return userRepository.findByUsername(username);
//	}
//	public User loadUserByUsernameNPassword(String username, String password)
//	{
//		User user = findByUsername(username);
//		
//		if(user != null)
//		{
//			if(passwordEncoder.matches(	password, user.getPassword())) 
//			{
//				return user;
//			}
//		}
//		
//		return null;
//	}
    
//    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Set<User> users){
//        return users.stream().map(user -> new SimpleGrantedAuthority(user.getEmail())).collect(Collectors.toList());
//    }
//
//    @Override
//    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String email)
//    		throws UsernameNotFoundException
//    {
//    	UserDetails userDetails = userDetailsRepository.findByEmail(email)
//    			.orElseThrow(() ->
//    			new UsernameNotFoundException("User not found with email:" + email));
//    	return new org.springframework.security.core.userdetails.User(userDetails.getEmail(),null,
//    			mapRolesToAuthorities(userDetails.getUser()));
//    }
//    
//    @Override
//    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
//       User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
//               .orElseThrow(() ->
//                       new UsernameNotFoundException("User not found with username or email:" + usernameOrEmail));
//        return new org.springframework.security.core.userdetails.User(user.getEmail(),
//                user.getPassword(), mapRolesToAuthorities(user.getRoles()));
//    }

//    /* @Override
//     public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
//        UserDetails userDetails = userDetailsRepository.findByEmail(email)
//                .orElseThrow(() ->
//                        new UsernameNotFoundException("User not found with email:" + email));
//         return new org.springframework.security.core.userdetails.User(userDetails.getEmail(),
//                 userDetails.getPassword(), mapRolesToAuthorities(userDetails.getRoles()));
//     }*/

}