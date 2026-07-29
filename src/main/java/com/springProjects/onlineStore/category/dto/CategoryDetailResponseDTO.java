package com.springProjects.onlineStore.category.dto;

import java.util.List;

import com.springProjects.onlineStore.product.dto.ProductResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponseDTO {
    private Integer categoryId;

    private String title;

    private String description;

    private Integer coverImageFileId;

    private List<ProductResponseDTO> products;
}
