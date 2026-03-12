package com.module5.team2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.module5.team2.entity.Cart;
import com.module5.team2.entity.CartItem;
import com.module5.team2.entity.ProductEntity;
import java.util.List;


public interface CartItemRepository extends JpaRepository<CartItem, Long> {
        Optional<CartItem> findByCartAndProduct(Cart cart, ProductEntity product);
       List<CartItem> findByProductId(Integer productId);

}
