package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Otprequests;

@Repository
public interface OtpRpository extends JpaRepository<Otprequests, Integer> {
	 // Find by email
    Optional<Otprequests> findByEmail(String email);

    // Delete by email
    void deleteByEmail(String email);
    Optional<Otprequests> findByOtp(String otp);
}
