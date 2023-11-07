package com.danila.javafxauth.model;

import java.time.LocalDateTime;

public class FileInfo {
    private int id;
    private String fileHash;
    private LocalDateTime saveTime;
    private String filePath;
    private int userId;

    public FileInfo(String fileHash, LocalDateTime saveTime, String filePath, int userId) {
        this.fileHash = fileHash;
        this.saveTime = saveTime;
        this.filePath = filePath;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public LocalDateTime getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(LocalDateTime saveTime) {
        this.saveTime = saveTime;
    }
}
