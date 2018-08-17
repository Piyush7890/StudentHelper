package com.example.mamid.studenthelper.Model;

import android.net.Uri;

public class Attachment {

    private String name;
    private Long size;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMIME_TYPE() {
        return MIME_TYPE;
    }

    public void setMIME_TYPE(String MIME_TYPE) {
        this.MIME_TYPE = MIME_TYPE;
    }

    private String MIME_TYPE;

    public Attachment(String uri, String MIME_TYPE) {
        this.uri = uri;
        this.MIME_TYPE = MIME_TYPE;
    }
}
