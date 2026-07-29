package com.springProjects.onlineStore.order.service;

import com.springProjects.onlineStore.cart.dto.CartItemResponseDTO;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.order.constants.OrderPeriod;
import com.springProjects.onlineStore.order.dto.OrderItemResponseDTO;
import com.springProjects.onlineStore.order.entity.Order;
import com.springProjects.onlineStore.order.entity.OrderItem;

import java.util.List;

public interface OrderItemService {
    /**
     * Fetch OrderItem details
     * @param orderItemId OrderItem table P.K.
     * @return fetched db-entry as OrderItemResponseDTO
     * @throws IllegalArgumentException for invalid orderItemId
     * @throws ResourceNotFoundException if an active OrderItem-table entry not exists for provided orderItemId
     */
    OrderItemResponseDTO getOrderItemById(Integer orderItemId) throws IllegalArgumentException,
            ResourceNotFoundException;

    /**
     * Fetch OrderItem-list for orderId
     * @param orderId OrderItem-table F.K. , Order table P.K.
     * @return fetched OrderItem-list as List<OrderItemResponseDTO>
     * @throws IllegalArgumentException for invalid orderId
     * @throws ResourceNotFoundException if no active Order-table exists for provided orderId
     */
    List<OrderItemResponseDTO> getOrderItemsByOrderId(Integer orderId) throws IllegalArgumentException,
            ResourceNotFoundException;

    /**
     * Invoked while placing order (Order-table entry creation)
     * @param order Order-table entry, to map OrderItems to
     * @param cartItems For creating OrderItem - details
     * @return Created OrderItem-list
     * @throws IllegalArgumentException for null order, OR empty cartItems
     */
    List<OrderItem> createOrderItems(Order order, List<CartItemResponseDTO> cartItems) throws IllegalArgumentException;

    /**
     * Invoked while Cancel Order , doing inventory management
     * @param orderItems Corresponding Product remainingQty & inStock fields to be updated
     * @throws IllegalArgumentException for any invalid productId in any OrderItem
     * @throws ResourceNotFoundException for any non-existing Product entry corresponding to productId of OrderItem
     */
    void retainCancelledOrderItemsProductQuantity(List<OrderItem> orderItems) throws IllegalArgumentException,
            ResourceNotFoundException;

    /**
     * Fetch orderItems for user - paginated response
     * @param userId User-table P.K.
     * @param pageNumber for paginated response
     * @param pageSize for paginated response
     * @return OrderItems as OrderItemResponseDTO
     * @throws IllegalArgumentException for invalid userId
     * @throws ResourceNotFoundException if User-table entry not exists for userId
     */
    PageableResponseDTO<OrderItemResponseDTO> getOrderItemsForUser(Integer userId, Integer pageNumber,
                                                                   Integer pageSize) throws IllegalArgumentException,
            ResourceNotFoundException;
}
