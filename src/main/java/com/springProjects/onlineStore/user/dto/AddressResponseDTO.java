package com.springProjects.onlineStore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDTO {
    private Integer addressId;

    private Integer userId;

    private String name;

    private String building;

    private String street;

    private String city;

    private String state;

    private String pinCode;

    private String phoneNumber;

    private Boolean isCurrent;

    private LocalDateTime addedOn;

    private LocalDateTime updatedOn;
}
