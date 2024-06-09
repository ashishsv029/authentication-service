package com.meghasandesham.authenticationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResponseDTO {

    private String user_id;
    private String user_name;
    private String user_email;
    private String jwt;
}
