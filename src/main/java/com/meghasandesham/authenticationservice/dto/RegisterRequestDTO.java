package com.meghasandesham.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private MultipartFile profile_pic;
    private LocalDateTime created_at;
    private LocalDateTime modified_at;
}
