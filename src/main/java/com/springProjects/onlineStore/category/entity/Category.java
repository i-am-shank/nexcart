package com.springProjects.onlineStore.category.entity;

import com.springProjects.onlineStore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(name = "categoryTitle", length = 50, nullable = false, unique = true)
    private String title;

    @Column(name = "categoryDescription")
    private String description;

    private Integer coverImageFileId;

    public Category(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
