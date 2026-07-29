package com.springProjects.onlineStore.order.entity;

import com.springProjects.onlineStore.common.entity.BaseEntity;
import com.springProjects.onlineStore.product.entity.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orderItems")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Product product;

    private Integer quantity;

    private BigDecimal totalAmount;

    private BigDecimal actualAmount;

    public OrderItem(Integer quantity, BigDecimal totalAmount, BigDecimal actualAmount) {
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.actualAmount = actualAmount;
    }
}
