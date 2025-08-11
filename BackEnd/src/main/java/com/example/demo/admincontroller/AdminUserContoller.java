package com.example.demo.admincontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.adminservices.AdminUserService;
import com.example.demo.entity.Users;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/admin/user")
public class AdminUserContoller {

	 private final AdminUserService adminUserService;
	    public AdminUserContoller(AdminUserService adminUserService) {
	        this.adminUserService = adminUserService;
	    }

	    @PutMapping("/modify")
	    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	    public ResponseEntity<?> modifyUser(@RequestBody Map<String, Object> userRequest) {
	    	 try {
	             Integer userId = (Integer) userRequest.get("userId");
	             String username = (String) userRequest.get("username");
	             String email = (String) userRequest.get("email");
	             String role = (String) userRequest.get("role");
	             Users updatedUser = adminUserService.modifyUser(userId, username, email, role);
	             Map<String, Object> response = new HashMap<>();
	             response.put("userId", updatedUser.getUserId());
	             response.put("username", updatedUser.getUserName());
	             response.put("email", updatedUser.getEmail());
	             response.put("role", updatedUser.getRole().name());
	             response.put("createdAt", updatedUser.getCreatedAt());
	             response.put("updatedAt", updatedUser.getUpdatedAt());
	             return ResponseEntity.status(HttpStatus.OK).body(response);
	         } catch (IllegalArgumentException e) {
	             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	         } catch (Exception e) {
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
	         }
	    }

	    @PostMapping("/getbyid")
	    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	    public ResponseEntity<?> getUserById(@RequestBody Map<String, Integer> userRequest) {
	    	 try {
	             Integer userId = userRequest.get("userId");
	             Users user = adminUserService.getUserById(userId);
	             return ResponseEntity.status(HttpStatus.OK).body(user);
	         } catch (IllegalArgumentException e) {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	         } catch (Exception e) {
	             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong in backend");
	         }
	     }
}
