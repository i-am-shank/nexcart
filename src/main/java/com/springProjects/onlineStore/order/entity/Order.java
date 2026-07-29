package com.springProjects.onlineStore.order.entity;

import com.springProjects.onlineStore.common.entity.BaseEntity;
import com.springProjects.onlineStore.order.constants.OrderStatus;
import com.springProjects.onlineStore.order.constants.PaymentStatus;
import com.springProjects.onlineStore.user.entity.Address;
import com.springProjects.onlineStore.user.entity.User;
import jakarta.persistence.*;
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
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    // Many to One mapping with User
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    // Many to One mapping with Address
    @ManyToOne(fetch = FetchType.EAGER)
    private Address address;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private BigDecimal actualPrice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    // Order <-> OrderItem  :  One-To-Many mapping
    @OneToMany(fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "order",
            orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime paymentDate;

    private LocalDateTime deliveryDate;

    private LocalDateTime refundDate;

    public Order(BigDecimal totalPrice, BigDecimal actualPrice, OrderStatus orderStatus, PaymentStatus paymentStatus) {
        this.totalPrice = totalPrice;
        this.actualPrice = actualPrice;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
    }
}
