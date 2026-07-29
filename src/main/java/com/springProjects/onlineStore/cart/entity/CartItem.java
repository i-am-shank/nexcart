package com.springProjects.onlineStore.cart.entity;

import com.springProjects.onlineStore.common.entity.BaseEntity;
import com.springProjects.onlineStore.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CartItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartItemId;

    // CartItem <-> Cart    :    Many-to-one
    // CartItem owns the relationship  :  class containing @JoinColumn
    // "cart_id" foreign-key added in CartItem table
    // FetchType.LAZY  :  required as for each CartItem, no need to fetch entire Cart data
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // CartItem <-> Product    :    Many-to-one
    // "product_id" foreign-key added to CartItem table
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    @Min(value = 0, message = "quantity cannot be negative")
    private Integer quantity;

    // amount & quantity won't be considered in Cart, of this CartItem
    private Boolean saveForLater = Boolean.FALSE;

    public CartItem(Cart cart, Product product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }
}
