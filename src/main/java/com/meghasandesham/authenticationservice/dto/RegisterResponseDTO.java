package com.meghasandesham.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 "id": "ae239d75-2f8d-4a1a-a15e-a634da06c25c",
    "name": "nag",
    "email": "nag@gmail.com",
    "password": "password",
    "profile_pic": "profile_pic",
    "created_at": "2024-05-19T15:11:58.104Z",
    "modified_at": "2024-05-19T15:11:58.104Z"
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private UUID id;
    private String name;
//    private String email;
//    private String password;
    private String profile_pic;
//    private LocalDateTime created_at;
//    private LocalDateTime modified_at;

}
