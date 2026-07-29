package com.springProjects.onlineStore.order.repository;

import com.springProjects.onlineStore.order.constants.OrderStatus;
import com.springProjects.onlineStore.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderIdAndDeletedFalse(Integer orderId);

    Order findByOrderIdAndUser_UserIdAndDeletedFalse(Integer orderId, Integer userId);

    List<Order> findByUser_UserIdAndAddedOnBetweenAndDeletedFalse(Integer userId, LocalDateTime startDate,
                                                                    LocalDateTime endDate);

    Page<Order> findByUser_UserIdAndAddedOnBetweenAndDeletedFalse(Integer userId, LocalDateTime startTime,
                                                                    LocalDateTime endTime, Pageable pageable);

    List<Order> findByAddress_AddressIdAndUser_UserIdAndAddedOnBetweenAndDeletedFalse(Integer addressId, Integer userId,
                                                                                      LocalDateTime startDate,
                                                                                      LocalDateTime endDate);

    Page<Order> findByAddress_AddressIdAndUser_UserIdAndAddedOnBetweenAndDeletedFalse(Integer addressId, Integer userId,
                                                                                      LocalDateTime startDate,
                                                                                      LocalDateTime endDate,
                                                                                      Pageable pageable);

    List<Order> findByUser_UserIdAndDeletedFalse(Integer userId);
}
