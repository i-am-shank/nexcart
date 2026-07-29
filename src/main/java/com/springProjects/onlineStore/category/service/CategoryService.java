package com.springProjects.onlineStore.category.service;

import java.util.List;

import com.springProjects.onlineStore.category.dto.CategoryDetailResponseDTO;
import com.springProjects.onlineStore.category.dto.CategoryResponseDTO;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;

public interface CategoryService {
    /**
     * Create a category for provided title & description
     *
     * @param title category title
     * @param description category description
     * @return created category as CategoryResponseDTO
     * @throws IllegalArgumentException for invalid title
     */
    CategoryResponseDTO createCategory(String title, String description) throws IllegalArgumentException,
            ResourceNotFoundException;

    /**
     * Fetch categories for the provided categoryId list
     *
     * @param categoryIds might have some invalid categoryId (not validating them here)
     * @return list of category for valid & existing categoryId
     */
    List<CategoryResponseDTO> getCategoryList(List<Integer> categoryIds);

    /**
     * Update title & description of category entry for categoryId
     *
     * @param categoryId unique category entry for this
     * @param title updated title
     * @param description updated description
     * @param coverImageFileId updated cover-image fileId
     * @return updated category as CategoryResponseDTO
     * @throws IllegalArgumentException for invalid categoryId
     * @throws ResourceNotFoundException if category not exist for categoryId
     */
    CategoryResponseDTO updateCategory(Integer categoryId, String title, String description,
                                              Integer coverImageFileId) throws IllegalArgumentException,
            ResourceNotFoundException;

    /**
     * Search categories with the provided searchKeyword, returning paginated response if page no. & size provided
     *
     * @param searchKeyword searches category having title containing this keyword
     * @param pageNumber for offset-based pagination
     * @param pageSize for offset-based pagination
     * @return paginated list of CategoryResponseDTO
     */
    PageableResponseDTO<CategoryResponseDTO> searchCategory(String searchKeyword, Integer pageNumber,
                                                                   Integer pageSize);

    /**
     * Delete category table entry for provided categoryId, also deleting category cover-image files
     *
     * @param categoryId unique category-entry for this
     * @throws IllegalArgumentException for invalid categoryId
     * @throws ResourceNotFoundException if category not exists for provided categoryId
     * @throws UnsupportedOperationException if some product exists in this category
     */
    void deleteCategory(Integer categoryId) throws IllegalArgumentException, ResourceNotFoundException,
            UnsupportedOperationException;

    /**
     * Fetches category-details & products in it
     * @param categoryId unique category-entry for this
     * @return fetched category-details as CategoryDetailResponseDTO
     * @throws IllegalArgumentException for invalid categoryId
     * @throws ResourceNotFoundException if category not exists for provided categoryId
     */
    CategoryDetailResponseDTO getCategoryDetails(Integer categoryId) throws IllegalArgumentException,
            ResourceNotFoundException;
}
