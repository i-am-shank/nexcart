package com.springProjects.onlineStore.file.dto;

import com.springProjects.onlineStore.file.constants.FileType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileResponseDTO {
    private Integer fileId;

    private String fileName;

    private String contentType;

    private FileType fileType;

    private Integer parentEntityId;

    private LocalDateTime addedOn;

    private LocalDateTime updatedOn;
}
