package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.exception.EmailAlreadyUsedException;
import com.openclassrooms.mddapi.exception.UsernameAlreadyUsedException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerHashesPasswordNormalizesEmailAndReturnsToken() {
        when(passwordEncoder.encode("Passw0rd!")).thenReturn("hash");
        when(jwtService.generateToken(any(Authentication.class))).thenReturn("token");

        var response = authService.register(new RegisterRequest(" alice ", " Alice@Mail.COM", "Passw0rd!"));

        assertThat(response.token()).isEqualTo("token");
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getUsername()).isEqualTo("alice");
        assertThat(saved.getValue().getEmail()).isEqualTo("alice@mail.com");
        assertThat(saved.getValue().getPassword()).isEqualTo("hash");
    }

    @Test
    void registerRejectsTakenEmail() {
        when(userRepository.existsByEmail("alice@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("alice", "alice@mail.com", "Passw0rd!")))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessageContaining("Email");
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerRejectsTakenUsername() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("alice", "alice@mail.com", "Passw0rd!")))
                .isInstanceOf(UsernameAlreadyUsedException.class)
                .hasMessageContaining("Nom d'utilisateur");
        verify(userRepository, never()).save(any());
    }

    @Test
    void loginWithValidCredentialsReturnsToken() {
        Authentication authenticated = new UsernamePasswordAuthenticationToken(
                new UserPrincipal(7L, "alice", "hash"), null, List.of());
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticated);
        when(jwtService.generateToken(authenticated)).thenReturn("token");

        assertThat(authService.login(new LoginRequest("alice@mail.com", "Passw0rd!")).token()).isEqualTo("token");
    }

    @Test
    void loginWithBadCredentialsFails() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(new LoginRequest("alice", "wrong")))
                .isInstanceOf(BadCredentialsException.class);
        verifyNoInteractions(jwtService);
    }

    @Test
    void loginFailsWhenPrincipalIsNotUserPrincipal() {
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("alice", null, List.of()));

        assertThatThrownBy(() -> authService.login(new LoginRequest("alice", "Passw0rd!")))
                .isInstanceOf(IllegalStateException.class);
    }
}
