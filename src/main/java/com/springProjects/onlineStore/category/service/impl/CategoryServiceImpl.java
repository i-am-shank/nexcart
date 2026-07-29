package com.springProjects.onlineStore.category.service.impl;

import com.springProjects.onlineStore.category.dto.CategoryDetailResponseDTO;
import com.springProjects.onlineStore.category.dto.CategoryResponseDTO;
import com.springProjects.onlineStore.category.entity.Category;
import com.springProjects.onlineStore.category.mapper.CategoryMapper;
import com.springProjects.onlineStore.category.repository.CategoryRepository;
import com.springProjects.onlineStore.category.service.CategoryService;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.file.constants.FileType;
import com.springProjects.onlineStore.file.entity.File;
import com.springProjects.onlineStore.file.repository.FileRepository;
import com.springProjects.onlineStore.product.dto.ProductResponseDTO;
import com.springProjects.onlineStore.product.entity.Product;
import com.springProjects.onlineStore.product.mapper.ProductMapper;
import com.springProjects.onlineStore.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public CategoryResponseDTO createCategory(String title, String description) throws IllegalArgumentException {
        if(!StringUtils.hasLength(title)) {
            throw new IllegalArgumentException("Category title is mandatory");
        }
        Category category = new Category(title, description);
        categoryRepository.save(category);
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    public List<CategoryResponseDTO> getCategoryList(List<Integer> categoryIds) {
        if(CollectionUtils.isEmpty(categoryIds)) {
            return new ArrayList<>();
        }
        List<Category> categories = categoryRepository.findByCategoryIdInAndDeletedFalse(categoryIds);
        return categories.stream()
                .map(category -> categoryMapper.toResponseDTO(category))
                .toList();
    }

    @Override
    public CategoryResponseDTO updateCategory(Integer categoryId, String title, String description,
                                              Integer coverImageFileId) throws IllegalArgumentException,
            ResourceNotFoundException {
        Category category = getCategoryById(categoryId);
        if(StringUtils.hasLength(title)) {
            category.setTitle(title);
        }
        if(StringUtils.hasLength(description)) {
            category.setDescription(description);
        }
        if(coverImageFileId != null) {
            category.setCoverImageFileId(coverImageFileId);
        }
        categoryRepository.save(category);
        return categoryMapper.toResponseDTO(category);
    }

    public Category getCategoryById(Integer categoryId) throws IllegalArgumentException, ResourceNotFoundException {
        if(categoryId == null) {
            throw new IllegalArgumentException("categoryId is empty");
        }
        Category category = categoryRepository.findByCategoryIdAndDeletedFalse(categoryId);
        if(category == null) {
            throw new ResourceNotFoundException("Category not found with id : " + categoryId);
        }
        return category;
    }

    @Override
    public PageableResponseDTO<CategoryResponseDTO> searchCategory(String searchKeyword, Integer pageNumber,
                                                                   Integer pageSize) {
        if(StringUtils.hasLength(searchKeyword)) {
            return getSearchedCategories(searchKeyword, pageNumber, pageSize);
        }
        return getAllCategory(pageNumber, pageSize);
    }

    private PageableResponseDTO<CategoryResponseDTO> getAllCategory(Integer pageNumber, Integer pageSize) {
        if(pageNumber!=null && pageSize!=null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Category> categories = categoryRepository.findByDeletedFalse(pageable);
            List<CategoryResponseDTO> categoryResponseDTOS = categories.stream()
                    .map(category -> categoryMapper.toResponseDTO(category))
                    .collect(Collectors.toList());
            return new PageableResponseDTO<>(
                    categoryResponseDTOS, categories.isFirst(), categories.isLast(), categories.getNumber(),
                    categories.getSize(), categories.getTotalElements(), categories.getTotalPages());
        } else {
            List<Category> categories = categoryRepository.findByDeletedFalse();
            List<CategoryResponseDTO> categoryResponseDTOS = categories.stream()
                    .map(category -> categoryMapper.toResponseDTO(category))
                    .collect(Collectors.toList());
            return new PageableResponseDTO<>(
                    categoryResponseDTOS, Boolean.TRUE, Boolean.TRUE, 0, categories.size(),
                    (long) categories.size(), 1);
        }
    }

    private PageableResponseDTO<CategoryResponseDTO> getSearchedCategories(String searchKeyword,
                                                                           Integer pageNumber, Integer pageSize) {
        if(pageNumber!=null && pageSize!=null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Category> categories = categoryRepository.findByTitleContainingAndDeletedFalse(
                    searchKeyword, pageable);
            List<CategoryResponseDTO> categoryResponseDTOS = categories.stream()
                    .map(category -> categoryMapper.toResponseDTO(category))
                    .collect(Collectors.toList());
            return new PageableResponseDTO<>(
                    categoryResponseDTOS, categories.isFirst(), categories.isLast(), categories.getNumber(),
                    categories.getSize(), categories.getTotalElements(), categories.getTotalPages());
        } else {
            List<Category> categories = categoryRepository.findByTitleContainingAndDeletedFalse(searchKeyword);
            List<CategoryResponseDTO> categoryResponseDTOS = categories.stream()
                    .map(category -> categoryMapper.toResponseDTO(category))
                    .collect(Collectors.toList());
            return new PageableResponseDTO<>(
                    categoryResponseDTOS, Boolean.TRUE, Boolean.TRUE, 0, categories.size(),
                    (long) categories.size(), 1);
        }
    }

    @Transactional
    @Override
    public void deleteCategory(Integer categoryId) throws IllegalArgumentException, ResourceNotFoundException,
            UnsupportedOperationException {
        // Validate if any product exists in this category
        List<Product> productList = productRepository.findByCategory_CategoryIdAndDeletedFalse(categoryId);
        if(!CollectionUtils.isEmpty(productList)) {
            throw new UnsupportedOperationException("Cannot delete category. Some product(s) exist in this category");
        }
        Category category = getCategoryById(categoryId);
        category.setDeleted(Boolean.TRUE);
        categoryRepository.save(category);
        // delete category cover-image  -  at max. 1 image (business logic constraint)
        List<File> categoryCoverImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(
                categoryId, FileType.CATEGORY_COVER_IMAGE);
        if(!CollectionUtils.isEmpty(categoryCoverImages)) {
            fileRepository.deleteAll(categoryCoverImages);
        }
    }

    @Override
    public CategoryDetailResponseDTO getCategoryDetails(Integer categoryId) throws IllegalArgumentException,
            ResourceNotFoundException {
        Category category = getCategoryById(categoryId);
        CategoryDetailResponseDTO categoryDetailResponseDTO = categoryMapper.toDetailResponseDTO(category);
        List<Product>  productList = productRepository.findByCategory_CategoryIdAndDeletedFalse(categoryId);
        List<ProductResponseDTO> categoryProductResponseDTOs = productList.stream()
                .map(product -> productMapper.toResponseDTO(product))
                .toList();
        categoryDetailResponseDTO.setProducts(categoryProductResponseDTOs);
        return categoryDetailResponseDTO;
    }
}
