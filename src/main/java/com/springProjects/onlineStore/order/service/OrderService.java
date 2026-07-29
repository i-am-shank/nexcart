package com.springProjects.onlineStore.order.service;

import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.order.constants.OrderPeriod;
import com.springProjects.onlineStore.order.constants.OrderStatus;
import com.springProjects.onlineStore.order.constants.PaymentStatus;
import com.springProjects.onlineStore.order.dto.OrderResponseDTO;
import com.springProjects.onlineStore.order.dto.OrderSummaryResponseDTO;

public interface OrderService {
    /**
     * Get the cart of user with userId (unique cart), create order out of those cartItem & cart - table entry
     * -----------------
     * Clear the cart after placing order (Transaction)
     * @param userId user table P.K.
     * @param addressId order delivery address
     * @return created order table entry as OrderResponseDTO
     * @throws IllegalArgumentException invalid userId or addressId
     * @throws ResourceNotFoundException if cart not found for (userId), or address not found for (addressId, userId)
     * @throws UnsupportedOperationException if cart is empty - 0 cartItems
     */
    OrderResponseDTO placeOrder(Integer userId, Integer addressId) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException;

    /**
     * Update order-status of the order
     * @param orderId order whose status to be updated
     * - not using userId here, as Order-status update is done by admin / automated (not by user who ordered)
     * @param orderStatus updated order-status
     * @return updated order as OrderResponseDTO
     * @throws IllegalArgumentException fur invalid orderId
     * @throws ResourceNotFoundException if order not found for provided (orderId)
     * @throws UnsupportedOperationException if invalid status-update is triggered
     * - updates should always proceed forward in hierarchy
     */
    OrderResponseDTO updateOrderStatus(Integer orderId, OrderStatus orderStatus) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException, IllegalStateException;

    /**
     * Update payment-status , if provided
     * @param orderId order table P.K.
     * not using userId here, as Order-status update is done by admin / automated (not by user who ordered)
     * @param paymentStatus updated payment-status
     * @return updated order as OrderResponseDTO
     * @throws IllegalArgumentException fur invalid orderId, or if paymentDone not provided
     * @throws ResourceNotFoundException if order not found for (orderId)
     * @throws UnsupportedOperationException if invalid paymentDone update is being triggered
     *      - should happen like a progress in order (never backward in hierarchy)
     */
    OrderResponseDTO updateOrderPaymentStatus(Integer orderId, PaymentStatus paymentStatus)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException;

    /**
     * Get order-details of a single order
     * @param orderId order whose details to be fetched
     * @param userId user-order validation, shouldn't allow fetching any other user's order
     * @return fetched order as OrderResponseDTO
     * @throws IllegalArgumentException fur invalid orderId or userId
     * @throws ResourceNotFoundException if order not found for provided (orderId, userId)
     */
    OrderResponseDTO getOrderDetails(Integer orderId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException;

    /**
     * Fetch orders of the user by userId
     * Allowed values for period : "last 30 days", "last 3 months", "last 1 year" OR empty (overall period)
     * @param userId user table P.K.
     * @param pageNumber fur pagination
     * @param pageSize fur pagination
     * @param orderPeriod time period enum for addedOn column of order table
     * @return paginated list of orders
     * @throws IllegalArgumentException for invalid userId
     */
    PageableResponseDTO<OrderSummaryResponseDTO> getOrdersForUser(Integer userId, Integer pageNumber,
                                                                  Integer pageSize, OrderPeriod orderPeriod)
            throws IllegalArgumentException;

    /**
     * Fetch orders for one of user's address, by addressId
     * ----------------
     * Allowed values for period : "last 30 days", "last 3 months", "last 1 year" OR empty (overall period)
     * @param addressId address table P.K.
     * @param userId to ensure a user shouldn't fetch another user's order
     * @param pageNumber fur pagination
     * @param pageSize fur pagination
     * @param orderPeriod time period enum for addedOn column of order table
     * @return paginated list of orders
     * @throws IllegalArgumentException fur invalid addressId or userId
     */
    PageableResponseDTO<OrderSummaryResponseDTO> getOrdersForAddress(Integer addressId, Integer userId,
                                                                     Integer pageNumber, Integer pageSize,
                                                                     OrderPeriod orderPeriod)
            throws IllegalArgumentException;

    /**
     * While fetching order, always fetch using (orderId, userId) both
     * --------------
     * Should only be allowed if Order is in "Pending" status
     * --------------
     * Product quantity should be retained, for each of the OrderItem
     * --------------
     * Update Order status to "Cancelled"
     * @param orderId order table P.K.
     * @param userId to ensure a user shouldn't fetch another user's order
     * @return updated order as OrderResponseDTO
     * @throws IllegalArgumentException for invalid orderId, userId
     * @throws ResourceNotFoundException if order not found for provided (orderId, userId)
     * @throws UnsupportedOperationException for invalid Order status
     */
    OrderResponseDTO cancelOrder(Integer orderId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException;
}
