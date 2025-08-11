package com.example.demo.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="tokens")
public class Token {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
private int tokenId;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@Column(nullable = false, name = "jwt_token")
	private String token;
	
	@Column(nullable = false, name = "expires_at")
	private LocalDateTime expiresAt;

	public Token() {
		super();
	}

	public Token(Users user, String token, LocalDateTime expiresAt) {
		super();
		this.user = user;
		this.token = token;
		this.expiresAt = expiresAt;
	}

	public Token(int tokenId, Users user, String token, LocalDateTime expiresAt) {
		super();
		this.tokenId = tokenId;
		this.user = user;
		this.token = token;
		this.expiresAt = expiresAt;
	}

	public int getTokenId() {
		return tokenId;
	}

	public void setTokenId(int tokenId) {
		this.tokenId = tokenId;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}
	
	
}
