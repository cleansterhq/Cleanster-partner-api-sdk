package com.cleanster.soap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Generic API response wrapper (status + message). */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse {

    private int    status;
    private String message;

    public ApiResponse() {}

    public ApiResponse(int status, String message) {
        this.status  = status;
        this.message = message;
    }

    public int    getStatus()  { return status; }
    public String getMessage() { return message; }

    public void setStatus(int status)      { this.status = status; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() {
        return status >= 200 && status < 300;
    }
}
