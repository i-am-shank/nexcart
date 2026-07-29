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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponseDTO {
    private Integer orderId;

    private Integer userId;

    private AddressResponseDTO address;

    private BigDecimal totalPrice;

    private BigDecimal actualPrice;

    private OrderStatus orderStatus;

    private PaymentStatus paymentStatus;
}
