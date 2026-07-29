package com.springProjects.onlineStore.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {
    private Integer cartId;

    private Integer userId;

    // Non-deleted Cart-items
    private Integer totalItems = 0;

    // Non-deleted Cart-items
    private List<CartItemResponseDTO> cartItems = new ArrayList<>();
}
