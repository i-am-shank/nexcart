package com.springProjects.onlineStore.product.controller;

import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.product.dto.CreateProductDTO;
import com.springProjects.onlineStore.product.dto.ProductResponseDTO;
import com.springProjects.onlineStore.product.dto.ProductSummaryResponseDTO;
import com.springProjects.onlineStore.product.dto.UpdateProductDTO;
import com.springProjects.onlineStore.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ResponseDTO> createProduct(@Valid @RequestBody CreateProductDTO createProductDTO) {
        ProductResponseDTO productResponseDTO = productService.createProduct(createProductDTO);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Product created successfully",
                productResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ResponseDTO> getProductDetails(@PathVariable Integer productId)
            throws Exception {
        ProductResponseDTO productResponseDTO = productService.getProductDetails(productId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Product details fetched successfully",
                productResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ResponseDTO> updateProduct(@PathVariable Integer productId,
                                                     @Valid @RequestBody UpdateProductDTO updateProductDTO)
            throws IllegalArgumentException, ResourceNotFoundException {
        ProductResponseDTO productResponseDTO = productService.updateProduct(productId, updateProductDTO);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Product updated successfully",
                productResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDTO> searchProducts(@RequestParam("searchKeyword") String searchKeyword,
                                                      @RequestParam(value = "pageNumber", required = false)
                                                      Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false)
                                                          Integer pageSize,
                                                      @RequestParam(value = "sortBy", required = false) String sortBy,
                                                      @RequestParam(value = "sortDirection", required = false)
                                                          String sortDirection) {
        PageableResponseDTO<ProductSummaryResponseDTO> productSummaryPageableResponseDTO =
                productService.searchProducts(searchKeyword, pageNumber, pageSize, sortBy, sortDirection);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Searched products fetched successfully",
                productSummaryPageableResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/in-stock")
    public ResponseEntity<ResponseDTO> getInStockProducts(@RequestParam(value = "pageNumber", required = false)
                                                          Integer pageNumber,
                                                          @RequestParam(value = "pageSize", required = false)
                                                          Integer pageSize,
                                                          @RequestParam(value = "sortBy", required = false)
                                                              String sortBy,
                                                          @RequestParam(value = "sortDirection", required = false)
                                                              String sortDirection) {
        PageableResponseDTO<ProductSummaryResponseDTO> productSummaryPageableResponseDTO =
                productService.getInStockProductList(pageNumber, pageSize, sortBy, sortDirection);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "In-stock products fetched successfully",
                productSummaryPageableResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ResponseDTO> deleteProduct(@PathVariable Integer productId)
            throws IllegalArgumentException, ResourceNotFoundException {
        productService.deleteProduct(productId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Product deleted successfully");
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseDTO> getCategoryProducts(@PathVariable Integer categoryId,
                                                                   @RequestParam(value = "pageNumber",
                                                                           required = false) Integer pageNumber,
                                                                   @RequestParam(value = "pageSize",
                                                                           required = false) Integer pageSize) {
        PageableResponseDTO<ProductResponseDTO> productResponseDTOPageableResponseDTO =
                productService.getCategoryProductList(categoryId, pageNumber, pageSize);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Category product-list fetched successfully",
                productResponseDTOPageableResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
