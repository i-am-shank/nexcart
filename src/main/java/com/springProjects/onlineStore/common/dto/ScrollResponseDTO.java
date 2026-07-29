package com.springProjects.onlineStore.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrollResponseDTO<T> {
    private List<T> items;

    // Encoded scroll position for next page
    private String scrollId;

    private Boolean hasNext;

    private Integer pageSize;
}
