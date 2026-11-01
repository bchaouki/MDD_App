package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.response.UpdateUserResponse;
import com.openclassrooms.mddapi.dto.response.UserDto;
import org.springframework.security.core.Authentication;

public interface UserService {

    UserDto getCurrentUser ();

    UpdateUserResponse updateCurrentUser(UpdateUserRequest userRequest);
}
