package com.example.mamid.studenthelper.Model;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Note {
    @org.greenrobot.greendao.annotation.Id (autoincrement = true)
    private Long Id;

private boolean isLocked;



    String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentjson() {
        return attachmentjson;
    }

    public void setAttachmentjson(String attachmentjson) {
        this.attachmentjson = attachmentjson;
    }

    private String title;
    private String content;
    private String attachmentjson;
    private Long millis;
private int color;


@Generated(hash = 2041382863)
public Note(Long Id, boolean isLocked, String password, String title,
        String content, String attachmentjson, Long millis, int color) {
    this.Id = Id;
    this.isLocked = isLocked;
    this.password = password;
    this.title = title;
    this.content = content;
    this.attachmentjson = attachmentjson;
    this.millis = millis;
    this.color = color;
}

@Generated(hash = 1272611929)
public Note() {
}

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Long getMillis() {
        return millis;
    }

    public void setMillis(Long millis) {
        this.millis = millis;
    }

    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public boolean getIsLocked() {
        return this.isLocked;
    }

    public void setIsLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }
}
