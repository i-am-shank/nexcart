package com.springProjects.onlineStore.product.service.impl;

import com.springProjects.onlineStore.cart.entity.CartItem;
import com.springProjects.onlineStore.category.entity.Category;
import com.springProjects.onlineStore.category.mapper.CategoryMapper;
import com.springProjects.onlineStore.category.repository.CategoryRepository;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.utils.CommonUtils;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.file.constants.FileType;
import com.springProjects.onlineStore.file.service.FileService;
import com.springProjects.onlineStore.product.constants.ProductConstants;
import com.springProjects.onlineStore.product.dto.CreateProductDTO;
import com.springProjects.onlineStore.product.dto.ProductResponseDTO;
import com.springProjects.onlineStore.product.dto.ProductSummaryResponseDTO;
import com.springProjects.onlineStore.product.dto.UpdateProductDTO;
import com.springProjects.onlineStore.product.entity.Product;
import com.springProjects.onlineStore.product.mapper.ProductMapper;
import com.springProjects.onlineStore.product.repository.ProductRepository;
import com.springProjects.onlineStore.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final FileService fileService;

    private final CategoryRepository categoryRepository;

    private final CategoryMapper categoryMapper;

    @Override
    public ProductResponseDTO createProduct(CreateProductDTO createProductDTO) throws ResourceNotFoundException {
        Product product = productMapper.toEntity(createProductDTO);
        product.setPrice(CommonUtils.getPrecisionFixedValueOrZero(product.getPrice()));
        product.setDiscountPercentage(CommonUtils.getPrecisionFixedValueOrZero(product.getDiscountPercentage()));
        if(createProductDTO.getCategoryId() != null) {
            Category category = categoryRepository.findByCategoryIdAndDeletedFalse(createProductDTO.getCategoryId());
            if(category == null) {
                throw new ResourceNotFoundException("Category not found with id : " + createProductDTO.getCategoryId());
            }
            product.setCategory(category);
        }
        product.setInStock((product.getRemainingQuantity() != null) &&
                (product.getRemainingQuantity() > ProductConstants.IN_STOCK_QTY));
        productRepository.save(product);
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO getProductDetails(Integer productId) throws IllegalArgumentException,
            ResourceNotFoundException {
        Product product = getProductByProductId(productId);
        ProductResponseDTO productResponseDTO = productMapper.toResponseDTO(product);
        productResponseDTO.setFiles(fileService.getProductImagesData(productId));
        Category category = product.getCategory();
        if(category != null) {
            productResponseDTO.setCategoryResponseDTO(categoryMapper.toResponseDTO(category));
        }
        return productResponseDTO;
    }

    @Override
    public Product getProductByProductId(Integer productId)
            throws IllegalArgumentException, ResourceNotFoundException {
        if(productId == null) {
            throw new IllegalArgumentException("productId is empty");
        }
        Product product = productRepository.findByProductIdAndDeletedFalse(productId);
        if(product == null) {
            throw new ResourceNotFoundException("Product not found with id : " + productId);
        }
        return product;
    }

    @Override
    public ProductResponseDTO updateProduct(Integer productId, UpdateProductDTO updateProductDTO)
            throws IllegalArgumentException, ResourceNotFoundException {
        Product product = getProductByProductId(productId);
        if(StringUtils.hasLength(updateProductDTO.getTitle())) {
            product.setTitle(updateProductDTO.getTitle());
        }
        if(StringUtils.hasLength(updateProductDTO.getDescription())) {
            product.setDescription(updateProductDTO.getDescription());
        }
        if(updateProductDTO.getPrice() != null) {
            product.setPrice(CommonUtils.getPrecisionFixedValueOrZero(updateProductDTO.getPrice()));
        }
        if(updateProductDTO.getDiscountPercentage() != null) {
            product.setDiscountPercentage(
                    CommonUtils.getPrecisionFixedValueOrZero(updateProductDTO.getDiscountPercentage()));
        }
        if(updateProductDTO.getRemainingQuantity() != null) {
            product.setRemainingQuantity(updateProductDTO.getRemainingQuantity());
            product.setInStock((product.getRemainingQuantity() != null) &&
                    (product.getRemainingQuantity() > ProductConstants.IN_STOCK_QTY));
        }
        if(updateProductDTO.getCategoryId() != null) {
            Category category = categoryRepository.findByCategoryIdAndDeletedFalse(updateProductDTO.getCategoryId());
            if(category == null) {
                throw new ResourceNotFoundException("Category not found with id : " + updateProductDTO.getCategoryId());
            }
            product.setCategory(category);
        }
        productRepository.save(product);
        ProductResponseDTO productResponseDTO = productMapper.toResponseDTO(product);
        productResponseDTO.setFiles(fileService.getProductImagesData(productId));
        return productResponseDTO;
    }

    @Override
    public PageableResponseDTO<ProductSummaryResponseDTO> searchProducts(String searchKeyword, Integer pageNumber,
                                                                         Integer pageSize, String sortBy,
                                                                         String sortDirection) {
        if(StringUtils.hasLength(searchKeyword)) {
            return getSearchedInStockProductList(searchKeyword, pageNumber, pageSize, sortBy, sortDirection);
        } else {
            return getInStockProductList(pageNumber, pageSize, sortBy, sortDirection);
        }
    }

    private PageableResponseDTO<ProductSummaryResponseDTO> getSearchedInStockProductList(String searchKeyword,
                                                                                         Integer pageNumber,
                                                                                         Integer pageSize,
                                                                                         String sortBy,
                                                                                         String sortDirection) {
        if(pageNumber!=null && pageSize!=null) {
            Sort sort = getDefaultSort(sortBy, sortDirection);
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<Product> products = productRepository.findByTitleContainingAndInStockTrueAndDeletedFalse(
                    searchKeyword, pageable);
            List<ProductSummaryResponseDTO> productSummaryResponseDTOS = getProductSummaryResponseDTOS(
                    products.stream().toList());
            return new PageableResponseDTO<>(productSummaryResponseDTOS, products.isFirst(), products.isLast(),
                    products.getNumber(), products.getSize(), products.getTotalElements(), products.getTotalPages());
        } else {
            List<Product> products = productRepository.findByTitleContainingAndInStockTrueAndDeletedFalse(
                    searchKeyword);
            List<ProductSummaryResponseDTO> productSummaryResponseDTOS = getProductSummaryResponseDTOS(products);
            return new PageableResponseDTO<>(productSummaryResponseDTOS, Boolean.TRUE, Boolean.TRUE,
                    0, products.size(), (long) products.size(), 1);
        }
    }

    @Override
    public PageableResponseDTO<ProductSummaryResponseDTO> getInStockProductList(Integer pageNumber, Integer pageSize,
                                                                                String sortBy, String sortDirection) {
        if(pageNumber!=null && pageSize!=null) {
            Sort sort = getDefaultSort(sortBy, sortDirection);
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<Product> products = productRepository.findByInStockTrueAndDeletedFalse(pageable);
            List<ProductSummaryResponseDTO> productSummaryResponseDTOS =
                    getProductSummaryResponseDTOS(products.stream().toList());
            return new PageableResponseDTO<>(productSummaryResponseDTOS, products.isFirst(), products.isLast(),
                    products.getNumber(), products.getSize(), products.getTotalElements(), products.getTotalPages());
        } else {
            List<Product> products = productRepository.findByInStockTrueAndDeletedFalse();
            List<ProductSummaryResponseDTO> productSummaryResponseDTOS = getProductSummaryResponseDTOS(products);
            return new PageableResponseDTO<>(productSummaryResponseDTOS, Boolean.TRUE, Boolean.TRUE,
                    0, products.size(), (long) products.size(), 1);
        }
    }

    private Sort getDefaultSort(String sortBy, String sortDirection) {
        String sortColumn = isValidProductSortField(sortBy) ? sortBy : "title";
        // Default sort-direction : ASC
        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortColumn);
    }

    /**
     * Reflection based validation
     * @param sortBy - Product entity db column to sort results by
     * @return - if sortBy is a valid Product entity db column name
     */
    private Boolean isValidProductSortField(String sortBy) {
        if(StringUtils.hasLength(sortBy)) {
            return Arrays.stream(Product.class.getDeclaredFields())
                    .anyMatch(field -> field.getName().equals(sortBy));
        }
        return false;
    }

    private List<ProductSummaryResponseDTO> getProductSummaryResponseDTOS(List<Product> products) {
        if(CollectionUtils.isEmpty(products)) {
            return new ArrayList<>();
        }
        return products.stream()
                .map(productMapper::toSummaryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProduct(Integer productId) throws IllegalArgumentException, ResourceNotFoundException {
        Product product = getProductByProductId(productId);
        product.setDeleted(Boolean.TRUE);
        productRepository.save(product);
        // Also delete product-images
        fileService.deleteEntityImages(productId, FileType.PRODUCT_IMAGE);
    }

    @Override
    public PageableResponseDTO<ProductResponseDTO> getCategoryProductList(Integer categoryId,
                                                                          Integer pageNumber, Integer pageSize) {
        if(pageNumber!=null && pageSize!=null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Product> productPage = productRepository.findByCategory_CategoryIdAndDeletedFalse(
                    categoryId, pageable);
            List<ProductResponseDTO> productResponseDTOS = productPage.stream()
                    .map(productMapper::toResponseDTO)
                    .collect(Collectors.toList());
            return new PageableResponseDTO<>(productResponseDTOS, productPage.isFirst(), productPage.isLast(),
                    productPage.getNumber(),  productPage.getSize(), productPage.getTotalElements(),
                    productPage.getTotalPages());
        } else {
            List<ProductResponseDTO> categoryProductResponseDTOs = getCategoryProductResponseDTOs(categoryId);
            return new PageableResponseDTO<>(categoryProductResponseDTOs, Boolean.TRUE, Boolean.TRUE, 1,
                    categoryProductResponseDTOs.size(), (long) categoryProductResponseDTOs.size(), 1);
        }
    }

    @Override
    public List<ProductResponseDTO> getCategoryProductResponseDTOs(Integer categoryId) {
        List<Product> productList = productRepository.findByCategory_CategoryIdAndDeletedFalse(categoryId);
        return productList.stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // TODO : Have to handle concurrent calls to this method. Ignoring for now.
    //  Suppose a product has qty 10. And 2 users at same time cancelled their order of 2 & 3 qty respectively
    //  ->  both will read current product-qty as 10
    //  ->  1st user will update it to 12 & 2nd to 13 respectively. But ideally it should be 15 finally.
    @Transactional
    @Override
    public List<Product> retainProductQuantity(Map<Integer, Integer> productIdRetainQuantityMap)
            throws IllegalArgumentException, ResourceNotFoundException {
        // validation
        boolean isAnyNullQtyInMap = productIdRetainQuantityMap.values().stream()
                .anyMatch(Objects::isNull);
        if(isAnyNullQtyInMap) {
            throw new IllegalArgumentException("To be retained quantity cannot be null");
        }
        boolean isAnyNegativeRetainedQty = productIdRetainQuantityMap.values().stream()
                .anyMatch(quantity -> quantity < 0);
        if(isAnyNegativeRetainedQty) {
            throw new IllegalArgumentException("To be retained quantity cannot be negative");
        }
        // prepare update data
        List<Integer> productIds = productIdRetainQuantityMap.keySet().stream().toList();
        List<Product> productList = productRepository.findByProductIdInAndDeletedFalse(productIds);
        Map<Integer, Product> productIdProductMap = productList.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        List<Product> updatedProductList = new ArrayList<>();
        // iterate on products
        for(Integer productId : productIds) {
            Product product = productIdProductMap.get(productId);
            if(product == null) {
                throw new ResourceNotFoundException("Product not found for id : " + productId);
            }
            Integer currentQuantity = CommonUtils.getValueOrZero(product.getRemainingQuantity());
            product.setRemainingQuantity(currentQuantity + productIdRetainQuantityMap.get(productId));
            product.setInStock((product.getRemainingQuantity() != null) &&
                    (product.getRemainingQuantity() > ProductConstants.IN_STOCK_QTY));
            updatedProductList.add(product);
        }
        return productRepository.saveAll(updatedProductList);
    }

    // TODO : Need to handle concurrent product buys to avoid overselling
    //          Multiple threads might read same product-quantity & can trigger valid updates on their thread
    @Override
    public Product updateProductRemainingQuantity(Integer updatedCartItemQuantity, CartItem cartItem)
            throws IllegalArgumentException, IllegalStateException, UnsupportedOperationException {
        if(updatedCartItemQuantity == null || updatedCartItemQuantity < 0) {
            throw new IllegalArgumentException("To be updated CartItem quantity cannot be null or negative");
        }
        Product product = cartItem.getProduct();
        if(product == null) {
            throw new IllegalStateException("product is null, for provided cartItem");
        }
        Integer remainingQty = CommonUtils.getValueOrZero(product.getRemainingQuantity());
        int extraRequestedQuantity = updatedCartItemQuantity - CommonUtils.getValueOrZero(cartItem.getQuantity());
        if((remainingQty - ProductConstants.IN_STOCK_QTY) < extraRequestedQuantity) {
            throw new UnsupportedOperationException("Requested quantity exceeds the stock quantity");
        }
        // update Product remainingQty
        product.setRemainingQuantity(remainingQty - extraRequestedQuantity);
        productRepository.save(product);
        return product;
    }
}
