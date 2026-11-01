package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.dto.request.UpdateUserRequest;
import com.openclassrooms.mddapi.dto.response.UserDto;
import com.openclassrooms.mddapi.exception.ConflictException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.CurrentUserProvider;
import com.openclassrooms.mddapi.security.UserPrincipal;
import com.openclassrooms.mddapi.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserProvider currentUserProvider;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("alice", "alice@mail.com", "old-hash");
        user.setId(1L);
        when(currentUserProvider.getCurrentUserById()).thenReturn(user);
    }

    @Test
    void getCurrentUserReturnsMappedUser() {
        UserDto dto = new UserDto(1L, "alice", "alice@mail.com", null);
        when(userMapper.toDto(user)).thenReturn(dto);

        assertThat(userService.getCurrentUser()).isEqualTo(dto);
    }

    @Test
    void updateKeepsPasswordWhenBlankAndReturnsNewToken() {
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(new UserDto(1L, "alice2", "alice2@mail.com", null));
        when(jwtService.generateToken(any(Authentication.class))).thenReturn("new-token");

        var response = userService.updateCurrentUser(new UpdateUserRequest("alice2", "Alice2@mail.com", ""));

        assertThat(user.getUsername()).isEqualTo("alice2");
        assertThat(user.getEmail()).isEqualTo("alice2@mail.com");
        assertThat(user.getPassword()).isEqualTo("old-hash");
        assertThat(response.token()).isEqualTo("new-token");
        assertThat(response.user().username()).isEqualTo("alice2");
        verify(passwordEncoder, never()).encode(anyString());

        ArgumentCaptor<Authentication> authentication = ArgumentCaptor.forClass(Authentication.class);
        verify(jwtService).generateToken(authentication.capture());
        assertThat(authentication.getValue().getPrincipal())
                .isInstanceOfSatisfying(UserPrincipal.class, principal -> {
                    assertThat(principal.id()).isEqualTo(1L);
                    assertThat(principal.username()).isEqualTo("alice2");
                });
    }

    @Test
    void updateHashesNewPassword() {
        when(userRepository.saveAndFlush(user)).thenReturn(user);
        when(passwordEncoder.encode("NewPassw0rd!")).thenReturn("new-hash");

        userService.updateCurrentUser(new UpdateUserRequest("alice", "alice@mail.com", "NewPassw0rd!"));

        assertThat(user.getPassword()).isEqualTo("new-hash");
    }

    @Test
    void updateRejectsEmailOfAnotherUser() {
        when(userRepository.existsByEmailAndIdNot("bob@mail.com", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateCurrentUser(new UpdateUserRequest("alice", "bob@mail.com", null)))
                .isInstanceOf(ConflictException.class);
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void updateRejectsUsernameOfAnotherUser() {
        when(userRepository.existsByUsernameAndIdNot("bob", 1L)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateCurrentUser(new UpdateUserRequest("bob", "alice@mail.com", null)))
                .isInstanceOf(ConflictException.class);
        verify(userRepository, never()).saveAndFlush(any());
    }
}
