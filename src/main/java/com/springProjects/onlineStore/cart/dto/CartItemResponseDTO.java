package com.springProjects.onlineStore.cart.dto;

import com.springProjects.onlineStore.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDTO {
    private Integer cartItemId;

    private Integer cartId;

    private Product product;

    private Integer quantity;

    private Boolean saveForLater;
}
