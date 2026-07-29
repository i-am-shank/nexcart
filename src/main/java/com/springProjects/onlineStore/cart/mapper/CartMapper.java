package com.springProjects.onlineStore.cart.mapper;

import com.springProjects.onlineStore.cart.dto.CartDetailsResponseDTO;
import com.springProjects.onlineStore.cart.dto.CartPaymentDetailsDTO;
import com.springProjects.onlineStore.cart.dto.CartResponseDTO;
import com.springProjects.onlineStore.cart.entity.Cart;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {
    @Autowired
    private ModelMapper modelMapper;

    public CartResponseDTO toResponseDTO(Cart cart) {
        if(cart == null) {
            return null;
        }
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(cart, CartResponseDTO.class);
    }

    public CartDetailsResponseDTO toDetailResponseDTO(Cart cart) {
        if(cart == null) {
            return null;
        }
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(cart, CartDetailsResponseDTO.class);
    }

    public CartPaymentDetailsDTO toPaymentDetailsDTO(Cart cart) {
        if(cart == null) {
            return null;
        }
        CartPaymentDetailsDTO cartPaymentDetailsDTO = new CartPaymentDetailsDTO();
        cartPaymentDetailsDTO.setCartId(cart.getCartId());
        cartPaymentDetailsDTO.setTotalItems(cart.getTotalItems());
        cartPaymentDetailsDTO.setTotalAmount(cart.getTotalAmount());
        cartPaymentDetailsDTO.setAmountToBePaid(cart.getAmountToBePaid());
        return cartPaymentDetailsDTO;
    }
}
