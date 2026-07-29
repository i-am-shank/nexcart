package com.springProjects.onlineStore.file.controller;

import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.file.dto.FileResponseDTO;
import com.springProjects.onlineStore.file.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;

/*
MediaType  :  "HTTP Content-Type"
    ->  use-case

MediaType.APPLICATION_JSON_VALUE    :    "application/json"
    ->  used for normal REST APIs
MediaType.MULTIPART_FORM_DATA_VALUE    :    "multipart/form-data"
    ->  used when request body contains multiple parts , in file upload
MediaType.APPLICATION_FORM_URLENCODED_VALUE    :    "application/x-www-form-urlencoded"
    ->  used by HTTP forms without files
MediaType.APPLICATION_OCTET_STREAM_VALUE    :    ""
    ->  unknown binary file download  (common for attachments)
MediaType.IMAGE_JPEG_VALUE  ,  MediaType.IMAGE_PNG_VALUE  ,  MediaType.IMAGE_GIF_VALUE
    ->  used for returning images
"application/pdf"
    ->  used for PDF
"audio/mpeg"  ,  "audio/wav"  ,  "audio/ogg"  ,  "audio/aac"
    ->  used for Audio
"video/mp4" (MP4), "video/x-msvideo" (AVI), "video/quicktime" (MOV), "video/webm" (WebM), "video/x-matroska" (MKV)
    ->  used for Video
*/

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDTO> uploadFile(@RequestParam("file") MultipartFile file,
                                                  @RequestParam("fileType") String fileType,
                                                  @RequestParam("parentEntityId") Integer parentEntityId)
            throws Exception {
        FileResponseDTO fileResponseDTO = fileService.uploadFile(file, fileType, parentEntityId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "File uploaded successfully",
                fileResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<ResponseDTO> getFile(@PathVariable("fileId") Integer fileId) throws Exception {
        FileResponseDTO fileResponseDTO = fileService.getFile(fileId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "File details fetched successfully",
                fileResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/userImage/{userId}")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Integer userId) throws Exception {
        byte[] userImage = fileService.getUserImage(userId);
        return ResponseEntity.ok(userImage);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<byte[]> getCategoryCoverImage(@PathVariable Integer categoryId) throws Exception {
        byte[] categoryCoverImage = fileService.getCategoryCoverImage(categoryId);
        return ResponseEntity.ok(categoryCoverImage);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ResponseDTO> getProductImagesData(@PathVariable Integer productId) throws Exception {
        List<FileResponseDTO> productImagesData = fileService.getProductImagesData(productId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK,
                "Product images data fetched successfully", productImagesData);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/product/{productId}/download")
    public ResponseEntity<byte[]> downloadProductImages(@PathVariable Integer productId) throws Exception {
        byte[] productImagesZip = fileService.downloadProductImages(productId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=productImages.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(productImagesZip);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ResponseDTO> deleteFile(@PathVariable("fileId") Integer fileId) throws Exception {
        fileService.deleteFile(fileId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "File deleted successfully");
        return ResponseEntity.ok(responseDTO);
    }
}
