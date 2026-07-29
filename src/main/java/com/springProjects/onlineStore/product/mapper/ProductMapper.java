package com.springProjects.onlineStore.product.mapper;

import com.springProjects.onlineStore.category.mapper.CategoryMapper;
import com.springProjects.onlineStore.product.dto.CreateProductDTO;
import com.springProjects.onlineStore.product.dto.ProductResponseDTO;
import com.springProjects.onlineStore.product.dto.ProductSummaryResponseDTO;
import com.springProjects.onlineStore.product.dto.UpdateProductDTO;
import com.springProjects.onlineStore.product.entity.Product;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public ProductResponseDTO toResponseDTO(Product product) {
        if(product == null) {
            return null;
        }
        ProductResponseDTO productResponseDTO = modelMapper.map(product, ProductResponseDTO.class);
        if(product.getCategory() != null) {
            productResponseDTO.setCategoryResponseDTO(categoryMapper.toResponseDTO(product.getCategory()));
        }
        return productResponseDTO;
    }

    public ProductSummaryResponseDTO toSummaryResponseDTO(Product product) {
        if(product == null) {
            return null;
        }
        return modelMapper.map(product, ProductSummaryResponseDTO.class);
    }

    public Product toEntity(CreateProductDTO createProductDTO) {
        if(createProductDTO == null) {
            return null;
        }
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(createProductDTO, Product.class);
    }

    public Product toEntity(UpdateProductDTO updateProductDTO) {
        if(updateProductDTO == null) {
            return null;
        }
        // Set strict matching strategy to avoid FUZZY matching  (data-type matching - not exact key-name)
        // categoryId is Integer  ->  can be by-mistake mapped to productId
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(updateProductDTO, Product.class);
    }
}
