package com.springProjects.onlineStore.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponseDTO {
    private Integer id;

    private String name;

    private String email;

    private String profilePicName;

    private Integer imageId;
}
