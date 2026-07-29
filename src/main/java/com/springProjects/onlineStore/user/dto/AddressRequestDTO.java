package com.springProjects.onlineStore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    private String name;

    private String building;

    private String street;

    private String city;

    private String state;

    private String pinCode;

    private String phoneNumber;
}
