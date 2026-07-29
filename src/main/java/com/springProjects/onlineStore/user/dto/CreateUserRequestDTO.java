package com.springProjects.onlineStore.user.dto;

import com.springProjects.onlineStore.validation.annotation.ValidImageName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDTO {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Pattern validation
    @Pattern(regexp = "(?i)Male|Female", message = "Gender must be either 'Male' or 'Female'")
    private String gender;

    private String bio;

    // Custom validator
    @ValidImageName
    private String imageName;
}
