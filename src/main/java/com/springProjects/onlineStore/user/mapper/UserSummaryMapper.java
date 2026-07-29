package com.springProjects.onlineStore.user.mapper;

import com.springProjects.onlineStore.user.dto.UserSummaryResponseDTO;
import com.springProjects.onlineStore.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserSummaryMapper {
    // No annotation needed here  :  if all variables of UserSummaryResponseDTO have type & name, same as User
    // Else  :  can have one-or-more @Mapping , or can use @Mappings to accomodate multiple @Mapping
    @Mappings(value = {
            @Mapping(target = "id", source = "userId"),
            @Mapping(target = "profilePicName", source = "imageName")
    })
    public UserSummaryResponseDTO toUserSummaryResponseDTO(User user);
}
