package com.springProjects.onlineStore.category.mapper;

import com.springProjects.onlineStore.category.dto.CategoryDetailResponseDTO;
import com.springProjects.onlineStore.category.dto.CategoryResponseDTO;
import com.springProjects.onlineStore.category.entity.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    @Autowired
    private ModelMapper modelMapper;

    public CategoryResponseDTO toResponseDTO(Category category) {
        if(category == null) {
            return null;
        }
        return modelMapper.map(category, CategoryResponseDTO.class);
    }

    public CategoryDetailResponseDTO toDetailResponseDTO(Category category) {
        if(category == null) {
            return null;
        }
        return modelMapper.map(category, CategoryDetailResponseDTO.class);
    }
}
