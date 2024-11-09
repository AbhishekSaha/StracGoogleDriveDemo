package com.example.stracgoogledrive.services;

import com.example.stracgoogledrive.utils.GoogleDriveMockMultipartFile;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.api.services.drive.Drive;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockingDetails;


@ExtendWith(MockitoExtension.class)
public class GoogleDriveServiceTest {

    @Mock
    Drive mockDrive;

    @Mock
    Drive.Files mockDriveFiles;

    @Spy
    GoogleDriveService spyService;

    @Mock
    File mockFile;

    @Mock
    Resource mockResource;

    @Captor
    ArgumentCaptor<File> metadataFileCaptor;


    final static String TEST_FILE_ID = "FILE_ID_1";
    final static String TEST_FILE_NAME = "FILE_NAME_1";
    final static String TEST_FULL_FILENAME = "filename.txt";
    final static String TEST_CONTENT_TYPE = "text/plain";
    final static String TEST_DATA = "Foobar";



    @BeforeEach
    public void beforeEach() throws IOException {
        Mockito.when(mockDrive.files()).thenReturn(mockDriveFiles);
        doReturn(mockDrive).when(spyService).authenticateWithGoogleDrive(Mockito.anyString());

    }

    @Test
    void test_successful_getFile() throws GeneralSecurityException, IOException {
        Drive.Files.Get mockDriveFilesGet = Mockito.mock(Drive.Files.Get.class);
        Mockito.when(mockDriveFiles.get(TEST_FILE_ID)).thenReturn(mockDriveFilesGet);
        Mockito.when(mockDriveFilesGet.execute()).thenReturn(mockFile);

        spyService.GetFile(TEST_FILE_ID, "bar");

        Mockito.verify(mockDrive, Mockito.times(1)).files();
        Mockito.verify(mockDriveFiles, Mockito.times(1)).get(Mockito.eq(TEST_FILE_ID));
        Mockito.verify(mockDriveFilesGet, Mockito.times(1)).execute();

    }

    @Test
    void test_successful_listFiles() throws GeneralSecurityException, IOException {
        FileList mockedFileList = Mockito.mock(FileList.class);
        Drive.Files.List mockDriveFilesList = Mockito.mock(Drive.Files.List.class);

        Mockito.when(mockDriveFiles.list()).thenReturn(mockDriveFilesList);
        Mockito.when(mockDriveFilesList.execute()).thenReturn(mockedFileList);

        FileList actualList = spyService.ListFiles( "bar");

        assertThat(mockingDetails(actualList).isMock()).isTrue();
        Mockito.verify(mockDrive, Mockito.times(1)).files();
        Mockito.verify(mockDriveFilesList, Mockito.times(1)).execute();
    }


    @Test
    void test_successful_uploadFile() throws GeneralSecurityException, IOException {
        Drive.Files.Create mockDriveFilesCreate = Mockito.mock(Drive.Files.Create.class);
        Mockito.when(mockDriveFiles.create(Mockito.any(File.class), Mockito.any(InputStreamContent.class)))
                .thenReturn(mockDriveFilesCreate);
        Mockito.when(mockDriveFilesCreate.setFields(Mockito.anyString())).thenReturn(mockDriveFilesCreate);
        Mockito.when(mockDriveFilesCreate.execute()).thenReturn(mockFile);
        Mockito.when(mockResource.lastModified()).thenReturn(100L);

        GoogleDriveMockMultipartFile testGoogleDriveMockMultipartFile = new GoogleDriveMockMultipartFile(TEST_FILE_NAME, TEST_FULL_FILENAME, TEST_CONTENT_TYPE, TEST_DATA.getBytes(), mockResource);
        spyService.UploadFile(testGoogleDriveMockMultipartFile, "bar");
        Mockito.verify(mockDriveFiles).create(metadataFileCaptor.capture(), Mockito.any());
        File actualMetadata = metadataFileCaptor.getValue();
        assertEquals(TEST_FILE_NAME, actualMetadata.getName());
        assertEquals(TEST_CONTENT_TYPE, actualMetadata.getMimeType());


        Mockito.verify(mockDrive, Mockito.times(1)).files();
        Mockito.verify(mockDriveFilesCreate, Mockito.times(1)).execute();


    }


}
