package com.module5.team2.service.service;

import com.module5.team2.entity.Cart;

public interface CartService {
    Cart getCartByCustomer(Integer customerId);
    Cart getCartById(Long cartId);
    Cart addToCart(Integer customerId, Integer productId, Integer quantity);
    Cart updateItem(Integer customerId, Integer productId, Integer quantity);
    void removeItem(Integer customerId, Integer productId);
    void clearCart(Integer customerId);
    Cart getCartByProduct(Integer productId);
}
