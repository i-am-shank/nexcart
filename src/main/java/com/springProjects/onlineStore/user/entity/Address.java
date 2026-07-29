package com.springProjects.onlineStore.user.entity;

import com.springProjects.onlineStore.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String name;

    private String building;

    private String street;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String pinCode;

    @Column(nullable = false)
    private String phoneNumber;

    private Boolean isCurrent;

    public Address(User user, String name, String building, String city, String state, String pinCode,
                   String phoneNumber) {
        this.user = user;
        this.name = name;
        this.building = building;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
        this.phoneNumber = phoneNumber;
    }
}
