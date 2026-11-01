package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserPrincipal loadUserByUsername(String identifier) throws UsernameNotFoundException {
      return  userRepository.findByEmailOrUsername(identifier)
                .map(UserPrincipal::from)    //.map(user -> UserPrincipal.from(user))
                .orElseThrow(() -> new UsernameNotFoundException(identifier));
    }

    public Optional<UserPrincipal> loadUserById(Long id) {

        return userRepository.findById(id).map(UserPrincipal::from);
    }
}
