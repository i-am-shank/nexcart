package com.springProjects.onlineStore.user.mapper;

import com.springProjects.onlineStore.user.dto.AddressRequestDTO;
import com.springProjects.onlineStore.user.dto.AddressResponseDTO;
import com.springProjects.onlineStore.user.entity.Address;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressMapper {
    private final ModelMapper mapper;

    public Address toEntity(AddressRequestDTO addressRequestDTO) {
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return mapper.map(addressRequestDTO, Address.class);
    }

    public AddressResponseDTO toResponseDTO(Address address) {
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        AddressResponseDTO addressResponseDTO = mapper.map(address, AddressResponseDTO.class);
        addressResponseDTO.setUserId(address.getUser() != null ? address.getUser().getUserId() : null);
        return addressResponseDTO;
    }
}
