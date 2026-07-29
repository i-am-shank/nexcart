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
public class CartPaymentDetailsDTO {
    private Integer cartId;

    private Integer userId;

    // Non-deleted Cart-items
    private Integer totalItems = 0;

    // Non-deleted & Not savedForLater Cart-items
    private Integer totalBilledItems = 0;

    // Non-deleted Cart-items
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Non-deleted & Not savedForLater Cart-items
    private BigDecimal amountToBePaid = BigDecimal.ZERO;

    // Non-deleted & Not savedForLater Cart-items
    private List<CartItemResponseDTO> cartItems = new ArrayList<>();

    // Non-deleted & savedForLater Cart-items
    private List<CartItemResponseDTO> savedForLaterItems = new ArrayList<>();
}
