package com.springProjects.onlineStore.order.repository;

import com.springProjects.onlineStore.order.entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    OrderItem findByOrderItemIdAndDeletedFalse(Integer orderItemId);

    List<OrderItem> findByOrder_OrderIdAndDeletedFalse(Integer orderId);

    List<OrderItem> findByOrder_OrderIdInAndDeletedFalse(List<Integer> orderIds);

    Page<OrderItem> findByOrder_OrderIdInAndDeletedFalse(List<Integer> orderIds, Pageable pageable);
}
