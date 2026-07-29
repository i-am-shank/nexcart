package com.springProjects.onlineStore.order.service.impl;

import com.springProjects.onlineStore.cart.dto.CartPaymentDetailsDTO;
import com.springProjects.onlineStore.cart.service.CartService;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.order.constants.OrderPeriod;
import com.springProjects.onlineStore.order.constants.OrderStatus;
import com.springProjects.onlineStore.order.constants.PaymentStatus;
import com.springProjects.onlineStore.order.dto.OrderResponseDTO;
import com.springProjects.onlineStore.order.dto.OrderSummaryResponseDTO;
import com.springProjects.onlineStore.order.entity.Order;
import com.springProjects.onlineStore.order.entity.OrderItem;
import com.springProjects.onlineStore.order.mapper.OrderMapper;
import com.springProjects.onlineStore.order.repository.OrderRepository;
import com.springProjects.onlineStore.order.service.OrderItemService;
import com.springProjects.onlineStore.order.service.OrderService;
import com.springProjects.onlineStore.user.entity.Address;
import com.springProjects.onlineStore.user.entity.User;
import com.springProjects.onlineStore.user.repository.AddressRepository;
import com.springProjects.onlineStore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final CartService cartService;

    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final OrderItemService orderItemService;

    @Transactional
    @Override
    public OrderResponseDTO placeOrder(Integer userId, Integer addressId) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException {
        if(userId == null || addressId == null) {
            throw new IllegalArgumentException("userId & addressId are mandatory");
        }
        User user = userRepository.findByUserIdAndDeletedFalse(userId);
        Address address = addressRepository.findByAddressIdAndUser_UserIdAndDeletedFalse(addressId, userId);
        CartPaymentDetailsDTO cartPaymentDetailsDTO = cartService.getCartPaymentDetails(userId);
        validateUserAddressAndCartToPlaceOrder(user, userId, address, addressId, cartPaymentDetailsDTO);
        Order order = new Order(cartPaymentDetailsDTO.getTotalAmount(),
                cartPaymentDetailsDTO.getAmountToBePaid(), OrderStatus.PENDING,
                PaymentStatus.PENDING);
        order.setUser(user);
        order.setAddress(address);
        List<OrderItem> orderItems = orderItemService.createOrderItems(order, cartPaymentDetailsDTO.getCartItems());
        order.setOrderItems(orderItems);
        order = orderRepository.save(order);
        // clearing cart
        cartService.clearCart(userId, Boolean.FALSE);
        return orderMapper.toResponseDTO(order);
    }

    private void validateUserAddressAndCartToPlaceOrder(User user, Integer userId, Address address, Integer addressId,
                                                        CartPaymentDetailsDTO cartPaymentDetailsDTO)
            throws ResourceNotFoundException, UnsupportedOperationException {
        if(user == null) {
            throw new ResourceNotFoundException("User not found with id : " + userId);
        }
        if(address == null) {
            throw new ResourceNotFoundException("Address not found with id : " + addressId + " for userId : " + userId);
        }
        if(CollectionUtils.isEmpty(cartPaymentDetailsDTO.getCartItems())) {
            throw new UnsupportedOperationException("Cart is empty, please add items to place order");
        }
    }

    @Override
    public OrderResponseDTO updateOrderStatus(Integer orderId, OrderStatus orderStatus)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException {
        Order order = getOrderByOrderId(orderId);
        OrderStatus currentOrderStatus = order.getOrderStatus();
        if(isUpdatedOrderStatusAllowed(currentOrderStatus, orderStatus)) {
            // PAID    (PENDING -> DISPATCHED)
            if(OrderStatus.PENDING.equals(currentOrderStatus) && OrderStatus.DISPATCHED.equals(orderStatus)) {
                if(!PaymentStatus.PAID.equals(order.getPaymentStatus())) {
                    throw new IllegalStateException("Cannot DISPATCH order, if not PAID");
                }
            } else if(OrderStatus.PENDING.equals(currentOrderStatus) && OrderStatus.CANCELLED.equals(orderStatus)) {
                if(PaymentStatus.PAID.equals(order.getPaymentStatus())) {
                    order.setPaymentStatus(PaymentStatus.REFUND_IN_PROCESS);
                }
                // Restore inventory - of OrderItems of canceled order
                orderItemService.retainCancelledOrderItemsProductQuantity(order.getOrderItems());
            } else if(OrderStatus.DISPATCHED.equals(currentOrderStatus) && OrderStatus.DELIVERED.equals(orderStatus)) {
                // update deliveryDate
                order.setDeliveryDate(LocalDateTime.now());
            }
            // PAID    (DELIVERED -> RETURN_STARTED  :  10 days return policy  :  deliveryDate check)
            else if(OrderStatus.DELIVERED.equals(currentOrderStatus) && OrderStatus.RETURN_STARTED.equals(orderStatus)) {
                LocalDateTime deliveryDate = order.getDeliveryDate();
                if(deliveryDate == null) {
                    throw new IllegalStateException("Cannot RETURN order, as deliveryDate is missing");
                }
                if(deliveryDate.isBefore(LocalDateTime.now().minusDays(10))) {
                    throw new IllegalStateException("Cannot RETURN order, if delivered more than 10 days ago");
                }
            }
            // REFUNDED    (RETURN_STARTED -> RETURNED)
            else if(OrderStatus.RETURN_STARTED.equals(currentOrderStatus) && OrderStatus.RETURNED.equals(orderStatus)) {
                if(!PaymentStatus.REFUNDED.equals(order.getPaymentStatus())) {
                    throw new IllegalStateException("Cannot update order to RETURNED, if not REFUNDED");
                }
            }
            order.setOrderStatus(orderStatus);
            order = orderRepository.save(order);
        } else {
            throw new UnsupportedOperationException("Cannot update " + currentOrderStatus + " order to " + orderStatus);
        }
        return orderMapper.toResponseDTO(order);
    }

    private boolean isUpdatedOrderStatusAllowed(OrderStatus currentOrderStatus, OrderStatus updatedOrderStatus)
            throws IllegalArgumentException, UnsupportedOperationException {
        if(currentOrderStatus == null || updatedOrderStatus == null) {
            throw new IllegalArgumentException("current & updated order status are mandatory");
        }
        return currentOrderStatus.canTransitionTo(updatedOrderStatus);
    }

    @Override
    public OrderResponseDTO updateOrderPaymentStatus(Integer orderId, PaymentStatus paymentStatus)
            throws IllegalArgumentException, ResourceNotFoundException, UnsupportedOperationException,
            IllegalStateException {
        Order order = getOrderByOrderId(orderId);
        PaymentStatus currentPaymentStatus = order.getPaymentStatus();
        if(isUpdatedPaymentStatusAllowed(currentPaymentStatus, paymentStatus)) {
            // (PENDING -> PAID)  :  then  :  DISPATCHED
            // DELIVERED    (PAID)
            // RETURN_STARTED    (PAID -> REFUND_IN_PROGRESS -> REFUNDED)
            // RETURNED    (REFUNDED)
            if(PaymentStatus.PENDING.equals(currentPaymentStatus) && PaymentStatus.PAID.equals(paymentStatus)) {
                // update paymentDate
                order.setPaymentDate(LocalDateTime.now());
            } else if(PaymentStatus.PAID.equals(currentPaymentStatus) &&
                    PaymentStatus.REFUND_IN_PROCESS.equals(paymentStatus)) {
                if(!OrderStatus.RETURN_STARTED.equals(order.getOrderStatus())) {
                    throw new IllegalStateException("Cannot start REFUND, if order not in RETURN_STARTED status");
                }
            } else if(PaymentStatus.REFUND_IN_PROCESS.equals(currentPaymentStatus) &&
                    PaymentStatus.REFUNDED.equals(paymentStatus)) {
                if(!OrderStatus.RETURN_STARTED.equals(order.getOrderStatus()) &&
                        !OrderStatus.CANCELLED.equals(order.getOrderStatus())) {
                    throw new IllegalStateException(
                            "Cannot mark REFUNDED, if order not in RETURN_STARTED or CANCELLED status");
                }
                // update refundDate
                order.setRefundDate(LocalDateTime.now());
            }
            order.setPaymentStatus(paymentStatus);
            order = orderRepository.save(order);
        } else {
            throw new UnsupportedOperationException("Cannot update " + currentPaymentStatus +
                    " payment-status to " + paymentStatus);
        }
        return orderMapper.toResponseDTO(order);
    }

    private boolean isUpdatedPaymentStatusAllowed(PaymentStatus currentPaymentStatus, PaymentStatus updatedPaymentStatus)
            throws IllegalArgumentException, UnsupportedOperationException {
        if(currentPaymentStatus == null || updatedPaymentStatus == null) {
            throw new IllegalArgumentException("current & updated payment status are mandatory");
        }
        return currentPaymentStatus.canTransitionTo(updatedPaymentStatus);
    }

    @Override
    public OrderResponseDTO getOrderDetails(Integer orderId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException {
        Order order = getOrderByOrderIdAndUserId(orderId, userId);
        return orderMapper.toResponseDTO(order);
    }

    private Order getOrderByOrderId(Integer orderId) throws IllegalArgumentException, ResourceNotFoundException {
        if(orderId == null) {
            throw new IllegalArgumentException("orderId is null");
        }
        Order order = orderRepository.findByOrderIdAndDeletedFalse(orderId);
        if(order == null) {
            throw new ResourceNotFoundException("Order not found with id : " + orderId);
        }
        return order;
    }

    private Order getOrderByOrderIdAndUserId(Integer orderId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException {
        if(orderId == null || userId == null) {
            throw new IllegalArgumentException("orderId & userId are mandatory");
        }
        Order order = orderRepository.findByOrderIdAndUser_UserIdAndDeletedFalse(orderId, userId);
        if(order == null) {
            throw new ResourceNotFoundException("Order not found for id : " + orderId + " and userId : " + userId);
        }
        return order;
    }

    @Override
    public PageableResponseDTO<OrderSummaryResponseDTO> getOrdersForUser(Integer userId, Integer pageNumber,
                                                                         Integer pageSize, OrderPeriod orderPeriod)
            throws IllegalArgumentException {
        if(userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDate(orderPeriod, endDate);
        if(pageNumber == null || pageSize == null) {
            return getAllOrdersForUserInTimePeriod(userId, startDate, endDate);
        }
        return getOrdersForUserHelper(userId, pageNumber, pageSize, startDate, endDate);
    }

    private PageableResponseDTO<OrderSummaryResponseDTO> getAllOrdersForUserInTimePeriod(Integer userId,
                                                                                         LocalDateTime startDate,
                                                                                         LocalDateTime endDate) {
        List<Order> orderList = orderRepository.findByUser_UserIdAndAddedOnBetweenAndDeletedFalse(userId,
                startDate, endDate);
        List<OrderSummaryResponseDTO> orderSummaryResponseDTOList = orderList.stream()
                .map(orderMapper::toSummaryResponseDTO)
                .toList();
        return new PageableResponseDTO<>(orderSummaryResponseDTOList, Boolean.TRUE,
                Boolean.TRUE,0, orderList.size(), (long) orderList.size(), 1);
    }

    private PageableResponseDTO<OrderSummaryResponseDTO> getOrdersForUserHelper(Integer userId, Integer pageNumber,
                                                                                Integer pageSize, LocalDateTime startDate,
                                                                                LocalDateTime endDate)
            throws IllegalArgumentException {
        if(pageNumber < 0) {
            throw new IllegalArgumentException("pageNumber cannot be negative");
        }
        if(pageSize <= 0) {
            throw new IllegalArgumentException("pageSize should be positive");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orderPage = orderRepository.findByUser_UserIdAndAddedOnBetweenAndDeletedFalse(userId,
                startDate, endDate, pageable);
        List<OrderSummaryResponseDTO> orderSummaryResponseDTOList = orderPage.stream()
                .map(orderMapper::toSummaryResponseDTO)
                .toList();
        return new PageableResponseDTO<>(orderSummaryResponseDTOList, orderPage.isFirst(), orderPage.isLast(),
                orderPage.getNumber(), orderPage.getSize(), orderPage.getTotalElements(), orderPage.getTotalPages());
    }

    private LocalDateTime getStartDate(OrderPeriod orderPeriod, LocalDateTime endDate)
            throws IllegalArgumentException {
        if(orderPeriod == null) {
            // Will return all records
            return LocalDateTime.MIN;
        }
        LocalDateTime startDate;
        switch(orderPeriod) {
            case LAST_1_WEEK -> startDate = endDate.minusWeeks(1);
            case LAST_30_DAYS -> startDate = endDate.minusDays(30);
            case LAST_3_MONTHS -> startDate = endDate.minusMonths(3);
            case LAST_1_YEAR -> startDate = endDate.minusYears(1);
            default -> throw new IllegalArgumentException("Invalid order period provided");
        }
        return startDate;
    }

    @Override
    public PageableResponseDTO<OrderSummaryResponseDTO> getOrdersForAddress(Integer addressId, Integer userId,
                                                                            Integer pageNumber, Integer pageSize,
                                                                            OrderPeriod orderPeriod)
            throws IllegalArgumentException {
        if(addressId == null || userId == null) {
            throw new IllegalArgumentException("addressId & userId are mandatory");
        }
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = getStartDate(orderPeriod, endDate);
        if(pageNumber == null || pageSize == null) {
            return getAllOrdersForAddressInTimePeriod(addressId, userId, startDate, endDate);
        }
        return getOrdersForAddressHelper(addressId, userId, pageNumber, pageSize, startDate, endDate);
    }

    private PageableResponseDTO<OrderSummaryResponseDTO> getAllOrdersForAddressInTimePeriod(Integer addressId,
                                                                                            Integer userId,
                                                                                            LocalDateTime startDate,
                                                                                            LocalDateTime endDate) {
        List<Order> orderList = orderRepository.findByAddress_AddressIdAndUser_UserIdAndAddedOnBetweenAndDeletedFalse(
                addressId, userId, startDate, endDate);
        List<OrderSummaryResponseDTO> orderSummaryResponseDTOList = orderList.stream()
                .map(orderMapper::toSummaryResponseDTO)
                .toList();
        return new PageableResponseDTO<>(orderSummaryResponseDTOList, Boolean.TRUE, Boolean.TRUE, 0,
                orderList.size(), (long) orderList.size(), 1);
    }

    private PageableResponseDTO<OrderSummaryResponseDTO> getOrdersForAddressHelper(Integer addressId, Integer userId,
                                                                                   Integer pageNumber, Integer pageSize,
                                                                                   LocalDateTime startDate,
                                                                                   LocalDateTime endDate)
            throws IllegalArgumentException {
        if(pageNumber < 0) {
            throw new IllegalArgumentException("pageNumber cannot be negative");
        }
        if(pageSize <= 0) {
            throw new IllegalArgumentException("pageSize should be positive");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Order> orderPage = orderRepository.findByAddress_AddressIdAndUser_UserIdAndAddedOnBetweenAndDeletedFalse(
                addressId, userId, startDate, endDate, pageable);
        List<OrderSummaryResponseDTO> orderSummaryResponseDTOList = orderPage.stream()
                .map(orderMapper::toSummaryResponseDTO)
                .toList();
        return new PageableResponseDTO<>(orderSummaryResponseDTOList, orderPage.isFirst(), orderPage.isLast(),
                orderPage.getNumber(), orderPage.getSize(), orderPage.getTotalElements(), orderPage.getTotalPages());
    }

    @Transactional
    @Override
    public OrderResponseDTO cancelOrder(Integer orderId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException {
        Order order = getOrderByOrderIdAndUserId(orderId, userId);
        if(!OrderStatus.PENDING.equals(order.getOrderStatus())) {
            throw new UnsupportedOperationException(
                    "Cannot process cancel request, already started processing or delivered order");
        }
        // retain all the Product quantity - of OrderItems of canceled order
        orderItemService.retainCancelledOrderItemsProductQuantity(order.getOrderItems());
        // update order status
        order.setOrderStatus(OrderStatus.CANCELLED);
        // REFUND only if PAID. Not for PENDING paymentStatus
        if(PaymentStatus.PAID.equals(order.getPaymentStatus())) {
            // update order payment status
            order.setPaymentStatus(PaymentStatus.REFUND_IN_PROCESS);
        }

        order = orderRepository.save(order);
        return orderMapper.toResponseDTO(order);
    }
}
