package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.response.UserDto;
import com.openclassrooms.mddapi.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
}
