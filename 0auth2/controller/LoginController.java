package com.example.SmartLifeTracker.controller;

import com.example.SmartLifeTracker.service.EmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    EmailService emailService;


    @GetMapping("/login")
    public void login(HttpServletResponse response) throws Exception {
        System.out.println("hello");
        GoogleAuthorizationCodeFlow flow = emailService.getLoginFlow();
        String url = flow.newAuthorizationUrl()
                .setRedirectUri("http://localhost:8080/auth/login/callback")
                .setScopes(Arrays.asList("openid", "email", "profile"))
                .build();
        response.sendRedirect(url);
    }

    @GetMapping("/login/callback")
    public String oauth2Callback(@RequestParam String code) throws Exception {
        GoogleTokenResponse tokenResponse = emailService.getLoginFlow()
                .newTokenRequest(code)
                .setRedirectUri("http://localhost:8080/auth/login/callback")
                .execute();

        NetHttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                .setAudience(Collections.singletonList("265994141714-mmmch8b563ad2mlhoitnnim64vkbfhmc.apps.googleusercontent.com"))
                .build();
        GoogleIdToken idToken = verifier.verify(tokenResponse.getIdToken());

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            return "Welcome " + payload.get("name") + " (" + email + ")";
        }
        return "Invalid ID Token.";
    }

}
