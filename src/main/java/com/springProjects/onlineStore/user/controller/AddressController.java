package com.springProjects.onlineStore.user.controller;

import com.springProjects.onlineStore.common.dto.ResponseDTO;
import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.user.dto.AddressRequestDTO;
import com.springProjects.onlineStore.user.dto.AddressResponseDTO;
import com.springProjects.onlineStore.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> addAddress(@PathVariable Integer userId,
                                                  @RequestBody AddressRequestDTO addressRequestDTO) throws IllegalArgumentException {
        AddressResponseDTO addressResponseDTO = addressService.addAddress(userId, addressRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Address added successfully",
                addressResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ResponseDTO> updateAddress(@PathVariable Integer addressId,
                                                     @RequestBody AddressRequestDTO addressRequestDTO) {
        AddressResponseDTO addressResponseDTO = addressService.updateAddress(addressId, addressRequestDTO);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Address updated successfully",
                addressResponseDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{addressId}/mark-current/user/{userId}")
    public ResponseEntity<ResponseDTO> markAddressAsCurrent(@PathVariable Integer addressId,
                                                            @PathVariable Integer userId)
            throws IllegalArgumentException, ResourceNotFoundException {
        addressService.markAddressAsCurrent(addressId, userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Current address updated for user");
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{addressId}/user/{userId}")
    public ResponseEntity<ResponseDTO> removeAddress(@PathVariable Integer addressId,
                                                     @PathVariable Integer userId)
        throws IllegalArgumentException, ResourceNotFoundException {
        addressService.removeAddressForUser(addressId, userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "Address removed successfully");
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseDTO> getAddress(@PathVariable Integer userId)
            throws IllegalArgumentException, ResourceNotFoundException {
        List<AddressResponseDTO> userAddressList = addressService.getAddressesForUser(userId);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.OK, "User addresses retrieved successfully",
                userAddressList);
        return ResponseEntity.ok(responseDTO);
    }
}
