package com.module5.team2.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.module5.team2.dto.request.AddCartRequest;
import com.module5.team2.entity.Cart;
import com.module5.team2.service.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @GetMapping("/{customerId}")
    public ResponseEntity<Cart> getCart(@PathVariable Integer customerId) {
        Cart cart=cartService.getCartByCustomer(customerId);
        
        return ResponseEntity.ok(cart);
    }
@PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestBody  AddCartRequest  addCartRequest
        ) {
            Cart newcart=cartService.addToCart(addCartRequest.getCustomerId(), addCartRequest.getProductId(), addCartRequest.getQuantity());
        return ResponseEntity.ok(newcart);
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateItem(
            @RequestBody  AddCartRequest  addCartRequest) {

        return ResponseEntity.ok(cartService.updateItem(addCartRequest.getCustomerId(), addCartRequest.getProductId(), addCartRequest.getQuantity()));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeItem(
            @RequestParam Integer customerId,
            @RequestParam Integer productId) {

        cartService.removeItem(customerId, productId);
        return ResponseEntity.ok("Item removed");
    }

    @DeleteMapping("/clear/{customerId}")
    public ResponseEntity<String> clearCart(@PathVariable Integer customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.ok("Cart cleared");
    }

    // @GetMapping("/find-by-product/{productId}")
    // public ResponseEntity<Cart> addToCartInfo(@RequestParam Integer customerId,
    //        @PathVariable Integer  productId) {

    //     return ResponseEntity.ok(cartService.getCartByProduct(customerId, productId));
    // }

    
    
    // end
}
