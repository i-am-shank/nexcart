package com.springProjects.onlineStore.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponseDTO {
    private Integer productId;

    private String title;

    private BigDecimal price;

    private Double discountPercentage;
}
