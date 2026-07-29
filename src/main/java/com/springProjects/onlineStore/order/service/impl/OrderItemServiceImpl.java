package com.springProjects.onlineStore.order.service.impl;

import com.springProjects.onlineStore.cart.dto.CartItemResponseDTO;
import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.utils.CommonUtils;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.order.dto.OrderItemResponseDTO;
import com.springProjects.onlineStore.order.entity.Order;
import com.springProjects.onlineStore.order.entity.OrderItem;
import com.springProjects.onlineStore.order.mapper.OrderItemMapper;
import com.springProjects.onlineStore.order.repository.OrderItemRepository;
import com.springProjects.onlineStore.order.repository.OrderRepository;
import com.springProjects.onlineStore.order.service.OrderItemService;
import com.springProjects.onlineStore.product.entity.Product;
import com.springProjects.onlineStore.product.service.ProductService;
import com.springProjects.onlineStore.user.entity.User;
import com.springProjects.onlineStore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;

    private final OrderItemMapper orderItemMapper;

    private final ProductService productService;

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    @Override
    public OrderItemResponseDTO getOrderItemById(Integer orderItemId) throws IllegalArgumentException,
            ResourceNotFoundException {
        if(orderItemId == null) {
            throw new IllegalArgumentException("orderItemId is null");
        }
        OrderItem orderItem = orderItemRepository.findByOrderItemIdAndDeletedFalse(orderItemId);
        if(orderItem == null) {
            throw new ResourceNotFoundException("orderItem not found for id : " + orderItemId);
        }
        return orderItemMapper.toResponseDTO(orderItem);
    }

    @Override
    public List<OrderItemResponseDTO> getOrderItemsByOrderId(Integer orderId) throws IllegalArgumentException,
            ResourceNotFoundException {
        if(orderId == null) {
            throw new IllegalArgumentException("orderId is null");
        }
        Order order = orderRepository.findByOrderIdAndDeletedFalse(orderId);
        if(order == null) {
            throw new ResourceNotFoundException("order not found for id : " + orderId);
        }
        List<OrderItem> orderItems = orderItemRepository.findByOrder_OrderIdAndDeletedFalse(orderId);
        if(CollectionUtils.isEmpty(orderItems)) {
            return new ArrayList<>();
        }
        return orderItems.stream()
                .map(orderItemMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    @Override
    public List<OrderItem> createOrderItems(Order order, List<CartItemResponseDTO> cartItems)
            throws IllegalArgumentException {
        if(order == null) {
            throw new IllegalArgumentException("order is null");
        }
        if(CollectionUtils.isEmpty(cartItems)) {
            throw new IllegalArgumentException("cartItems list is empty");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItemResponseDTO cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if(product != null) {
                Integer quantity = cartItem.getQuantity();
                BigDecimal totalAmount = (CommonUtils.getPrecisionFixedValueOrZero(product.getPrice())
                        .multiply(BigDecimal.valueOf(quantity)));
                BigDecimal actualAmount = CommonUtils.getDiscountedAmount(totalAmount, product.getDiscountPercentage());
                OrderItem orderItem = new OrderItem(quantity, CommonUtils.getPrecisionFixedValueOrZero(totalAmount),
                        CommonUtils.getPrecisionFixedValueOrZero(actualAmount));
                orderItem.setProduct(product);
                orderItem.setOrder(order);
                orderItems.add(orderItem);
            }
        }
        return orderItems;
    }

    @Override
    public void retainCancelledOrderItemsProductQuantity(List<OrderItem> orderItems) throws IllegalArgumentException,
            ResourceNotFoundException {
        Map<Integer, Integer> productIdRetainedQuantityMap = new HashMap<>();
        for(OrderItem orderItem : orderItems) {
            Integer productId = orderItem.getProduct().getProductId();
            Integer quantity = orderItem.getQuantity();
            productIdRetainedQuantityMap.put(productId, quantity);
        }
        List<Product> updatedProductList = productService.retainProductQuantity(productIdRetainedQuantityMap);
        // Update mapping in OrderItem
        Map<Integer, Product> updatedProductMap = updatedProductList.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));
        for(OrderItem orderItem : orderItems) {
            Integer productId = orderItem.getProduct().getProductId();
            orderItem.setProduct(updatedProductMap.get(productId));
        }
        orderItemRepository.saveAll(orderItems);
    }

    @Override
    public PageableResponseDTO<OrderItemResponseDTO> getOrderItemsForUser(Integer userId, Integer pageNumber,
                                                                          Integer pageSize)
            throws IllegalArgumentException, ResourceNotFoundException {
        validateUserIdAndUser(userId);
        // Fetch list of orders for userId
        List<Order> userOrders = orderRepository.findByUser_UserIdAndDeletedFalse(userId);
        List<Integer> orderIds = userOrders.stream()
                .map(Order::getOrderId)
                .collect(Collectors.toList());
        // Fetch list of orderItems for those order
        if(pageNumber == null || pageSize == null) {
            return getAllOrderItemsForOrderIds(orderIds);
        } else {
            return getOrderItemsPageForOrderIds(orderIds, pageNumber, pageSize);
        }
    }

    private void validateUserIdAndUser(Integer userId) throws IllegalArgumentException, ResourceNotFoundException {
        if(userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        User user = userRepository.findByUserIdAndDeletedFalse(userId);
        if(user == null) {
            throw new ResourceNotFoundException("user not found for id : " + userId);
        }
    }

    private PageableResponseDTO<OrderItemResponseDTO> getAllOrderItemsForOrderIds(List<Integer> orderIds) {
        List<OrderItem> orderItemList = orderItemRepository.findByOrder_OrderIdInAndDeletedFalse(orderIds);
        List<OrderItemResponseDTO> orderItemResponseDTOS = orderItemList.stream()
                .map(orderItemMapper::toResponseDTO)
                .toList();
        return new PageableResponseDTO<>(orderItemResponseDTOS, Boolean.TRUE, Boolean.TRUE, 0,
                orderItemList.size(), (long)orderItemList.size(), 1);
    }

    private PageableResponseDTO<OrderItemResponseDTO> getOrderItemsPageForOrderIds(List<Integer> orderIds,
                                                                                   Integer pageNumber, Integer pageSize)
            throws IllegalArgumentException {
        if(pageNumber < 0) {
            throw new IllegalArgumentException("pageNumber cannot be negative");
        }
        if(pageSize < 1) {
            throw new IllegalArgumentException("pageSize cannot be non-positive");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OrderItem> orderItemPage = orderItemRepository.findByOrder_OrderIdInAndDeletedFalse(orderIds, pageable);
        List<OrderItemResponseDTO> orderItemResponseDTOS = orderItemPage.stream()
                .map(orderItemMapper::toResponseDTO)
                .toList();
        return new PageableResponseDTO<>(orderItemResponseDTOS, orderItemPage.isFirst(), orderItemPage.isLast(),
                orderItemPage.getNumber(), orderItemPage.getSize(), orderItemPage.getTotalElements(),
                orderItemPage.getTotalPages());
    }
}
