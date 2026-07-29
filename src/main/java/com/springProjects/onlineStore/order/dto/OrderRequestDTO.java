package com.springProjects.onlineStore.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {
    private Integer userId;

    private Integer addressId;

    private BigDecimal totalPrice;

    private BigDecimal paidPrice;
}
