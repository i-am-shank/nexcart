package com.springProjects.onlineStore.user.dto;

import com.springProjects.onlineStore.user.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Integer userId;

    private String name;

    private String email;

    private String gender;

    private String bio;

    private String imageName;

    private Integer imageId;

    private List<AddressResponseDTO> addressList = new ArrayList<>();

    private LocalDateTime addedOn;

    private LocalDateTime updatedOn;
}
