package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.LoginRequest;
import com.example.demo.entity.Users;
import com.example.demo.service.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/auth")
public class AuthController {
private final AuthService authService;

@Autowired
public AuthController(AuthService authService) {
	this.authService = authService;
}

@PostMapping("/login")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
	try {
		System.out.println(loginRequest.getUsername()+ " " + loginRequest.getPassword());
		// Authenticate user and get the role
		Users user = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
		
		// generate JWT token
		String token = authService.generateToken(user);
		
		// Set token as HttpOnly
		Cookie cookie = new Cookie("authToken", token);
		cookie.setHttpOnly(true);
		cookie.setSecure(false);
		cookie.setPath("/");
		cookie.setMaxAge(3600);
		cookie.setDomain("localhost");// 1 hour
		response.addCookie(cookie);
		
		 response.addHeader("Set-Cookie",
                 String.format("authToken=%s; HttpOnly; Path=/; Max-Age=3600; SameSite=None", token));
		// Return user role in reponse body
		Map<String, Object> reponseBody = new HashMap();
		reponseBody.put("message", "Login successful");
		reponseBody.put("role", user.getRole().name());
		reponseBody.put("username", user.getUserName());
		return ResponseEntity.ok(reponseBody);
	}
	catch(RuntimeException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
		
	}
}

@PostMapping("/logout")
public ResponseEntity<Map<String, String>> logout(HttpServletRequest request,HttpServletResponse response) {
    try {
    	Users user=(Users) request.getAttribute("authenticatedUser");
        authService.logout(user);
        Cookie cookie = new Cookie("authToken", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logout successful");
        return ResponseEntity.ok(responseBody);
    } catch (RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Logout failed");
        return ResponseEntity.status(500).body(errorResponse);
    }
}
@PostMapping("/forgot-password")
@CrossOrigin(origins = "http://localhost:5173")
public ResponseEntity<String> forgotPassword(@RequestParam String email) {
    try {
    	authService.processForgotPassword(email);
    	System.out.println("sent");
        return ResponseEntity.ok("OTP sent to your email.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PostMapping("/verify-otp")
@CrossOrigin(origins = "http://localhost:5173")
public ResponseEntity<String> verifyOtp( @RequestParam String otp) {
    boolean isValid = authService.verifyOtp(otp);
    if (isValid) {
        return ResponseEntity.ok("OTP verified successfully.");
    } else {
        return ResponseEntity.badRequest().body("Invalid OTP.");
    }
}

@PostMapping("/reset-password")
@CrossOrigin(origins = "http://localhost:5173")
public ResponseEntity<String> resetPassword(@RequestParam String newPassword) {
    try {
    	authService.resetPassword(newPassword);
        return ResponseEntity.ok("Password reset successfully.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
}
