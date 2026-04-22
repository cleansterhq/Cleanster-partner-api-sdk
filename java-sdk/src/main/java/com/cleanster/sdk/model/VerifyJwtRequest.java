package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request body for verifying a JWT token.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerifyJwtRequest {

    @JsonProperty("token")
    private String token;

    public VerifyJwtRequest() {}

    public VerifyJwtRequest(String token) {
        this.token = token;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
