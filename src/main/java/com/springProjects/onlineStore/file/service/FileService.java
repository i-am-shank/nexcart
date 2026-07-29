package com.springProjects.onlineStore.file.service;

import com.springProjects.onlineStore.file.constants.FileType;
import com.springProjects.onlineStore.file.dto.FileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    /**
     * Validates the file data (file-type, size-limit) , then uploads the file in files db-table
     *
     * @param multipartFile
     * @param fileTypeStr
     * @param parentEntityId
     * @return FileResponseDTO for the uploaded file
     * @throws Exception
     */
    public FileResponseDTO uploadFile(MultipartFile multipartFile, String fileTypeStr, Integer parentEntityId)
            throws Exception;

    /**
     * Get file-meta data (all data except raw binary data of file), for an active files table entry with fileId
     *
     * @param fileId
     * @return FileResponseDTO
     * @throws Exception
     */
    public FileResponseDTO getFile(Integer fileId) throws Exception;

    /**
     * Fetches raw binary data for an active files db-table entry, with parentEntityId userId, fileType USER_IMAGE
     *
     * @param userId
     * @return byte[]
     * @throws Exception
     */
    public byte[] getUserImage(Integer userId) throws Exception;

    /**
     * Fetches raw binary data for an active files db-table entry, with parentEntityId userId,
     * fileType CATEGORY_COVER_IMAGE
     * @param categoryId
     * @return byte[]
     * @throws Exception
     */
    public byte[] getCategoryCoverImage(Integer categoryId) throws Exception;

    /**
     * Gets file metadata of all the files for this productId and fileType PRODUCT_IMAGE
     * @param productId
     * @return
     * @throws IllegalArgumentException
     */
    public List<FileResponseDTO> getProductImagesData(Integer productId) throws IllegalArgumentException;

    /**
     * Downloads a zip file containing all the images for a product
     * @param productId fetches Product-images mapped to this productId
     * @return zip file in bytes
     * @throws Exception
     */
    public byte[] downloadProductImages(Integer productId) throws Exception;

    /**
     * Remove file entry from database, if any file found with fileId
     *      Not soft-deleting, as db-space remains occupied  -  files can be large
     *
     * @param fileId
     * @throws Exception
     */
    public void deleteFile(Integer fileId) throws Exception;

    public void deleteEntityImages(Integer entityId, FileType fileType);
}
