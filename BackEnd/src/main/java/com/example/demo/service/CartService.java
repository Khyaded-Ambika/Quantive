package com.example.demo.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.ProductImage;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;

@Service
public class CartService {

	    @Autowired
	    private CartItemRepository cartRepository;

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private ProductRepository productRepository;

	    @Autowired
	    private ProductImageRepository productImageRepository;
	    
	    // Get the total cart item count for a user
	    public int getCartItemCount(int userId) {
//	        Users user = userRepository.findById(userId)
//	                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
	        return cartRepository.countTotalItems(userId);
	    }

	    // Add an item to the cart
	    public void addToCart(int userId, int productId, int quantity) {
	        Users user = userRepository.findById(userId)
	                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

	        Products product = productRepository.findById(productId)
	                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

	        Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);

	        if (existingItem.isPresent()) {
	            CartItem cartItem = existingItem.get();
	            cartItem.setQuantity(cartItem.getQuantity() + quantity);
	            cartRepository.save(cartItem);
	        } else {
	            CartItem newItem = new CartItem(user, product, quantity);
	            cartRepository.save(newItem);
	        }
	    }
	    
	 // Get Cart Items for a User
	    public Map<String, Object> getCartItems(int userId) {
	    	List<CartItem> cartItems = cartRepository.findCartItemsWithProductDetails(userId);
	    	
	    	Map<String, Object> response = new HashMap<>();
	    	Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
	    	
	    	response.put("username", user.getUserName());
	    	response.put("role", user.getRole().toString());
	    	
	    	List<Map<String, Object>> products = new ArrayList<>();
	    	double overallTotalPrice = 0;
	    	
	    	for(CartItem cartItem: cartItems) {
	    		Map<String, Object> productDetails = new HashMap<>();
	    		Products product = cartItem.getProduct();
	    		
	    		// Fetch product images

	    		List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(product.getId()); 
	    		String imageUrl= null;

				if (productImages != null && !productImages.isEmpty()) {
					// If there are images, get the first image's URL
					imageUrl = productImages.get(0).getImageUrl();
				} else {
					// Set a default image if no images are available
					imageUrl = "default-image-url";  // You can replace this with your default image URL
				}

	    		// Populate product details

	    		productDetails.put("product_id", product.getId());

	    		productDetails.put("image_url", imageUrl);

	    		productDetails.put("name", product.getName());

	    		productDetails.put("description", product.getDescription());

	    		productDetails.put("price_per_unit", product.getPrice());

	    		productDetails.put("quantity", cartItem.getQuantity());

	    		productDetails.put("total_price", cartItem.getQuantity() * product.getPrice().doubleValue());

	    		// Add to products list 
	    		products.add(productDetails);

	    		// Update overall total price

	    		overallTotalPrice += cartItem.getQuantity() *product.getPrice().doubleValue();

	    		}

	    		// Prepare the final cart response

	    		Map<String, Object> cart = new HashMap<>();

	    		cart.put("products", products);

	    		cart.put("overall_total_price", overallTotalPrice);
	    		response.put("cart", cart);
	    		return response;
	    	}
	    
	    public void updateCartItemQuantity(int userId, int productId, int quantity) {
	    	Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
	    	
	    	Products product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found "));
	    	
	    	Optional<CartItem> existingItem = cartRepository.findByUserAndProduct(userId, productId);
	    	
	    	if(existingItem.isPresent()) {
	    		CartItem cartItem = existingItem.get();
	    		if(quantity == 0) {
	    			deleteCartItem(userId, productId);
	    		} else {
	    			cartItem.setQuantity(quantity);
	    			cartRepository.save(cartItem);
	    		}
	    	}
	    	
	    }
	    
	    public void deleteCartItem(int userId, int productId) {
	    	Users user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
	    	
	    	Products product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
	    	
	    	cartRepository.deleteCartItem(userId, productId);
	    }
	    }
	

