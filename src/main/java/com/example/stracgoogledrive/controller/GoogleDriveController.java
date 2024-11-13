package com.example.stracgoogledrive.controller;

import com.example.stracgoogledrive.controller.schemas.DownloadFileSchemas.DownloadFileResponse;
import com.example.stracgoogledrive.services.GoogleDriveService;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@Log4j2
@RequestMapping("/files")
public class GoogleDriveController {

    @Autowired
    GoogleDriveService service;

    @GetMapping("/list")
    public ResponseEntity<FileList> getAllFiles(@RequestHeader("Authorization") String authHeader) {
        FileList response = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                response = service.ListFiles(token);
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(401));
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(504));
    }

    @GetMapping
    public ResponseEntity<File> getFile(@RequestParam("fileId") String fileId, @RequestHeader("Authorization") String authHeader) {
        File response = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                response = service.GetFile(fileId, token);
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(401));
            }
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(504));
    }



    @PostMapping("/upload")
    public ResponseEntity<List<String>> upload(MultipartHttpServletRequest request, @RequestHeader("Authorization") String authHeader) {
        List<String> fileIds = new ArrayList<>();

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Iterator<String> fileNames = request.getFileNames();

                while (fileNames.hasNext()) {
                    String currentFilename = fileNames.next();
                    MultipartFile currentFile = request.getFile(currentFilename);
                    assert currentFile != null;
                    String uploadedFileId = service.UploadFile(currentFile, token);
                    fileIds.add(uploadedFileId);
                }

                return new ResponseEntity<>(fileIds, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>(fileIds, HttpStatusCode.valueOf(401));
            }
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        return new ResponseEntity<>(fileIds, HttpStatusCode.valueOf(504));
    }

    @GetMapping("/download")
    public ResponseEntity<DownloadFileResponse> downloadFile(@RequestParam("fileId") String fileId,  @RequestHeader("Authorization") String authHeader) {
        DownloadFileResponse response = null;

        try {

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                ByteArrayOutputStream downloadedFileStream = service.DownloadFile(fileId, token);
                response = new DownloadFileResponse(downloadedFileStream.toByteArray());
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>(response, HttpStatusCode.valueOf(401));
            }
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(504));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMapping(@RequestParam("fileId") String fileId, @RequestHeader("Authorization") String authHeader) {
        DownloadFileResponse response = null;

        try {

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                service.DeleteFile(fileId, token);

                return new ResponseEntity<>(fileId, HttpStatusCode.valueOf(200));
            } else {
                return new ResponseEntity<>(fileId, HttpStatusCode.valueOf(401));
            }
        } catch (Exception e) {
            log.error(e.getStackTrace());
        }
        return new ResponseEntity<>(fileId, HttpStatusCode.valueOf(504));
    }

}
