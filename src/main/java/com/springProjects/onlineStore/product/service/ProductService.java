package com.springProjects.onlineStore.product.service;

import com.springProjects.onlineStore.cart.entity.CartItem;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.product.dto.CreateProductDTO;
import com.springProjects.onlineStore.product.dto.ProductResponseDTO;
import com.springProjects.onlineStore.product.dto.ProductSummaryResponseDTO;
import com.springProjects.onlineStore.product.dto.UpdateProductDTO;
import com.springProjects.onlineStore.product.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService {
    /**
     * Create a products table entry
     * @param createProductDTO product creation provided values
     * @return - products db entry which is added
     */
    ProductResponseDTO createProduct(CreateProductDTO createProductDTO) throws ResourceNotFoundException;

    /**
     * Gets product details for provided productId
     * @param productId products db entry to look for
     * @return products db entry - in responseDTO
     * @throws IllegalArgumentException for invalid productId
     * @throws ResourceNotFoundException if no db entry found for provided productId
     */
    ProductResponseDTO getProductDetails(Integer productId) throws IllegalArgumentException, ResourceNotFoundException;

    /**
     * Gets product table db entry for provided productId
     * @param productId - products db entry to look for
     * @return fetched product if any
     * @throws IllegalArgumentException - for invalid productId
     * @throws ResourceNotFoundException - if no db entry found for provided productId
     */
    Product getProductByProductId(Integer productId)
            throws IllegalArgumentException, ResourceNotFoundException;

    /**
     * Updates product details for provided productId
     * @param productId products db entry to look for
     * @param updateProductDTO description, price, discountPercentage, remainingQuantity - updated values
     * @return updated products db entry - in responseDTO
     * @throws IllegalArgumentException for invalid productId
     * @throws ResourceNotFoundException if no db entry found for provided productId
     */
    ProductResponseDTO updateProduct(Integer productId, UpdateProductDTO updateProductDTO)
            throws IllegalArgumentException, ResourceNotFoundException;

    /**
     * Search products by a search-keyword
     * @param searchKeyword products containing this in title
     * @param pageNumber page no. in offset-based pagination
     * @param pageSize page size in offset-based pagination
     * @param sortBy products db column to sort paginated results with
     * @param sortDirection direction to sort results in - ASC / DESC
     * @return PageableResponseDTO - isFirstPage, isLastPage, pageSize, pageNumber, ..
     */
    PageableResponseDTO<ProductSummaryResponseDTO> searchProducts(String searchKeyword, Integer pageNumber,
                                                                  Integer pageSize, String sortBy,
                                                                  String sortDirection);

    /**
     * Fetches all products which are in-stock, as paginated result
     * @param pageNumber page no. in offset-based pagination
     * @param pageSize page size in offset-based pagination
     * @param sortBy products db column to sort paginated results with
     * @param sortDirection direction to sort results in - ASC / DESC
     * @return PageableResponseDTO - isFirstPage, isLastPage, pageSize, pageNumber, ..
     */
    PageableResponseDTO<ProductSummaryResponseDTO> getInStockProductList(Integer pageNumber, Integer pageSize,
                                                                         String sortBy, String sortDirection);

    /**
     * Marks products db entry as deleted for provided productId, also deletes product-image files
     * @param productId products db entry to look for
     * @throws IllegalArgumentException for invalid productId
     * @throws ResourceNotFoundException if no db entry found for provided productId
     */
    void deleteProduct(Integer productId) throws IllegalArgumentException, ResourceNotFoundException;

    PageableResponseDTO<ProductResponseDTO> getCategoryProductList(Integer categoryId, Integer pageNumber,
                                                                   Integer pageSize);

    List<ProductResponseDTO> getCategoryProductResponseDTOs(Integer categoryId);

    List<Product> retainProductQuantity(Map<Integer, Integer> productIdRetainQuantityMap)
            throws IllegalArgumentException, ResourceNotFoundException;

    /**
     * validate & update product quantity
     * @param updatedCartItemQuantity to be updated quantity of cartItem,
     *                 difference between current qty & this qty will be deducted from product remainingQuantity
     * @param cartItem corresponding cartItem to this product
     * @return updated product db entry
     * @throws UnsupportedOperationException for invalid product quantity requirements
     */
    Product updateProductRemainingQuantity(Integer updatedCartItemQuantity, CartItem cartItem)
            throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException;
}
