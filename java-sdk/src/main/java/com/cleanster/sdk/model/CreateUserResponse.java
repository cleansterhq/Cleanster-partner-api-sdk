package com.cleanster.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response returned by {@code POST /v1/user/account}.
 *
 * <p>Confirmed against the live sandbox API: creating a user does NOT return a full user
 * profile. It returns only the new Cleanster user ID and a per-user JWT. The {@code accessToken}
 * value is already prefixed with {@code "Bearer "} - store it as-is and pass it straight into
 * {@code CleansterClient.Builder#accessToken(String)} (which itself adds "Bearer " once, so most
 * callers will want to strip the prefix first with {@link #getAccessTokenWithoutPrefix()}).</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse {

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("accessToken")
    private String accessToken;

    public CreateUserResponse() {}

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    /** Convenience accessor that strips the leading "Bearer " prefix, if present. */
    public String getAccessTokenWithoutPrefix() {
        if (accessToken == null) return null;
        return accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;
    }

    @Override
    public String toString() {
        return "CreateUserResponse{userId=" + userId + "}";
    }
}
