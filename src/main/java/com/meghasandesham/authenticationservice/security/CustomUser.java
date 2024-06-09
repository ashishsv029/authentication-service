package com.meghasandesham.authenticationservice.security;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;


public class CustomUser extends User {
    private final UUID userId;
    private final String name;

    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities, String name, UUID userId) {
        super(username, password, authorities);
        this.userId = userId;
        this.name = name;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

}
