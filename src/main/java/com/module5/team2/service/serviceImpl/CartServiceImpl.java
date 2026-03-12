package com.module5.team2.service.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.module5.team2.entity.Cart;
import com.module5.team2.entity.CartItem;
import com.module5.team2.entity.ProductEntity;
import com.module5.team2.entity.UserEntity;
import com.module5.team2.enums.Role;
import com.module5.team2.exception.BusinessException;
import com.module5.team2.repository.CartItemRepository;
import com.module5.team2.repository.CartRepository;
import com.module5.team2.repository.ProductRepository;
import com.module5.team2.repository.UserRepository;
import com.module5.team2.service.service.CartService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    @Override
    public Cart getCartByCustomer(Integer customerId) {

        UserEntity customer = userRepository.findById(customerId).orElseThrow(()->new RuntimeException("Get user by id fail"));
        if(customer.getRole().equals(Role.USER)||customer.getRole().equals(Role.CUSTOMER)){
        return cartRepository.findByCustomer(customer).orElseGet(()->{
            Cart newCart= new Cart();
            newCart.setCustomer(customer);
            return cartRepository.save(newCart);
        });}
        else{
            throw new RuntimeException("Only customer and user can order");
        }
    }

    @Override
    public Cart addToCart(Integer customerId, Integer productId, Integer quantity) {
        Cart cart = getCartByCustomer(customerId);
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if(quantity>product.getQuantity()){
          throw  new RuntimeException("Product quantity is overd");
        }
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
              if(item.getQuantity()>product.getQuantity()){
          throw  new RuntimeException("Quantity of cart item is overd");
        }
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
             if(newItem.getQuantity()>product.getQuantity()){
          throw  new RuntimeException("Quantity of cart newItem is overd");
        }
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItem(Integer customerId, Integer productId, Integer quantity) {

        Cart cart = getCartByCustomer(customerId);
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
         if(quantity>product.getQuantity()){
          throw  new RuntimeException("Product quantity is overd");
        }
        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return cart;
    }

    @Override
    public void removeItem(Integer customerId, Integer productId) {
       Cart cart = getCartByCustomer(customerId);
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }

    @Override
    public void clearCart(Integer customerId) {
       Cart cart = getCartByCustomer(customerId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartById(Long cartId) {
         Cart cart=cartRepository.findById(cartId).orElseThrow(()-> new NullPointerException("Cart not found: " + cartId));


        return cart;
    }

    @Override
    public Cart getCartByProduct(Integer productId) {
        Cart cart= new Cart();
        try {
             List<CartItem> cardItems= cartItemRepository.findByProductId(productId);
          cart= cartRepository.findByItems(cardItems).orElse(null);
        } catch (NullPointerException e) {
            throw new BusinessException(e.getMessage()+e.getCause());
        }
       
         return cart;

    }

}
