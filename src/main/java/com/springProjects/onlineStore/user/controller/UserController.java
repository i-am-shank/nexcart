package com.springProjects.onlineStore.user.controller;

import com.springProjects.onlineStore.common.dto.PageableResponseDTO;
import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.common.dto.ScrollResponseDTO;
import com.springProjects.onlineStore.user.dto.CreateUserRequestDTO;
import com.springProjects.onlineStore.user.dto.UpdateUserRequestDTO;
import com.springProjects.onlineStore.user.dto.UserResponseDTO;
import com.springProjects.onlineStore.user.dto.UserSummaryResponseDTO;
import com.springProjects.onlineStore.user.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // @Valid  :  added for Hibernate validation of Request DTO (in CREATE & UPDATE APIs)
    @PostMapping
    public ResponseEntity<ResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequestDTO)
            throws Exception {
        logger.info("Creating new user");
        UserResponseDTO addedUserResponseDTO = userService.addUser(createUserRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "User created successfully",
                addedUserResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDTO> updateUser(@PathVariable("userId") Integer userId,
                                                  @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO)
            throws Exception {
        logger.info("Updating user for id : {}", userId);
        UserResponseDTO updatedUserResponseDTO = userService.updateUser(userId, updateUserRequestDTO, null);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "User updated successfully",
                updatedUserResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable("userId") Integer userId) throws Exception {
        logger.info("Fetching user for id : {}", userId);
        UserResponseDTO fetchedUser = userService.getUser(userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK,
                "User fetched successfully for given userId", fetchedUser);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<ResponseDTO> getUserByEmail(@PathVariable("email") String email) throws Exception {
        logger.info("Fetching user with email : {}", email);
        UserResponseDTO fetchedUser = userService.getUserByEmail(email);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK,
                "User fetched successfully for given email", fetchedUser);
        return ResponseEntity.ok(responseDTO);
    }

    // Offset-based pagination
    @GetMapping
    public ResponseEntity<ResponseDTO> getUsers(@RequestParam(value = "pageNumber", required = false)
                                                Integer pageNumber,
                                                @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                @RequestParam(value = "search", required = false)
                                                String searchKeyword,
                                                @RequestParam(value = "sortBy", defaultValue = "name",
                                                        required = false) String sortBy,
                                                @RequestParam(value = "sortDirection", defaultValue = "ASC",
                                                        required = false) String sortDirection)
            throws Exception {
        PageableResponseDTO<UserSummaryResponseDTO> userList = userService.getUsers(pageNumber, pageSize, searchKeyword,
                sortBy, sortDirection);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Users fetched successfully", userList);
        return ResponseEntity.ok(responseDTO);
    }

    // Cursor-based pagination
    // TODO : Will work on this in future (marked incomplete for now)
    @GetMapping("/scroll")
    public ResponseEntity<ResponseDTO> getUsersNew(@RequestParam(value = "scrollId", required = false)
                                                   String scrollId,
                                                   @RequestParam(defaultValue = "5") Integer pageSize,
                                                   @RequestParam(value = "search", required = false)
                                                   String searchKeyword) throws Exception {
        ScrollResponseDTO<UserSummaryResponseDTO> userList = userService.getUsersNew(searchKeyword, scrollId, pageSize);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Users fetched successfully", userList);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> deleteUser(@PathVariable("userId") Integer userId) throws Exception {
        logger.info("Deleting user for id : {}", userId);
        userService.deleteUser(userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "User deleted successfully");
        return ResponseEntity.ok(responseDTO);
    }
}
