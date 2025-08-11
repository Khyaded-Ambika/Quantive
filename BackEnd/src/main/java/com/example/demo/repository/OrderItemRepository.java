package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.OrderItems;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Integer> {
	 @Query("SELECT oi FROM OrderItems oi WHERE oi.order.orderId = :orderId")
	    List<OrderItems> findByOrderId(String orderId);
	 
	 @Query("SELECT oi FROM OrderItems oi WHERE oi.order.userId = :userId AND oi.order.status = 'SUCCESS'")
	 List<OrderItems> findSuccessfulOrderItemsByUserId(int userId);
}
