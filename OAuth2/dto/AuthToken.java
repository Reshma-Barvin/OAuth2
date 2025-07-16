package com.example.SmartLifeTracker.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "user_auth_tokens")
public class AuthToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Numeric primary key

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "token_type")
    private String tokenType;

    @Column(name = "expires_in")
    private Long expiresInSeconds;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "scope")
    private String scope;

}