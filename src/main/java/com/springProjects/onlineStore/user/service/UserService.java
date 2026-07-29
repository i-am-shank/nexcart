package com.springProjects.onlineStore.user.service;

import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.dto.ScrollResponseDTO;
import com.springProjects.onlineStore.user.dto.CreateUserRequestDTO;
import com.springProjects.onlineStore.user.dto.UpdateUserRequestDTO;
import com.springProjects.onlineStore.user.dto.UserResponseDTO;
import com.springProjects.onlineStore.user.dto.UserSummaryResponseDTO;

public interface UserService {
    /**
     * INSERT new user in db, after validating request object
     * @param createUserRequestDTO
     * @return Inserted User-response object
     * @throws Exception
     */
    public UserResponseDTO addUser(CreateUserRequestDTO createUserRequestDTO) throws Exception;

    /**
     * Updates name, bio, imageName of user with userId
     * @param userId
     * @param updateUserRequestDTO
     * @return updated User-response object
     * @throws Exception
     */
    public UserResponseDTO updateUser(Integer userId, UpdateUserRequestDTO updateUserRequestDTO,
                                      Integer userImageId) throws Exception;

    /**
     * If searchKeyword is present, searches all users having that keyword in name, or email starting with it.
     * Else, fetches all users
     *
     * @param pageNumber
     * @param pageSize
     * @param searchKeyword
     * @return List of user-summary objects
     * @throws Exception
     */
    public PageableResponseDTO<UserSummaryResponseDTO> getUsers(Integer pageNumber, Integer pageSize,
                                                                String searchKeyword, String sortBy,
                                                                String sortDirection) throws Exception;

    public ScrollResponseDTO<UserSummaryResponseDTO> getUsersNew(String searchKeyword, String scrollId,
                                                                 Integer pageSize) throws Exception;

    /**
     * Gets User for userId, if userId is valid & any active user exists with this id
     * @param userId
     * @return Fetched User-response object
     * @throws Exception
     */
    public UserResponseDTO getUser(Integer userId) throws Exception;

    /**
     * Gets user with this email
     * @param email
     * @return fetched User-response object
     * @throws Exception
     */
    public UserResponseDTO getUserByEmail(String email) throws Exception;

    /**
     * Soft deletes user with userId, also deleting user-image files
     * @param userId
     * @throws Exception
     */
    public void deleteUser(Integer userId) throws Exception;
}
