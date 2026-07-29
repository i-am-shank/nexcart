package com.springProjects.onlineStore.cart.service;

import com.springProjects.onlineStore.cart.dto.CartDetailsResponseDTO;
import com.springProjects.onlineStore.cart.dto.CartPaymentDetailsDTO;
import com.springProjects.onlineStore.cart.dto.CartResponseDTO;
import com.springProjects.onlineStore.cart.entity.Cart;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;

public interface CartService {
    CartDetailsResponseDTO createCartForUser(Integer userId) throws IllegalArgumentException, ResourceNotFoundException;
    
    Cart createCart(Integer userId) throws IllegalArgumentException, ResourceNotFoundException,
            UnsupportedOperationException;

    CartResponseDTO getCart(Integer userId) throws IllegalArgumentException, ResourceNotFoundException;

    CartDetailsResponseDTO getCartDetails(Integer userId) throws IllegalArgumentException, ResourceNotFoundException;

    CartPaymentDetailsDTO getCartPaymentDetails(Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException;

    CartDetailsResponseDTO clearCart(Integer userId, Boolean restoreInventory) throws IllegalArgumentException, ResourceNotFoundException;

    Cart updateCartItemsAndPaymentDetails(Integer cartId) throws IllegalArgumentException, ResourceNotFoundException;
}
