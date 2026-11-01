package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.dto.response.UserDto;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

//    public String getCurrentUserEmail(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)
//                || !authentication.isAuthenticated()) {
//            throw new AuthenticationCredentialsNotFoundException(
//                    "Authentification requise"
//            );
//        }
//        return   jwtAuth.getName();
//    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            throw new IllegalArgumentException("Le principal d'authentification n'est pas du type UserPrincipal");
        }
        return userPrincipal.id();
    }

    public User getCurrentUserById(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
//            throw new IllegalArgumentException("Le principal d'authentification n'est pas du type UserPrincipal");
//        }
//
//        // 2. On extrait l'ID (qui est un Long)
//        Long id = userPrincipal.id();
//        String username= userPrincipal.username();

        User user =userRepository.findById(getCurrentUserId())
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentification requise"));

        return user;

    }
}
