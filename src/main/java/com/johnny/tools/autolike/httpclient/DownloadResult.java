package com.johnny.tools.autolike.httpclient;

import java.io.File;

public class DownloadResult {
    private int httpCode;
    private File downloadFile;
    private boolean success;
    private String errorMessage;

    public DownloadResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public DownloadResult(int httpCode, File downloadFile, boolean success, String errorMessage) {
        this.httpCode = httpCode;
        this.downloadFile = downloadFile;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public DownloadResult() {
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public File getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
