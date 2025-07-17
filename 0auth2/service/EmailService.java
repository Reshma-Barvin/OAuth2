package com.example.SmartLifeTracker.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

@Service
public class EmailService {

    private static final String APPLICATION_NAME = "SmartLife Tracker";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(GmailScopes.GMAIL_READONLY);

    public GoogleAuthorizationCodeFlow getFlow() throws Exception {
        InputStream in = getClass().getResourceAsStream("/credentials.json");
        return new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in)),
                SCOPES
        )
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                .setAccessType("offline")
                .build();
    }

    public Gmail getGmails(Credential credential) throws GeneralSecurityException, IOException {
        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                credential
        ).setApplicationName(APPLICATION_NAME).build();

    }

    public GoogleAuthorizationCodeFlow getLoginFlow() throws IOException, GeneralSecurityException {
        try (InputStream credentialsStream = getClass().getResourceAsStream("/credentials.json")) {
            if (credentialsStream == null) {
                throw new FileNotFoundException("Resource not found: /credentials.json");
            }

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    JSON_FACTORY, new InputStreamReader(credentialsStream)
            );

            return new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    clientSecrets,
                    Arrays.asList("openid", "email", "profile")
            )
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("login_tokens")))
                    .setAccessType("offline")
                    .build();
        }
    }

}
