package com.springProjects.onlineStore.user.service;

import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.user.dto.AddressResponseDTO;
import com.springProjects.onlineStore.user.dto.AddressRequestDTO;

import java.util.List;

public interface AddressService {
    AddressResponseDTO addAddress(Integer userId, AddressRequestDTO addressRequestDTO)
            throws IllegalArgumentException, ResourceNotFoundException;

    AddressResponseDTO updateAddress(Integer addressId, AddressRequestDTO addressRequestDTO)
            throws IllegalArgumentException, ResourceNotFoundException;

    void markAddressAsCurrent(Integer addressId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException;

    void removeAddressForUser(Integer addressId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException;

    List<AddressResponseDTO> getAddressesForUser(Integer userId) throws IllegalArgumentException;

    AddressResponseDTO getAddress(Integer addressId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException;
}
