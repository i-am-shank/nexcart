package com.springProjects.onlineStore.user.mapper;

import com.springProjects.onlineStore.user.dto.AddressResponseDTO;
import com.springProjects.onlineStore.user.dto.CreateUserRequestDTO;
import com.springProjects.onlineStore.user.dto.UserResponseDTO;
import com.springProjects.onlineStore.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    private final AddressMapper addressMapper;

    // Builder pattern used  :  @Builder annotation at target class
    public User toEntity(CreateUserRequestDTO userRequestDTO) {
        return User.builder()
                .name(userRequestDTO.getName())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .gender(userRequestDTO.getGender())
                .bio(userRequestDTO.getBio())
                .imageName(userRequestDTO.getImageName()).build();
    }

    // Model Mapper used  :  modelMapper Maven dependency added
    public UserResponseDTO toResponseDTO(User user) {
        if(user == null) {
            return null;
        }
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        UserResponseDTO userResponseDTO = modelMapper.map(user, UserResponseDTO.class);
        if(!CollectionUtils.isEmpty(user.getAddressList())) {
            List<AddressResponseDTO> addressResponseDTOList = user.getAddressList().stream()
                    .map(addressMapper::toResponseDTO)
                    .toList();
            userResponseDTO.setAddressList(addressResponseDTOList);
        }
        return userResponseDTO;
    }
}
