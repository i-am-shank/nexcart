package com.springProjects.onlineStore.product.dto;

import com.springProjects.onlineStore.category.dto.CategoryResponseDTO;
import com.springProjects.onlineStore.file.dto.FileResponseDTO;
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
public class ProductResponseDTO {
    private Integer productId;

    private String title;

    private String description;

    private BigDecimal price;

    private Double discountPercentage;

    private Integer remainingQuantity;

    private Boolean inStock;

    private CategoryResponseDTO categoryResponseDTO;

    // Application-managed relation  ,  not Database / Spring Data JPA - managed
    // As parent_entity_id in File has Polymorphic relation with tables - User, Category, Product, etc.
    private List<FileResponseDTO> files = new ArrayList<>();
}
