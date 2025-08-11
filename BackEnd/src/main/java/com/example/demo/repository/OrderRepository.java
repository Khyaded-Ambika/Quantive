package com.example.demo.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.OrderStatus;
import com.example.demo.entity.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
	// Custom query methods can be added here if needed
		 @Query("SELECT o FROM Orders o WHERE MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year AND o.status = 'SUCCESS'")
		    List<Orders> findSuccessfulOrdersByMonthAndYear(int month, int year);
		 
		 
		 
		 @Query("SELECT o FROM Orders o WHERE DATE(o.createdAt) = :date AND o.status = 'SUCCESS'")
		    List<Orders> findSuccessfulOrdersByDate(LocalDate date);


		 
		 @Query("SELECT o FROM Orders o WHERE YEAR(o.createdAt) = :year AND o.status = 'SUCCESS'")
		    List<Orders> findSuccessfulOrdersByYear(int year);
		 
		 
		 
		 
		 @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Orders o WHERE o.status = 'SUCCESS'")
		    BigDecimal calculateOverallBusiness();

		    @Query("SELECT o FROM Orders o WHERE o.status = :status")
		    List<Orders> findAllByStatus(OrderStatus status);	
}
