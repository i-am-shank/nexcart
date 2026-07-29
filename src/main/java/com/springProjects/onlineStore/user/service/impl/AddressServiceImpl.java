package com.springProjects.onlineStore.user.service.impl;

import com.springProjects.onlineStore.exceptions.ResourceNotFoundException;
import com.springProjects.onlineStore.user.dto.AddressRequestDTO;
import com.springProjects.onlineStore.user.dto.AddressResponseDTO;
import com.springProjects.onlineStore.user.entity.Address;
import com.springProjects.onlineStore.user.entity.User;
import com.springProjects.onlineStore.user.mapper.AddressMapper;
import com.springProjects.onlineStore.user.repository.AddressRepository;
import com.springProjects.onlineStore.user.repository.UserRepository;
import com.springProjects.onlineStore.user.service.AddressService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final UserRepository userRepository;

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    @Override
    public AddressResponseDTO addAddress(Integer userId, AddressRequestDTO addressRequestDTO)
            throws IllegalArgumentException, ResourceNotFoundException {
        User user = getUserByUserId(userId);
        Address address = addressMapper.toEntity(addressRequestDTO);
        address.setUser(user); // child-entity side mapping
        // get Address-list for current user
        List<Address> userAddressList = addressRepository.findByUser_UserIdAndDeletedFalse(userId);
        if(CollectionUtils.isEmpty(userAddressList)){
            // If 1st address being added, set it as Current-address
            address.setIsCurrent(Boolean.TRUE);
        }
        address = addressRepository.save(address);
        user.getAddressList().add(address); // parent-entity side mapping
        userRepository.save(user);
        return addressMapper.toResponseDTO(address);
    }

    private User getUserByUserId(Integer userId) throws IllegalArgumentException, ResourceNotFoundException {
        if(userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        User user = userRepository.findByUserIdAndDeletedFalse(userId);
        if(user == null) {
            throw new ResourceNotFoundException("User not found with id : " + userId);
        }
        return user;
    }

    @Override
    public AddressResponseDTO updateAddress(Integer addressId, AddressRequestDTO addressRequestDTO)
            throws IllegalArgumentException, ResourceNotFoundException {
        Address address = getAddressByAddressId(addressId);
        updateAddressFields(address, addressRequestDTO);
        address = addressRepository.save(address);
        return addressMapper.toResponseDTO(address);
    }

    private void updateAddressFields(Address address, AddressRequestDTO addressRequestDTO) {
        if(StringUtils.isNotEmpty(addressRequestDTO.getBuilding())) {
            address.setBuilding(addressRequestDTO.getBuilding());
        }
        if(StringUtils.isNotEmpty(addressRequestDTO.getStreet())) {
            address.setStreet(addressRequestDTO.getStreet());
        }
        if(StringUtils.isNotBlank(addressRequestDTO.getName())) {
            address.setName(addressRequestDTO.getName());
        }
        if(StringUtils.isNotBlank(addressRequestDTO.getCity())) {
            address.setCity(addressRequestDTO.getCity());
        }
        if(StringUtils.isNotBlank(addressRequestDTO.getState())) {
            address.setState(addressRequestDTO.getState());
        }
        if(StringUtils.isNotBlank(addressRequestDTO.getPinCode())) {
            address.setPinCode(addressRequestDTO.getPinCode());
        }
        if(StringUtils.isNotBlank(addressRequestDTO.getPhoneNumber())) {
            address.setPhoneNumber(addressRequestDTO.getPhoneNumber());
        }
    }

    private Address getAddressByAddressId(Integer addressId) throws IllegalArgumentException,
            ResourceNotFoundException {
        if(addressId == null) {
            throw new IllegalArgumentException("addressId is null");
        }
        Address address = addressRepository.findByAddressIdAndDeletedFalse(addressId);
        if(address == null) {
            throw new ResourceNotFoundException("Address not found with id : " + addressId);
        }
        return address;
    }

    @Override
    public void markAddressAsCurrent(Integer addressId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException {
        if(addressId == null || userId == null) {
            throw new IllegalArgumentException("addressId and userId are mandatory");
        }
        List<Address> userAddressList = addressRepository.findByUser_UserIdAndDeletedFalse(userId);
        boolean addressExists = userAddressList.stream()
                .anyMatch(address1 -> address1.getAddressId().equals(addressId));
        if(!addressExists) {
            throw new ResourceNotFoundException("Address not found with id : " + addressId + " for userId : " + userId);
        }
        for(Address address1 : userAddressList){
            if(address1.getAddressId().equals(addressId)) {
                address1.setIsCurrent(Boolean.TRUE);
            } else {
                address1.setIsCurrent(Boolean.FALSE);
            }
        }
        addressRepository.saveAll(userAddressList);
    }

    @Transactional
    @Override
    public void removeAddressForUser(Integer addressId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException, UnsupportedOperationException {
        if(addressId == null || userId == null) {
            throw new IllegalArgumentException("addressId and userId are mandatory");
        }
        Address address = addressRepository.findByAddressIdAndUser_UserIdAndDeletedFalse(addressId, userId);
        if(address == null) {
            throw new ResourceNotFoundException("Address not found with id : " + addressId + " for userId : " + userId);
        }
        User user = userRepository.findByUserIdAndDeletedFalse(userId);
        if(user == null) {
            throw new ResourceNotFoundException("User not found with id : " + userId);
        }
        if(address.getIsCurrent()) {
            // mark any other address as current address
            List<Address> userAddressList = addressRepository.findByUser_UserIdAndDeletedFalse(userId);
            Address anyOtherUserAddress = userAddressList.stream()
                    .filter(address1 -> !address1.getAddressId().equals(addressId))
                    .findAny().orElse(null);
            if(anyOtherUserAddress == null) {
                throw new UnsupportedOperationException("Cannot find any other user address for userId : " + userId);
            }
            anyOtherUserAddress.setIsCurrent(Boolean.TRUE);
            addressRepository.save(anyOtherUserAddress);
        }
        address.setUser(null); // disconnect child-end mapping
        addressRepository.save(address);
        user.getAddressList().remove(address); // orphanRemoval = true , will delete this address
        userRepository.save(user);
    }

    @Override
    public List<AddressResponseDTO> getAddressesForUser(Integer userId) throws IllegalArgumentException {
        if(userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        List<Address> userAddressList = addressRepository.findByUser_UserIdAndDeletedFalse(userId);
        if(CollectionUtils.isEmpty(userAddressList)) {
            return new ArrayList<>();
        }
        return userAddressList.stream()
                .map(addressMapper::toResponseDTO)
                .toList();
    }

    private Address getAddressByAddressIdAndUserId(Integer addressId, Integer userId)
        throws IllegalArgumentException, ResourceNotFoundException {
        if(addressId == null || userId == null) {
            throw new IllegalArgumentException("addressId and userId are mandatory");
        }
        Address address = addressRepository.findByAddressIdAndUser_UserIdAndDeletedFalse(addressId, userId);
        if(address == null) {
            throw new ResourceNotFoundException("Address not found with id : " + addressId);
        }
        return address;
    }

    @Override
    public AddressResponseDTO getAddress(Integer addressId, Integer userId) throws IllegalArgumentException,
            ResourceNotFoundException {
        Address address = getAddressByAddressIdAndUserId(addressId, userId);
        return addressMapper.toResponseDTO(address);
    }
}
