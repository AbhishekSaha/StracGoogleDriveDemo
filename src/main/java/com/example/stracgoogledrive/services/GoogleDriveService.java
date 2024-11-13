package com.example.stracgoogledrive.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    final Tika tika = new Tika();


    private static final List<String> SCOPES = new ArrayList<>(Arrays.asList(new String[]{DriveScopes.DRIVE, DriveScopes.DRIVE_FILE}));

    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    public GoogleDriveService() throws GeneralSecurityException, IOException {
    }

    protected Drive authenticateWithGoogleDrive(String oauthAccessToken) throws IOException {
        InputStream in = DriveQuickstart.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .build();

        GoogleCredential googleCredential = new GoogleCredential().setAccessToken(oauthAccessToken);
//
//        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8188).build();
//        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");


        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, googleCredential)
                .setApplicationName("Drive_Demo")
                .build();
    }

    public FileList ListFiles(String oauthAccessToken) throws IOException {

        Drive authenticatedGoogleDrive = authenticateWithGoogleDrive(oauthAccessToken);
        FileList result = authenticatedGoogleDrive.files().list().execute();
        for (File file : result.getFiles()) {
            System.out.printf("Found file: %s (%s)\n",
                    file.getName(), file.getMimeType(), file.getModifiedTime());
        }

        return result;
    }

    public File GetFile(String fileId, String oauthAccessToken) throws IOException {

        Drive authenticatedGoogleDrive = authenticateWithGoogleDrive(oauthAccessToken);
        return authenticatedGoogleDrive.files().get(fileId).execute();
    }

    public String UploadFile(MultipartFile inputFile, String oauthAccessToken) throws IOException {
        String mimeType = tika.detect(inputFile.getBytes());

        File metadataFile = new File();
        metadataFile.setName(inputFile.getName());
        metadataFile.setOriginalFilename(inputFile.getOriginalFilename());
        metadataFile.setModifiedTime(new DateTime(inputFile.getResource().lastModified()));
        metadataFile.setMimeType(mimeType);


        Drive authenticatedGoogleDrive = authenticateWithGoogleDrive(oauthAccessToken);
        File f = authenticatedGoogleDrive.files().create(metadataFile, new InputStreamContent(mimeType, inputFile.getInputStream()))
                .setFields("id")
                .execute();

        return f.getId();
    }

    public ByteArrayOutputStream DownloadFile(String fileId, String oauthAccessToken) throws IOException {
        Drive authenticatedGoogleDrive = authenticateWithGoogleDrive(oauthAccessToken);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            authenticatedGoogleDrive.files().get(fileId)
                    .executeMediaAndDownloadTo(outputStream);


            return outputStream;
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to move file: " + e.getDetails());
            throw e;
        }
    }

    public void DeleteFile(String fileId, String oauthAccessToken) throws IOException {
        Drive authenticatedGoogleDrive = authenticateWithGoogleDrive(oauthAccessToken);
        authenticatedGoogleDrive.files().delete(fileId)
                .execute();
    }

}
