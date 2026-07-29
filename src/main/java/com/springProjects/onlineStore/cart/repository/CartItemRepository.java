package com.springProjects.onlineStore.cart.repository;

import com.springProjects.onlineStore.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
    List<CartItem> findByCart_CartIdAndDeletedFalse(Integer cartId);

    CartItem findByCartItemIdAndDeletedFalse(Integer cartItemId);

    boolean existsByCart_CartIdAndProduct_ProductIdAndDeletedFalse(Integer cartId, Integer productId);
}
