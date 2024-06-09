package com.meghasandesham.authenticationservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="\"user\"") //A users table will be created if not present
// Lets assume the table is already there, then ensure that data types and constraints are correctly mapped by using annotations
// If the names mismatch then also ensure to have a column annotation mapping
public class User {
    // Getters and setters

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.created_at = now;
        this.modified_at = now;
        if(this.profile_pic == null) {
            this.profile_pic = "profile_pic";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.modified_at = LocalDateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = true)
    private String password;
    @Column(nullable = true)
    private String profile_pic;
    @Column(nullable = true, columnDefinition = "timestamp(3) without time zone default CURRENT_TIMESTAMP")
    private LocalDateTime created_at;
    @Column(nullable = true, columnDefinition = "timestamp(3) without time zone default CURRENT_TIMESTAMP")
    private LocalDateTime modified_at;

}
