package com.springProjects.onlineStore.product.repository;

import com.springProjects.onlineStore.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Product findByProductIdAndDeletedFalse(Integer productId);

    List<Product> findByProductIdInAndDeletedFalse(List<Integer> productIds);

    List<Product> findByInStockTrueAndDeletedFalse();

    Page<Product> findByInStockTrueAndDeletedFalse(Pageable pageable);

    List<Product> findByTitleContainingAndInStockTrueAndDeletedFalse(String searchedKeyword);

    Page<Product> findByTitleContainingAndInStockTrueAndDeletedFalse(String searchedKeyword, Pageable pageable);

    List<Product> findByCategory_CategoryIdAndDeletedFalse(Integer categoryId);

    Page<Product> findByCategory_CategoryIdAndDeletedFalse(Integer categoryId, Pageable pageable);
}
