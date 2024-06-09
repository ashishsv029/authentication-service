package com.meghasandesham.authenticationservice.security;

import com.meghasandesham.authenticationservice.entity.User;
import com.meghasandesham.authenticationservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        //returning spring security maintained default userDetails interface implemented User object which can hold only username, password and authorities;
        //this can be returned when the implementation is simple, but cant be used when we want to also include other details in UserDetails
        // So we need to create a UserDetails implemented customUser class by extending default user class and return that instance
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getEmail()) // Set username to email for uniquely finding the user based on his email
//                .password(user.getPassword())
//                .build();
        return new CustomUser(
                user.getEmail(),
                user.getPassword(),
                Collections.emptyList(),
                user.getName(),
                user.getId()
        );

    }
}
