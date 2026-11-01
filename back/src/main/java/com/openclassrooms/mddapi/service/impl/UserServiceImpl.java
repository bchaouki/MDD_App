package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.response.UpdateUserResponse;
import com.openclassrooms.mddapi.dto.response.UserDto;
import com.openclassrooms.mddapi.exception.ConflictException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.security.jwt.JwtService;
import com.openclassrooms.mddapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CurrentUserProvider currentUserProvider;


    @Override
    @Transactional
    public UserDto getCurrentUser() {

        User user = currentUserProvider.getCurrentUserById();

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UpdateUserResponse updateCurrentUser(UpdateUserRequest userRequest) {
        User currentUser = currentUserProvider.getCurrentUserById();
        String username = userRequest.username().trim();
        String email = AuthServiceImpl.normalizeEmail(userRequest.email());

        // On verifie que le userName n'existe pas en BDD

        if (userRepository.existsByUsernameAndIdNot(username, currentUser.getId())) {
            throw new ConflictException("Ce nom d'utilisateur est déjà utilisé : " + username);
        }
        // On verifie que l'email n'existe pas en BDD
        if (userRepository.existsByEmailAndIdNot(email, currentUser.getId())) {
            throw new ConflictException("Ce email est déjà utilisé: " + email);
        }

        if (userRequest.password() != null && !userRequest.password().isEmpty()) {
            currentUser.setPassword(passwordEncoder.encode(userRequest.password()));
        }
        currentUser.setUsername(username);
        currentUser.setEmail(email);
        User savedUser = userRepository.saveAndFlush(currentUser);

        UserPrincipal principal = UserPrincipal.from(savedUser);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        return new UpdateUserResponse(userMapper.toDto(savedUser), jwtService.generateToken(authentication));
    }

}
