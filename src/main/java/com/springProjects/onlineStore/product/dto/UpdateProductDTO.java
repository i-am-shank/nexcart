package com.springProjects.onlineStore.product.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductDTO {
    private String title;

    private String description;

    @DecimalMin(value = "0.0", message = "Product price should be positive", inclusive = false)
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Discount percentage cannot be negative")
    @DecimalMax(value = "100.0", message = "Maximum allowed value for discountPercentage is 100.0")
    private BigDecimal discountPercentage;

    @Min(value = 0, message = "Minimum allowed value for remainingQuantity is 0")
    private Integer remainingQuantity;

    @Min(value = 1, message = "categoryId cannot be negative")
    private Integer categoryId;
}
