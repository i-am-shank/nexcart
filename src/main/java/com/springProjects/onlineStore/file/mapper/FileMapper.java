package com.springProjects.onlineStore.file.mapper;

import com.springProjects.onlineStore.file.dto.FileResponseDTO;
import com.springProjects.onlineStore.file.entity.File;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class FileMapper {
    @Autowired
    private ModelMapper modelMapper;

    public File toEntity(MultipartFile multipartFile) throws IOException {
        return File.builder()
                .contentType(multipartFile.getContentType())
                .data(multipartFile.getBytes())
                .fileName(multipartFile.getOriginalFilename())
                .build();
    }

    public FileResponseDTO toResponseDTO(File file) {
        if(file == null) {
            return null;
        }
        return modelMapper.map(file, FileResponseDTO.class);
    }
}
