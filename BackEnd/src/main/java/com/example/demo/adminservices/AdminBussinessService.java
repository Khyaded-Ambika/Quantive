package com.example.demo.adminservices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.demo.OrderStatus;
import com.example.demo.entity.OrderItems;
import com.example.demo.entity.Orders;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;

@Service
public class AdminBussinessService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;

	public AdminBussinessService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
	}
	
	public Map<String, Object> calculateMonthlyBusiness(int month, int year) {
		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Invalid month: " + month);
		}
		if (year < 2000 || year > 2100) { // Adjust range as needed
			throw new IllegalArgumentException("Invalid year: " + year);
		}

		// Fetch successful orders
		List<Orders> successfulOrders = orderRepository.findSuccessfulOrdersByMonthAndYear(month, year);

		// Calculate total business
		double totalBusiness = 0.0;
		Map<String, Integer> categorySales = new HashMap<>();

		for (Orders order : successfulOrders) {
			totalBusiness += order.getTotalAmount().doubleValue();

			List<OrderItems> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
			for (OrderItems item : orderItems) {
				// Fetch category name based on productId
				String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
			}
		}

		// Prepare the response
		Map<String, Object> businessReport = new HashMap<>();
		businessReport.put("totalBusiness", totalBusiness);
		businessReport.put("categorySales", categorySales);

		return businessReport;
	}




	public Map<String, Object> calculateDailyBusiness(LocalDate date) {
		if (date == null) {
			throw new IllegalArgumentException("Invalid date: Date cannot be null");
		}

		// Fetch successful orders for the date
		List<Orders> successfulOrders = orderRepository.findSuccessfulOrdersByDate(date);

		// Calculate total business
		double totalBusiness = 0.0;
		Map<String, Integer> categorySales = new HashMap<>();

		for (Orders order : successfulOrders) {
			totalBusiness += order.getTotalAmount().doubleValue();

			List<OrderItems> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
			for (OrderItems item : orderItems) {
				// Fetch category name based on productId
				String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
				categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
			}
		}

		// Prepare the response
		Map<String, Object> businessReport = new HashMap<>();
		businessReport.put("totalBusiness", totalBusiness);
		businessReport.put("categorySales", categorySales);

		return businessReport;
	}





	public Map<String, Object> calculateYearlyBusiness(int year) {
        if (year < 2000 || year > 2100) { // Adjust range as needed
            throw new IllegalArgumentException("Invalid year: " + year);
        }

        // Fetch successful orders for the year
        List<Orders> successfulOrders = orderRepository.findSuccessfulOrdersByYear(year);

        // Calculate total business
        double totalBusiness = 0.0;
        Map<String, Integer> categorySales = new HashMap<>();

        for (Orders order : successfulOrders) {
            totalBusiness += order.getTotalAmount().doubleValue();

            List<OrderItems> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
            for (OrderItems item : orderItems) {
                // Fetch category name based on productId
                String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
                categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
            }
        }

        // Prepare the response
        Map<String, Object> businessReport = new HashMap<>();
        businessReport.put("totalBusiness", totalBusiness);
        businessReport.put("categorySales", categorySales);

        return businessReport;
    }
	
	
	
	public Map<String, Object> calculateOverallBusiness() {
	    BigDecimal totalBusinessAmount = orderRepository.calculateOverallBusiness();
	    List<Orders> successfulOrders = orderRepository.findAllByStatus(OrderStatus.SUCCESS);

	    Map<String, Integer> categorySales = new HashMap<>();
	    for (Orders order : successfulOrders) {
	        List<OrderItems> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
	        for (OrderItems item : orderItems) {
	            String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
	            categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
	        }
	    }

	    Map<String, Object> response = new HashMap<>();
	    response.put("totalBusiness", totalBusinessAmount.doubleValue());
	    response.put("categorySales", categorySales);
	    return response;
	}

}
