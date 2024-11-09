package com.example.stracgoogledrive.controller;

import com.example.stracgoogledrive.services.GoogleDriveService;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.github.scribejava.core.oauth.OAuth20Service;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StracGoogleDriveControllerIntegrationTest {

    @Autowired
    GoogleDriveService service;

    String authToken;

    private static final String CREDENTIALS_FILE_PATH = "src/test/java/com/example/stracgoogledrive/controller/integ_test_credentials.json";


    private static final List<String> SCOPES =
            Collections.singletonList(DriveScopes.DRIVE);
    @BeforeEach
    public void beforeEach() throws IOException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH)).createScoped(SCOPES);

        AccessToken token = credentials.refreshAccessToken();
        authToken = token.getTokenValue();
    }

    @Test
    void shouldListFiles(){

        RequestSpecification httpRequest = RestAssured.given();
        Header authHeader = new Header("Authorization", "Bearer " + authToken);
        httpRequest.header(authHeader);
        Response response = httpRequest.get("/files/list");

        // Retrieve the body of the Response
        ResponseBody body = response.getBody();

        System.out.println("Response Body is: " + body.asString());

    }
}
