package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Token;

import jakarta.transaction.Transactional;

@Repository
public interface JWTTokenRepository extends JpaRepository<Token, Integer> {
	 Optional<Token> findByToken(String token);
	// Custom query to find tokens by user ID
	    @Query("SELECT t FROM Token t WHERE t.user.userId = :userId")
	    Token findByUserId(@Param("userId") int userId);

	    // Custom query to delete tokens by user ID
	    @Modifying
	    @Transactional
	    @Query("DELETE FROM Token t WHERE t.user.userId = :userId")
	    void deleteByUserId(@Param("userId") int userId);
}
