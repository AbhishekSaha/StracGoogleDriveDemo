package com.example.stracgoogledrive.controller.schemas;

import lombok.AllArgsConstructor;
import lombok.Data;


public class DownloadFileSchemas {

    @Data
    @AllArgsConstructor
    public static class DownloadFileResponse {
        byte[] downloadedBytes;
    }
}