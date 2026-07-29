package com.springProjects.onlineStore.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// @MappedSuperclass  :  no separate db table, mapping info is applied to all entities inheriting it
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    private LocalDateTime addedOn;

    private LocalDateTime updatedOn;

    @Column(nullable = false)
    private Boolean deleted = false;

    // protected  :  can be called by sub-packages (none) or by subclasses (required in this case)
    protected BaseEntity() {};

    // Automatically called before INSERT
    @PrePersist
    protected void onCreate() {
        this.addedOn = LocalDateTime.now();
    }

    // Automatically called before UPDATE
    @PreUpdate
    protected void onUpdate() {
        this.updatedOn = LocalDateTime.now();
    }
}
