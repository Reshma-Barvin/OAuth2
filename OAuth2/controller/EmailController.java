package com.example.SmartLifeTracker.controller;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.services.gmail.Gmail;
import com.example.SmartLifeTracker.dto.AuthToken;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.SmartLifeTracker.repository.AuthDAO;
import com.example.SmartLifeTracker.service.EmailService;

@RestController
public class EmailController {

    @Autowired
    EmailService emailService;
    @Autowired
    AuthDAO authDAO;

    @GetMapping("/hel")
    public void printyes(){
        System.out.println("yes");
    }


    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response) throws Exception {
        GoogleAuthorizationCodeFlow flow = emailService.getFlow();
        System.out.println("redirect worked");
        GoogleAuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl()
                .setRedirectUri("http://localhost:8080/oauth2/callback");
        System.out.println("redirect worked");
        response.sendRedirect(authorizationUrl.build());
    }

    @GetMapping("/oauth2/callback")
    @ResponseBody
    public String oauth2Callback(
            @RequestParam String state,
            @RequestParam String code,
            @RequestParam String scope
    ) throws Exception {
        System.out.println("Received code: " + code);

        GoogleAuthorizationCodeFlow flow = emailService.getFlow();

        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri("http://localhost:8080/oauth2/callback")
                .execute();

        System.out.println("Access Token: " + tokenResponse.getAccessToken());

        Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

        Gmail gmail = emailService.getGmails(credential);
        Gmail.Users.GetProfile getProfiles = gmail.users().getProfile("me");
        com.google.api.services.gmail.model.Profile profile = getProfiles.execute();

//        AuthToken authToken = new AuthToken();
//        authToken.setId(Long.valueOf(tokenResponse.getIdToken()));
//        authToken.setAccessToken(tokenResponse.getAccessToken());
//        authToken.setRefreshToken(tokenResponse.getRefreshToken());
//        authToken.setTokenType(tokenResponse.getTokenType());
//        authToken.setScope(tokenResponse.getScope());
//        authToken.setExpiresInSeconds(tokenResponse.getExpiresInSeconds());
//        authToken.setUserEmail(profile.getEmailAddress());
//
//        authDAO.save(authToken);

        System.out.println("Logged in as: " + profile.getEmailAddress());

        var messages = gmail.users().messages().list("me")
                .setMaxResults(10L)
                .execute()
                .getMessages();
        System.out.println("Fetched " + messages.size() + " emails successfully!");
        return "Welcome " + profile.getEmailAddress();
    }

    @GetMapping("req")
    public void get(@RequestParam String name){
        System.out.println(name);
    }

    @GetMapping("getUserEmail/getEmails")
    public String getUserEmailsByEmail(@RequestParam String email) throws Exception {

        AuthToken authToken = authDAO.findByUserEmail(email);
        if (authToken == null) {
            return "No authentication token found for email: " + email;
        }

        GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
        tokenResponse.setAccessToken(authToken.getAccessToken());
        tokenResponse.setRefreshToken(authToken.getRefreshToken());
        tokenResponse.setTokenType(authToken.getTokenType());
        tokenResponse.setScope(authToken.getScope());
        tokenResponse.setExpiresInSeconds(authToken.getExpiresInSeconds());
        GoogleAuthorizationCodeFlow flow = emailService.getFlow();
        Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

        // Build Gmail client
        Gmail gmail = emailService.getGmails(credential);


        // Fetch 10 emails
        var messages = gmail.users().messages().list("me")
                .setMaxResults(10L)
                .execute()
                .getMessages();

        return "Fetched " + messages.size() + " emails successfully!";
    }


}
