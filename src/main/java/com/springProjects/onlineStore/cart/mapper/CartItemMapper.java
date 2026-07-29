package com.springProjects.onlineStore.cart.mapper;

import com.springProjects.onlineStore.cart.dto.CartItemResponseDTO;
import com.springProjects.onlineStore.cart.entity.CartItem;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {

    public CartItemResponseDTO toResponseDTO(CartItem cartItem) {
        CartItemResponseDTO cartItemResponseDTO = new CartItemResponseDTO();
        if(cartItem != null) {
            cartItemResponseDTO.setCartItemId(cartItem.getCartItemId());
            cartItemResponseDTO.setProduct(cartItem.getProduct());
            cartItemResponseDTO.setQuantity(cartItem.getQuantity());
            cartItemResponseDTO.setSaveForLater(cartItem.getSaveForLater());
            if(cartItem.getCart() != null) {
                cartItemResponseDTO.setCartId(cartItem.getCart().getCartId());
            }
        }
        return cartItemResponseDTO;
    }
}
