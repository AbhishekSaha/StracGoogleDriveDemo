package com.example.stracgoogledrive.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;


@RestController
@Log4j2
@RequestMapping("/oauth2_redirect")
public class GoogleOauthController {

    @Value("spring.security.oauth2.client.registration.google.client-id")
    private String clientId;

    @Value("spring.security.oauth2.client.registration.google.client-secret")
    private String clientSecret;

    @GetMapping
    @PostMapping
    @CrossOrigin
    public String getFile(@RequestParam("authorizationToken") String token, HttpServletRequest request) {
//  const res = await axios.post(
//                "https://oauth2.googleapis.com/token",
//                new URLSearchParams({
//                        grant_type: "authorization_code",
//                code,
//                client_id: process.env.GCP_CLIENT_ID,
//                client_secret: process.env.GCP_CLIENT_SECRET,
//                redirect_uri: `your-domain/integrations/gcp-secret-manager/oauth2/callback`,
//  })
//) ;
        try {

            UriComponents uriComponents = UriComponentsBuilder
                    .fromUriString("https://oauth2.googleapis.com/token")
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("code", token)
                    .queryParam("client_id", clientId)
                    .queryParam("client_secret", clientSecret)
                    .queryParam("redirect_uri", "postmessage")
                    .encode()
                    .build();


            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(uriComponents.toUri())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            return client.sendAsync(postRequest, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

}
