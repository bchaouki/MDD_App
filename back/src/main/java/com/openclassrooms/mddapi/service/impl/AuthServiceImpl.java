package com.openclassrooms.mddapi.service.impl;


import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.security.jwt.JwtService;
import com.openclassrooms.mddapi.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String username= request.username().trim();
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)){
            throw new EmailAlreadyUsedException(email);
        }

        if (userRepository.existsByUsername(username)){
            throw new UsernameAlreadyUsedException(username);
        }

        User user = new User(username, email, passwordEncoder.encode(request.password()));
        userRepository.save(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, Collections.emptyList());



        String token = jwtService.generateToken(authentication);
        return new AuthResponse(token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // L'AuthenticationManager va appeler en arrière-plan
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.identifier(), // Peut être l'email ou le username
                        request.password()
                )
        );
        // Vérification facultative, utile pendant le développement
        if (!(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new IllegalStateException(
                    "Le principal authentifié n'est pas un UserPrincipal"
            );
        }
        // Génération du token à partir de l'objet d'authentification valide
        String token = jwtService.generateToken(authentication);

        return new AuthResponse(token);
    }


    static String normalizeEmail(String email){
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
