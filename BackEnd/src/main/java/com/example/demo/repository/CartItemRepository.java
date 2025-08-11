package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Products;
import com.example.demo.entity.Users;

import jakarta.transaction.Transactional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	 // Custom query to count cart items for a given userId
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user.id = :userId")
    int countCartItemsByUserId(@Param("userId") int userId);

    // Custom query to fetch userId by username (assumes relationship exists)
    @Query("SELECT u.id FROM Users u WHERE u.userName = :userName")
    int findUserIdByUsername(@Param("userName") String username);
   // =============================================================
     @Query("SELECT c FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
    Optional<CartItem> findByUserAndProduct(int userId, int productId);

   // @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user = :user")
    //int countTotalItems(@Param("user") Users user);

   // List<CartItem> findAllByUser(Users user);
    
    @Query("SELECT c FROM CartItem c JOIN FETCH c.product p LEFT JOIN FETCH ProductImage pi ON p.productId = pi.product.productId WHERE c.user.userId = :userId")
    List<CartItem> findCartItemsWithProductDetails(int userId);
    
    @Query("UPDATE CartItem c SET c.quantity = :quantity WHERE c.id = :cartItemId")
    void updateCartItemQuantity(int cartItemId, int quantity);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId")
    void deleteCartItem(int userId, int productId);
    
    // Count the total quantity of items in the cart
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user.userId = :userId")
    int countTotalItems(int userId);
    
    
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user.userId = :userId")
    void deleteAllCartItemsByUserId(int userId);
}
