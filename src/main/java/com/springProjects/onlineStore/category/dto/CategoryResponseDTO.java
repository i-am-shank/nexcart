package com.springProjects.onlineStore.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {
    private Integer categoryId;

    private String title;

    private String description;

    private Integer coverImageFileId;
}
