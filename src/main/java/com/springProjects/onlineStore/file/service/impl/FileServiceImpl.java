package com.springProjects.onlineStore.file.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.springProjects.onlineStore.category.service.CategoryService;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.file.constants.FileType;
import com.springProjects.onlineStore.file.constants.ImageType;
import com.springProjects.onlineStore.file.dto.FileResponseDTO;
import com.springProjects.onlineStore.file.entity.File;
import com.springProjects.onlineStore.file.mapper.FileMapper;
import com.springProjects.onlineStore.file.repository.FileRepository;
import com.springProjects.onlineStore.file.service.FileService;
import com.springProjects.onlineStore.user.service.UserService;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Override
    public FileResponseDTO uploadFile(MultipartFile multipartFile, String fileTypeStr, Integer parentEntityId)
            throws Exception {
        validateFileToUpload(multipartFile);

        if(Arrays.stream(FileType.values()).anyMatch(x -> x.name().equals(fileTypeStr))) {
            FileType fileType = FileType.valueOf(fileTypeStr);
            switch (fileType) {
                case USER_IMAGE:
                case CATEGORY_COVER_IMAGE:
                case PRODUCT_IMAGE:
                    return uploadImage(multipartFile, parentEntityId, fileType);
                default:
                    throw new UnsupportedOperationException("File-type not supported for upload : " + fileTypeStr);
            }
        } else {
            logger.error("Invalid File-type : {}", fileTypeStr);
            throw new IllegalArgumentException("Invalid File-type : " + fileTypeStr);
        }
    }

    private void validateFileToUpload(MultipartFile multipartFile) throws Exception {
        if(multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        long maxSize = (5L * 1024 * 1024); // 5 MB
        if(multipartFile.getSize() > maxSize) {
            throw new IllegalArgumentException("Maximum file size allowed is 5 MB");
        }
    }

    private FileResponseDTO uploadImage(MultipartFile multipartFile, Integer parentEntityId, FileType fileType) throws Exception {
        validateImageToUpload(multipartFile);
        switch (fileType) {
            case USER_IMAGE:
                validateUserForUserImageUpload(parentEntityId);
                break;
            case CATEGORY_COVER_IMAGE:
                validateCategoryForCoverImageUpload(parentEntityId);
                break;
            default:
                break;
        }
        File file = fileMapper.toEntity(multipartFile);
        file.setFileType(fileType);
        file.setParentEntityId(parentEntityId);
        file = fileRepository.save(file);
        updateFileReferenceInParentEntity(file, parentEntityId, fileType);
        return fileMapper.toResponseDTO(file);
    }

    /**
     * Adds fileId to parentEntity db entry
     * @param file
     * @param parentEntityId
     * @param fileType
     * @throws Exception
     */
    private void updateFileReferenceInParentEntity(File file, Integer parentEntityId, FileType fileType)
            throws Exception {
        switch (fileType) {
            case USER_IMAGE:
                updateImageReferenceInUser(file, parentEntityId);
                break;
            case CATEGORY_COVER_IMAGE:
                updateCoverImageReferenceInCategory(file, parentEntityId);
                break;
            default:
                break;
        }
    }

    private void updateImageReferenceInUser(File file, Integer userId) throws Exception {
        userService.updateUser(userId, null, file.getFileId());
    }

    private void updateCoverImageReferenceInCategory(File file, Integer categoryId) throws Exception {
        categoryService.updateCategory(categoryId, null, null, file.getFileId());
    }

    private void validateImageToUpload(MultipartFile multipartFile) throws Exception {
        String fileContentType = multipartFile.getContentType();
        if(fileContentType == null || !Arrays.stream(ImageType.values()).anyMatch(x -> x.value.equals(fileContentType))) {
            throw new IllegalArgumentException("Only JPG, PNG, JPEG allowed");
        }
    }

    /**
     * Validate if any image already exists for this userId
     *
     * @param userId
     * @throws Exception
     */
    private void validateUserForUserImageUpload(Integer userId) throws Exception {
        List<File> userImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(userId,
                FileType.USER_IMAGE);
        if(!CollectionUtils.isEmpty(userImages)) {
            throw new UnsupportedOperationException("Image already exists for user with id : " + userId);
        }
    }

    /**
     * Validate if any cover-image already exists for this categoryId
     * @param categoryId
     * @throws Exception
     */
    private void validateCategoryForCoverImageUpload(Integer categoryId) throws Exception {
        List<File> categoryCoverImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(
                categoryId, FileType.CATEGORY_COVER_IMAGE);
        if(!CollectionUtils.isEmpty(categoryCoverImages)) {
            throw new UnsupportedOperationException("Cover image already exists for category with id : " + categoryId);
        }
    }

    @Override
    public FileResponseDTO getFile(Integer fileId) throws Exception {
        File file = getFileByFileId(fileId);
        return fileMapper.toResponseDTO(file);
    }

    private File getFileByFileId(Integer fileId) throws Exception {
        if(fileId == null) {
            throw new IllegalArgumentException("fileId is empty");
        }
        File file = fileRepository.findByFileIdAndDeletedFalse(fileId);
        if(file == null) {
            throw new ResourceNotFoundException("File not found with id : " + fileId);
        }
        return file;
    }

    @Override
    public byte[] getUserImage(Integer userId) throws Exception {
        if(userId == null) {
            throw new IllegalArgumentException("userId is empty");
        }
        List<File> userImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(
                userId, FileType.USER_IMAGE);
        if(CollectionUtils.isEmpty(userImages)) {
            throw new ResourceNotFoundException("UserImage not found for userId : " + userId);
        }
        File userImage = userImages.get(0);
        return userImage.getData();
    }

    @Override
    public byte[] getCategoryCoverImage(Integer categoryId) throws Exception {
        if(categoryId == null) {
            throw new IllegalArgumentException("categoryId is empty");
        }
        List<File> categoryImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(categoryId, FileType.CATEGORY_COVER_IMAGE);
        if(CollectionUtils.isEmpty(categoryImages)) {
            throw new ResourceNotFoundException("Cover-image not found for category with id : " + categoryId);
        }
        File categoryImage = categoryImages.get(0);
        return categoryImage.getData();
    }

    @Override
    public List<FileResponseDTO> getProductImagesData(Integer productId) throws IllegalArgumentException {
        if(productId == null) {
            throw new IllegalArgumentException("productId is empty");
        }
        List<File> productImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(productId,
                FileType.PRODUCT_IMAGE);
        return productImages.stream()
                .map(file -> fileMapper.toResponseDTO(file))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] downloadProductImages(Integer productId) throws Exception {
        if(productId == null) {
            throw new IllegalArgumentException("productId is empty");
        }
        List<File> productImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(productId,
                FileType.PRODUCT_IMAGE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Creates a new ZIP output stream
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        int imageCount = 1;
        for(File productImage : productImages) {
            // image_2_imageName  :  imageName also contains file extension
            ZipEntry zipEntry = new ZipEntry("image_" + imageCount + "_" + productImage.getFileName());
            // putNextEntry(..)  :  starts writing a new ZIP file entry
            zipOutputStream.putNextEntry(zipEntry);
            // Writes byte[] length bytes to the output-stream
            zipOutputStream.write(productImage.getData());
            // Close current ZIP entry, position stream for writing next ZIP entry
            zipOutputStream.closeEntry();
            imageCount++;
        }
        // IMPORTANT step  :  to finalise the ZIP-stream , before it gets converted into bytes
        zipOutputStream.finish();  // or .close()
        return outputStream.toByteArray();
    }

    private String getImageFileExtension(File imageFile) throws Exception {
        String imageFileExtension = "";
        ImageType imageType;
        try {
            imageType = ImageType.valueOf(imageFile.getContentType());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid image file type : " + imageFile.getContentType());
            throw new UnsupportedOperationException("Unsupported image type : " + imageFile.getContentType());
        }

        switch (imageType) {
            case JPG:
                imageFileExtension = "jpg";
                break;
            case PNG:
                imageFileExtension = "png";
                break;
            case JPEG:
                imageFileExtension = "jpeg";
                break;
            default:
                logger.error("Invalid image file type for extensions : " + imageFile.getContentType());
                throw new UnsupportedOperationException("Unsupported image type for extensions : " +
                        imageFile.getContentType());
        }
        return imageFileExtension;
    }

    @Override
    public void deleteFile(Integer fileId) throws Exception {
        File file = getFileByFileId(fileId);
        fileRepository.delete(file);
    }

    @Override
    public void deleteEntityImages(Integer entityId, FileType fileType) {
        List<File> entityFiles = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(entityId, fileType);
        if(!CollectionUtils.isEmpty(entityFiles)) {
            fileRepository.deleteAll(entityFiles);
        }
    }
}
