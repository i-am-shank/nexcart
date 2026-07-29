package com.springProjects.onlineStore.user.service.impl;

import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.dto.ScrollResponseDTO;
import com.springProjects.onlineStore.common.utils.ScrollPositionUtil;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.file.constants.FileType;
import com.springProjects.onlineStore.file.entity.File;
import com.springProjects.onlineStore.file.repository.FileRepository;
import com.springProjects.onlineStore.user.dto.CreateUserRequestDTO;
import com.springProjects.onlineStore.user.dto.UpdateUserRequestDTO;
import com.springProjects.onlineStore.user.dto.UserResponseDTO;
import com.springProjects.onlineStore.user.dto.UserSummaryResponseDTO;
import com.springProjects.onlineStore.user.entity.User;
import com.springProjects.onlineStore.user.mapper.UserMapper;
import com.springProjects.onlineStore.user.mapper.UserSummaryMapper;
import com.springProjects.onlineStore.user.repository.UserRepository;
import com.springProjects.onlineStore.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSummaryMapper userSummaryMapper;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public UserResponseDTO addUser(CreateUserRequestDTO createUserRequestDTO) throws Exception {
        User user = userMapper.toEntity(createUserRequestDTO);
        if(user == null) {
            throw new IllegalArgumentException("Invalid request for creating user");
        }
        User addedUser = userRepository.save(user);
        return userMapper.toResponseDTO(addedUser);
    }

    @Override
    public UserResponseDTO updateUser(Integer userId, UpdateUserRequestDTO updateUserRequestDTO,
                                      Integer userImageId) throws Exception {
        User user = getUserForId(userId);
        if(updateUserRequestDTO != null) {
            if(StringUtils.hasLength(updateUserRequestDTO.getBio())) {
                user.setBio(updateUserRequestDTO.getBio());
            }
            if(StringUtils.hasLength(updateUserRequestDTO.getName())) {
                user.setName(updateUserRequestDTO.getName());
            }
            if(StringUtils.hasLength(updateUserRequestDTO.getImageName())) {
                user.setImageName(updateUserRequestDTO.getImageName());
            }
        }
        if(userImageId != null) {
            user.setImageId(userImageId);
        }
        User updatedUser = userRepository.save(user);
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    public PageableResponseDTO<UserSummaryResponseDTO> getUsers(Integer pageNumber, Integer pageSize, String searchKeyword,
                                                                String sortBy, String sortDirection) throws Exception {
        PageableResponseDTO<UserSummaryResponseDTO> userResponseDTOS;
        Sort.Direction direction = sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortColumn = isValidUserSortField(sortBy) ? sortBy : "name";
        Sort sort = Sort.by(direction, sortColumn);
        if(StringUtils.hasLength(searchKeyword)) {
            logger.info("Fetching users for searched keyword : {}", searchKeyword);
            userResponseDTOS = searchUsers(searchKeyword, pageNumber, pageSize, sort);
        } else {
            logger.info("Fetching all users");
            userResponseDTOS = getAllUsers(pageNumber, pageSize, sort);
        }
        return userResponseDTOS;
    }

    private Boolean isValidUserSortField(String sortBy) {
        if(StringUtils.hasLength(sortBy)) {
            return Arrays.stream(User.class.getDeclaredFields())
                    .anyMatch(field -> field.getName().equals(sortBy));
        }
        return false;
    }

    private PageableResponseDTO<UserSummaryResponseDTO> getAllUsers(Integer pageNumber, Integer pageSize, Sort sort) {
        if(pageNumber != null && pageSize != null) {
            // If Sort object not available, can do offset-pagination using only (pageNumber, pageSize) like :
            //      Pageable pageable = PageRequest.of(pageNumber, pageSize);
            // PageRequest also works with (pageNumber, pageSize, Sort)
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<User> userPage = userRepository.findAllByDeletedFalse(pageable);
            List<UserSummaryResponseDTO> userSummaryResponseDTOList = userPage.stream()
                    .map(user -> userSummaryMapper.toUserSummaryResponseDTO(user))
                    .toList();
            return new PageableResponseDTO<>(userSummaryResponseDTOList, userPage.isFirst(),
                    userPage.isLast(), userPage.getNumber(), userPage.getSize(),
                    userPage.getTotalElements(), userPage.getTotalPages());
        } else {
            List<User> userList = userRepository.findAllByDeletedFalse(sort);
            List<UserSummaryResponseDTO> userSummaryResponseDTOList = userList.stream()
                    .map(user -> userSummaryMapper.toUserSummaryResponseDTO(user))
                    .toList();
            return new PageableResponseDTO<>(userSummaryResponseDTOList, Boolean.TRUE, Boolean.TRUE,
                    0, userList.size(), (long) userList.size(), 1);
        }
    }

    private PageableResponseDTO<UserSummaryResponseDTO> searchUsers(String keyword, Integer pageNumber,
                                                                    Integer pageSize, Sort sort) {
        if(!StringUtils.hasLength(keyword)) {
            throw new IllegalArgumentException("Search keyword is required");
        }
        if(pageNumber != null && pageSize != null) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<User> userPage =
                    userRepository.findByNameContainingAndDeletedFalseOrEmailStartingWithAndDeletedFalse(
                            keyword, keyword, pageable);
            List<UserSummaryResponseDTO> userSummaryResponseDTOList = userPage.stream()
                    .map(user -> userSummaryMapper.toUserSummaryResponseDTO(user))
                    .toList();
            return new PageableResponseDTO<>(userSummaryResponseDTOList, userPage.isFirst(),
                    userPage.isLast(), userPage.getNumber(), userPage.getSize(),
                    userPage.getTotalElements(), userPage.getTotalPages());
        } else {
            List<User> userList = userRepository
                    .findByNameContainingAndDeletedFalseOrEmailStartingWithAndDeletedFalse(keyword, keyword, sort);
            List<UserSummaryResponseDTO> userSummaryResponseDTOList = userList.stream()
                    .map(user -> userSummaryMapper.toUserSummaryResponseDTO(user))
                    .toList();
            return new PageableResponseDTO<>(userSummaryResponseDTOList, Boolean.TRUE, Boolean.TRUE,
                    1, userList.size(), (long) userList.size(), 1);
        }
    }

    // TODO : Will work on this in future (marked incomplete for now)
    @Override
    public ScrollResponseDTO<UserSummaryResponseDTO> getUsersNew(String searchKeyword, String scrollId,
                                                                 Integer pageSize) throws Exception {
        ScrollResponseDTO<UserSummaryResponseDTO> usersScrollResultDTO;
        // Decode scroll position , from scrollId
        ScrollPosition scrollPosition = ScrollPositionUtil.decode(scrollId);
        // Create sort (using name as default field here - can also pass some field in API)
        // TODO : build Sort for Sorting (based on any param)
        Sort sort = Sort.by(
                Sort.Order.asc("name"), // key to sort by
                Sort.Order.desc("id") // tie-breaker
        );

        // Filter specification
        // TODO : build Specs for filtering (based on any param, criteria)
        Specification<User> userSpecification = null;
        if(StringUtils.hasLength(searchKeyword)) {
            usersScrollResultDTO = searchUsersNew(searchKeyword, scrollId, pageSize);
        } else {
            usersScrollResultDTO = getAllUsersNew(scrollId, pageSize);
        }
        return usersScrollResultDTO;
    }

    private ScrollResponseDTO<UserSummaryResponseDTO> getAllUsersNew(String scrollId, Integer pageSize) {
        // TODO : Use repository.findBy(spec , ..) for fetching scroll-pagination result from db
        return null;
    }

    private ScrollResponseDTO<UserSummaryResponseDTO> searchUsersNew(String searchKeyword, String scrollId,
                                                                     Integer pageSize) throws Exception {
        // TODO : Use repository.findBy(spec , ..) for fetching scroll-pagination result from db
        return null;
    }

    @Override
    public UserResponseDTO getUser(Integer userId) throws Exception {
        User user = getUserForId(userId);
        return userMapper.toResponseDTO(user);
    }

    private User getUserForId(Integer userId) throws Exception {
        if(userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        User user = userRepository.findByUserIdAndDeletedFalse(userId);
        if(user == null) {
            throw new ResourceNotFoundException("User not found for id : " + userId);
        }
        return user;
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) throws Exception {
        if(!StringUtils.hasLength(email)) {
            throw new IllegalArgumentException("email is empty");
        }
        User user = userRepository.findByEmailAndDeletedFalse(email);
        if(user == null) {
            throw new ResourceNotFoundException("User not found with email : " + email);
        }
        return userMapper.toResponseDTO(user);
    }

    @Override
    public void deleteUser(Integer userId) throws Exception {
        User user = getUserForId(userId);
        user.setDeleted(Boolean.TRUE);
        userRepository.save(user);
        // delete user-image
        List<File> userImages = fileRepository.findByParentEntityIdAndFileTypeAndDeletedFalse(userId,
                FileType.USER_IMAGE);
        if(!CollectionUtils.isEmpty(userImages)) {
            fileRepository.deleteAll(userImages);
        }
    }
}
