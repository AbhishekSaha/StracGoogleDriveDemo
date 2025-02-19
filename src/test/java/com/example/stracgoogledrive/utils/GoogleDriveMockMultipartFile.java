package com.example.stracgoogledrive.utils;/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Note: Forking the class  to support  "GetResource"
 *
 * Mock implementation of the {@link org.springframework.web.multipart.MultipartFile}
 * interface.
 *
 * <p>Useful in conjunction with a {@link MockMultipartHttpServletRequest}
 * for testing application controllers that access multipart uploads.
 *
 * @author Juergen Hoeller
 * @author Eric Crampton
 * @since 2.0
 * @see MockMultipartHttpServletRequest
 */
public class GoogleDriveMockMultipartFile implements MultipartFile {

    private final String name;

    private final String originalFilename;

    @Nullable
    private final String contentType;

    private final byte[] content;

    @Nullable
    private Resource resource;


    /**
     * Create a new MockMultipartFile with the given content.
     *
     * @param name     the name of the file
     * @param content  the content of the file
     * @param resource
     */
    public GoogleDriveMockMultipartFile(String name, @Nullable byte[] content) {
        this(name, "", null, content, null);
    }

    /**
     * Create a new MockMultipartFile with the given content.
     * @param name the name of the file
     * @param contentStream the content of the file as stream
     * @throws IOException if reading from the stream failed
     */
    public GoogleDriveMockMultipartFile(String name, InputStream contentStream) throws IOException {
        this(name, "", null, FileCopyUtils.copyToByteArray(contentStream), null);
    }

    /**
     * Create a new MockMultipartFile with the given content.
     * @param name the name of the file
     * @param originalFilename the original filename (as on the client's machine)
     * @param contentType the content type (if known)
     * @param content the content of the file
     */
    public GoogleDriveMockMultipartFile(
            String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content) {

        Assert.hasLength(name, "Name must not be empty");
        this.name = name;
        this.originalFilename = (originalFilename != null ? originalFilename : "");
        this.contentType = contentType;
        this.content = (content != null ? content : new byte[0]);
    }

    public GoogleDriveMockMultipartFile(
            String name, @Nullable String originalFilename, @Nullable String contentType, @Nullable byte[] content, Resource resource) {

        Assert.hasLength(name, "Name must not be empty");
        this.name = name;
        this.originalFilename = (originalFilename != null ? originalFilename : "");
        this.contentType = contentType;
        this.content = (content != null ? content : new byte[0]);
        this.resource = resource;
    }

    /**
     * Create a new MockMultipartFile with the given content.
     * @param name the name of the file
     * @param originalFilename the original filename (as on the client's machine)
     * @param contentType the content type (if known)
     * @param contentStream the content of the file as stream
     * @throws IOException if reading from the stream failed
     */
    public GoogleDriveMockMultipartFile(
            String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream)
            throws IOException {

        this(name, originalFilename, contentType, FileCopyUtils.copyToByteArray(contentStream));
    }


    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public Resource getResource() {
        return this.resource;
    }

    @Override
    @NonNull
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return (this.content.length == 0);
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }

}
