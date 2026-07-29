package com.springProjects.onlineStore.cart.repository;

import com.springProjects.onlineStore.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Integer> {
    Cart findByUserIdAndDeletedFalse(Integer userId);

    Cart findByCartIdAndDeletedFalse(Integer cartId);
}
