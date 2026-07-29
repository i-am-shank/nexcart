package com.springProjects.onlineStore.order.dto;

import com.springProjects.onlineStore.order.constants.OrderStatus;
import com.springProjects.onlineStore.order.constants.PaymentStatus;
import com.springProjects.onlineStore.user.dto.AddressResponseDTO;
import com.springProjects.onlineStore.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Integer orderId;

    private Integer userId;

    private AddressResponseDTO address;

    private BigDecimal totalPrice;

    private BigDecimal actualPrice;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;

    private List<OrderItemResponseDTO> orderItems = new ArrayList<>();

    private LocalDateTime paymentDate;

    private LocalDateTime deliveryDate;

    private LocalDateTime refundDate;

    private LocalDateTime addedOn;

    private LocalDateTime updatedOn;
}
