package com.springProjects.onlineStore.order.dto;

import com.springProjects.onlineStore.product.dto.ProductResponseDTO;
import com.springProjects.onlineStore.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Integer orderItemId;

    private Integer orderId;

    private ProductResponseDTO product;

    private Integer quantity;

    private BigDecimal totalAmount;

    private BigDecimal actualAmount;
}
