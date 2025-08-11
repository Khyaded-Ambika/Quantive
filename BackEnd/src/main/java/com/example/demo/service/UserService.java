package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

	private  final UserRepository userRepository ;
	private final  BCryptPasswordEncoder passwordEncoder ;
	
	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}
	
	public Users registerUser(Users user) {
		if(userRepository.findByUserName(user.getUserName()).isPresent()) {
			throw new RuntimeException("Username is already taken");
		}
		
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email is already taken");
		}
		
//	        if (user.getUserName() == null || user.getUserName().isEmpty()) {
//	            return ResponseEntity.badRequest().body("UserName is required");
//	        }
		
		// Encode password before saving
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		// Save the user
		return userRepository.save(user);
	}
}
