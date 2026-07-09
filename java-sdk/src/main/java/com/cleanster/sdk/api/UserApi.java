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
     * <p>Confirmed against the live sandbox API: this call only needs the {@code access-key}
     * header (no bearer token yet, since the user doesn't have one). It requires an
     * undocumented {@code customerId} field on the request, and the response is
     * {@code {userId, accessToken}}, not a full user profile.</p>
     *
     * @param request User account details (email, firstName, lastName, phone, customerId)
     * @return API response with the new user's ID and access token
     */
    public ApiResponse<CreateUserResponse> createUser(CreateUserRequest request) {
        return httpClient.post("/v1/user/account", request,
                new TypeReference<ApiResponse<CreateUserResponse>>() {});
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
