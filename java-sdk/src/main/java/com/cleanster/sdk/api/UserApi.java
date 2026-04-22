package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * API operations for user management: create user, fetch access token, and verify JWT.
 */
public class UserApi {

    private final HttpClient httpClient;

    public UserApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Create a new user account.
     *
     * @param request User account details (email, firstName, lastName, phone)
     * @return API response with created user data
     */
    public ApiResponse<User> createUser(CreateUserRequest request) {
        return httpClient.post("/v1/user/account", request,
                new TypeReference<ApiResponse<User>>() {});
    }

    /**
     * Fetch the long-lived access token for a user.
     * Use this token as the bearer token for subsequent user-authenticated requests.
     *
     * @param userId The user ID
     * @return API response with the access token
     */
    public ApiResponse<User> fetchAccessToken(int userId) {
        return httpClient.get("/v1/user/access-token/" + userId,
                new TypeReference<ApiResponse<User>>() {});
    }

    /**
     * Verify the validity of a JWT token.
     *
     * @param request JWT token to verify
     * @return API response with verification result
     */
    public ApiResponse<Object> verifyJwt(VerifyJwtRequest request) {
        return httpClient.post("/v1/user/verify-jwt", request,
                new TypeReference<ApiResponse<Object>>() {});
    }
}
