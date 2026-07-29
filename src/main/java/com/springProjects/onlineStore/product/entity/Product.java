package com.springProjects.onlineStore.product.entity;

import com.springProjects.onlineStore.category.entity.Category;
import com.springProjects.onlineStore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(scale = 2)
    private BigDecimal price;

    @Column(scale = 2)
    private BigDecimal discountPercentage;

    private Integer remainingQuantity;

    private Boolean inStock;

    // Product  <->  Category mapping    :    Many-to-One mapping (owner Product - as it is added later)
    // @JoinColumn  :  f.k. added in Product table
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Product(String title, String description, BigDecimal price, BigDecimal discountPercentage,
                   Integer remainingQuantity, Boolean inStock, Category category) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.discountPercentage = discountPercentage;
        this.remainingQuantity = remainingQuantity;
        this.inStock = inStock;
        this.category = category;
    }
}
