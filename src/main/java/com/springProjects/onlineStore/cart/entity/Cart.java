package com.springProjects.onlineStore.cart.entity;

import com.springProjects.onlineStore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartId;

    @Column(unique = true, nullable = false)
    private Integer userId;

    // Non-deleted Cart-items
    private Integer totalItems = 0;

    // Non-deleted Cart-items
    @Column(scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    // Non-deleted & Not savedForLater Cart-items
    @Column(scale = 2)
    private BigDecimal amountToBePaid = BigDecimal.ZERO;

    // mappedBy = "cart"  :  member variable of CartItem
    // This is supporting side of Bi-directional mapping
    // orphanRemoval = true  :  If a CartItem removed from Cart -> cartItems, Hibernate automatically delete it from DB
    @OneToMany(mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<CartItem> cartItems = new ArrayList<>();
}
