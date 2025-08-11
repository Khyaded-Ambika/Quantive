package com.example.demo.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Otprequests;
import com.example.demo.entity.Token;
import com.example.demo.entity.Users;
import com.example.demo.repository.JWTTokenRepository;
import com.example.demo.repository.OtpRpository;
import com.example.demo.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.security.Key;
@Service
public class AuthService {
//private final String SECRET_KEY = "iNku2J9Xw7LM1QlFTHBZ6fTNyD4cZeK/qCbzgRbh9RGV8kDyVYRd8yHKjeoEQ5ZUOzkxWYlH9yb8fxUP02MZgQ";
//private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	
//private final Key SIGNING_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
//@Autowired
//private UserRepository userRepository;
//
//
//@Autowired
//private JavaMailSender mailSender;
//
//@Autowired
//private OtpRpository otpRpository;
//
//@Autowired
//private JWTTokenRepository jwtTokenRepository;
//
////private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
//
////Temporary storage for OTPs (can be replaced with a database solution)
//private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

private String mail = null;
private final Key SIGNING_KEY;

private final UserRepository userRepository;
private final JWTTokenRepository jwtTokenRepository;
private final BCryptPasswordEncoder passwordEncoder;
private OtpRpository otpRpository;
private JavaMailSender mailSender;

// Injecting jwt.secret from properties file

@Autowired
public AuthService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository,JavaMailSender mailSender,OtpRpository otpRpository,
                   @Value("${jwt.secret}") String jwtSecret) {
    this.userRepository = userRepository;
    this.jwtTokenRepository = jwtTokenRepository;
    this.otpRpository = otpRpository;
    this.mailSender = mailSender;
    this.passwordEncoder = new BCryptPasswordEncoder();

    // Ensure the key length is at least 64 bytes
    if (jwtSecret.getBytes(StandardCharsets.UTF_8).length < 64) {
        throw new IllegalArgumentException("JWT_SECRET in application.properties must be at least 64 bytes long for HS512.");
    }
    this.SIGNING_KEY = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
}


public Users authenticate(String username, String password) {
	Users user = userRepository.findByUserName(username).orElseThrow(() -> new RuntimeException("Invalid username "));
	
	if (!passwordEncoder.matches(password, user.getPassword())) {
		throw new RuntimeException("Invalid  password");
	}
	return user;
}

//public String generateToken(Users user) {
//    String token = Jwts.builder()
//            .setSubject(user.getUserName())
//            .claim("role", user.getRole().name())
//            .setIssuedAt(new Date())
//            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
//            .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
//            .compact();
//
//    saveToken(user, token);
//    return token;
//}
public String generateToken(Users user) {
    String token;
    LocalDateTime now = LocalDateTime.now();
    Token existingToken = jwtTokenRepository.findByUserId(user.getUserId());

    if (existingToken != null && now.isBefore(existingToken.getExpiresAt())) {
        token = existingToken.getToken();
    } else {
        token = generateNewToken(user);
        if (existingToken != null) {
            jwtTokenRepository.delete(existingToken);
        }
        saveToken(user, token);
    }
    return token;
}

private String generateNewToken(Users user) {
    return Jwts.builder()
            .setSubject(user.getUserName())
            .claim("role", user.getRole().name())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
            .signWith(SIGNING_KEY, SignatureAlgorithm.HS512)
            .compact();
}

public void saveToken(Users user, String token) {
    Token jwtToken = new Token(user, token, LocalDateTime.now().plusHours(1));
    jwtTokenRepository.save(jwtToken);
}

//public void logout(HttpServletResponse response) {
//    Cookie cookie = new Cookie("authToken", null);
//    cookie.setHttpOnly(true);
//    cookie.setMaxAge(0);
//    cookie.setPath("/");
//    response.addCookie(cookie);
//
//    // Invalidate the token in the database (if exists)
//    Optional<Token> jwtToken = jwtTokenRepository.findByToken(cookie.getValue());
//    jwtToken.ifPresent(jwtTokenRepository::delete);
//}

public void logout(Users user) {
	 jwtTokenRepository.deleteByUserId(user.getUserId());
    
}
public boolean validateToken(String token) {
    try {
        System.err.println("VALIDATING TOKEN...");

        // Parse and validate the token
        Jwts.parserBuilder()
            .setSigningKey(SIGNING_KEY)
            .build()
            .parseClaimsJws(token);

        // Check if the token exists in the database and is not expired
        Optional<Token> jwtToken = jwtTokenRepository.findByToken(token);
        if (jwtToken.isPresent()) {
            System.err.println("Token Expiry: " + jwtToken.get().getExpiresAt());
            System.err.println("Current Time: " + LocalDateTime.now());
            return jwtToken.get().getExpiresAt().isAfter(LocalDateTime.now());
        }

        return false;
    } catch (Exception e) {
        System.err.println("Token validation failed: " + e.getMessage());
        return false;
    }
}

public String extractUsername(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(SIGNING_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
}


public void processForgotPassword(String email) {
	    // Retrieve user from the repository and throw an exception if not found
	    Optional<Users> userOptional = userRepository.findByEmail(email);
	   // System.out.println("User details retrieved: " +userOptional );
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Email not found in the system");
        }
	    
    // Generate OTP
     String otp = generateOtp();
     Otprequests otprequests = new Otprequests();
     otprequests.setEmail(email);
     otprequests.setOtp(otp);
     otprequests.setCreated_at(LocalDateTime.now());
     otpRpository.save(otprequests);
     
    
    // Send OTP to the user's email
    sendEmail(email, "Your OTP for Password Reset", "Your OTP is: " + otp);
}

private String generateOtp() {
    Random random = new Random();
    return String.valueOf(100000 + random.nextInt(900000)); // Generate a 6-digit OTP
}

private void sendEmail(String to, String subject, String text) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
        System.out.println("Email sent successfully to " + to);
    } catch (Exception e) {
        System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        throw new RuntimeException("Failed to send email. Please try again later.");
    }
}
public boolean verifyOtp(String otp) {
    // Fetch the reset request using OTP
    Optional<Otprequests> request = otpRpository.findByOtp(otp);

    if (request.isPresent()) {
    	Otprequests resetRequest = request.get();
        LocalDateTime now = LocalDateTime.now();

        // Check OTP validity (10 minutes expiration)
        if (resetRequest.getCreated_at().isAfter(now.minusMinutes(10))) {
            mail = resetRequest.getEmail(); // Update the mail variable for further use
            return true;
        } else {
            throw new IllegalArgumentException("OTP has expired.");
        }
    } else {
        throw new IllegalArgumentException("Invalid OTP.");
    }
}



public void resetPassword(String newPassword) {
	if (mail == null) {
        throw new IllegalStateException("OTP verification is not initiated. Please request a password reset first.");
    }
    Optional<Users> userOptional = userRepository.findByEmail(mail);
    if (userOptional.isPresent()) {
        Users user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword)); // Hash the password
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        otpRpository.deleteByEmail(mail);
        // Remove OTP after successful password reset
        System.out.println("Password reset successfully for " + mail);
        mail=null;
    } else {
        throw new IllegalArgumentException("Email not found");
    }
}
}
