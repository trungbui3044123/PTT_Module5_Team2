package com.module5.team2.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.module5.team2.entity.Cart;
import com.module5.team2.entity.UserEntity;
import java.util.List;
import com.module5.team2.entity.CartItem;


public interface CartRepository extends JpaRepository<Cart, Long>{
        Optional<Cart> findByCustomer(UserEntity customer);
        Optional<Cart> findByItemsAndCustomer(List<CartItem> items, UserEntity customer);
        Optional<Cart> findByItems(List<CartItem> items);
}
