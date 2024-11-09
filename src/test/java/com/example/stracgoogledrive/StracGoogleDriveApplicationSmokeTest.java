package com.example.stracgoogledrive;

import com.example.stracgoogledrive.controller.GoogleDriveController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StracGoogleDriveApplicationSmokeTest {

    @Autowired
    private GoogleDriveController controller;


    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}
