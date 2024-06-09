package com.meghasandesham.authenticationservice.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.meghasandesham.authenticationservice.dto.*;
import com.meghasandesham.authenticationservice.entity.User;
import com.meghasandesham.authenticationservice.repository.UserRepository;
import com.meghasandesham.authenticationservice.security.CustomUser;
import com.meghasandesham.authenticationservice.security.CustomUserDetailService;
import com.meghasandesham.authenticationservice.security.JwtUtil;
import com.meghasandesham.authenticationservice.service.AuthService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private CustomUserDetailService userDetailsService;
    private  JwtUtil jwtUtil;
    private AmazonS3 amazonS3;
//

    @Override
    public RegisterResponseDTO registerUser(RegisterRequestDTO registerRequestDTO) throws Exception{
        String bucketName = "meghasandesham-profiles";
        String fileName = System.currentTimeMillis() + "_" + registerRequestDTO.getProfile_pic().getOriginalFilename();
        try (InputStream inputStream = registerRequestDTO.getProfile_pic().getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream, null);
            amazonS3.putObject(putObjectRequest);
            //awsUploadURL = amazonS3.getUrl(bucketName, fileName).toString(); -> this url need to be stored when s3 access is made public
            // but to make user images secure, we dont expose them publicly and public url (awsUploadURL) is not enough to get the image
            // so while showing the image, we get a signed url of the profile pic from server, which is used to load the image
        } catch (Exception e) {
            throw new Exception("Error uploading file to S3", e);
        }

        User userEntity = new User(
                registerRequestDTO.getId(),
                registerRequestDTO.getName(),
                registerRequestDTO.getEmail(),
                passwordEncoder.encode(registerRequestDTO.getPassword()),
                fileName,
                registerRequestDTO.getCreated_at(),
                registerRequestDTO.getModified_at()
        );

        User registeredUser = userRepository.save(userEntity);
        return new RegisterResponseDTO(
                registeredUser.getId(),
                registeredUser.getName(),
                registeredUser.getProfile_pic()
        );
    }

    @Override
    public LoginResponseDTO loginUser(LoginRequestDTO loginRequestDTO) throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
            );

        } catch(BadCredentialsException err) {
            throw new Exception("Incorrect username or password", err);
        }
        final CustomUser userDetails = (CustomUser) userDetailsService.loadUserByUsername(loginRequestDTO.getEmail()); //loadUserByUsername is an overriden function which is used to fetch data from db

        final String jwt = jwtUtil.generateToken(userDetails);
        return new LoginResponseDTO(jwt, "Bearer", jwtUtil.extractExpiration(jwt));
    }

    @Override
    public ValidationResponseDTO validateUser(ValidationRequestDTO validateRequestDTO) throws Exception {

        String jwt = validateRequestDTO.getJwt();
        try {
            Claims claims = jwtUtil.extractAllClaims(jwt);
            return new ValidationResponseDTO(
                    (String) claims.get("user_id"),
                    (String)claims.get("user_name"),
                    (String)claims.get("user_email"),
                    jwt
            );
        } catch (ExpiredJwtException e) {
            throw new Exception("JWT Expired. Please Login again", e);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new Exception("Something went wrong...", e);
        }
    }
}
