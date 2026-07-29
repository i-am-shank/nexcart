package com.springProjects.onlineStore.category.controller;

import com.springProjects.onlineStore.category.dto.CategoryDetailResponseDTO;
import com.springProjects.onlineStore.category.dto.CategoryResponseDTO;
import com.springProjects.onlineStore.category.service.CategoryService;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ResponseDTO> createCategory(@RequestParam("title") String title,
                                                      @RequestParam(value = "description", required = false)
                                                      String description) throws IllegalArgumentException,
            ResourceNotFoundException {
        CategoryResponseDTO categoryResponseDTO = categoryService.createCategory(title, description);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Category created successfully",
                categoryResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseDTO> getCategories(@RequestParam("categoryIds") List<Integer> categoryIds) {
        List<CategoryResponseDTO> categoryResponseDTOS = categoryService.getCategoryList(categoryIds);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Categories fetched successfully",
                categoryResponseDTOS);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ResponseDTO> updateCategory(@PathVariable Integer categoryId,
                                                      @RequestParam(value = "title", required = false) String title,
                                                      @RequestParam(value = "description", required = false)
                                                      String description) throws IllegalArgumentException,
            ResourceNotFoundException {
        CategoryResponseDTO categoryResponseDTO = categoryService.updateCategory(categoryId, title,
                description, null);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Category updated successfully",
                categoryResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/search/{searchKeyword}")
    public ResponseEntity<ResponseDTO> searchCategory(@PathVariable String searchKeyword,
                                                      @RequestParam(value = "pageNumber", required = false)
                                                      Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false)
                                                      Integer pageSize) {
        PageableResponseDTO<CategoryResponseDTO> categoryResponseDTOPageableResponseDTO =
                categoryService.searchCategory(searchKeyword, pageNumber, pageSize);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK,
                "Searched categories fetched successfully", categoryResponseDTOPageableResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ResponseDTO> deleteCategory(@PathVariable Integer categoryId)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException {
        categoryService.deleteCategory(categoryId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Category deleted successfully");
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/details/{categoryId}")
    public ResponseEntity<ResponseDTO> getCategoryDetails(@PathVariable Integer categoryId)
            throws IllegalArgumentException, ResourceNotFoundException {
        CategoryDetailResponseDTO categoryDetailResponseDTO = categoryService.getCategoryDetails(categoryId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Category details fetched successfully",
                categoryDetailResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
