package com.springProjects.onlineStore.user.dto;

import com.springProjects.onlineStore.validation.annotation.ValidImageName;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDTO {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    private String bio;

    // Custom Validator
    @ValidImageName
    private String imageName;
}
