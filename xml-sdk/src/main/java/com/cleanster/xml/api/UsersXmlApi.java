package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.CreateUserResponse;
import com.cleanster.xml.model.User;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Users API - create partner-managed users and issue access tokens.
 *
 * <h3>Endpoints (3)</h3>
 * <ul>
 *   <li>POST /v1/user/account                - create user account</li>
 *   <li>GET  /v1/user/access-token/{userId}  - fetch access token</li>
 *   <li>POST /v1/user/verify-jwt             - verify a JWT token</li>
 * </ul>
 */
public class UsersXmlApi {

    private final XmlHttpClient http;

    public UsersXmlApi(XmlHttpClient http) { this.http = http; }

    /**
     * Create a new user account on behalf of your platform.
     *
     * @param request  Map with keys: email, firstName, lastName, customerId, phone (optional).
     * @return response containing a {@link CreateUserResponse} with the new user's ID and access token.
     */
    public XmlApiResponse<CreateUserResponse> createUser(Map<String, Object> request) {
        String json = http.post("/v1/user/account", request);
        return http.fromJson(json, new TypeToken<XmlApiResponse<CreateUserResponse>>(){}.getType());
    }

    /** Convenience overload for creating a user with the most common fields. */
    public XmlApiResponse<CreateUserResponse> createUser(String email, String firstName, String lastName, String customerId) {
        Map<String, Object> body = new HashMap<>();
        body.put("email",      email);
        body.put("firstName",  firstName);
        body.put("lastName",   lastName);
        body.put("customerId", customerId);
        return createUser(body);
    }

    /**
     * Fetch a long-lived JWT access token for the given user.
     * Store the returned token via {@code CleansterXmlClient.setToken()} and pass it
     * as the {@code token} header on all subsequent calls made on behalf of this user.
     *
     * @param userId  Numeric user ID (returned by {@link #createUser}).
     * @return response containing a {@link User} with the token field populated.
     */
    public XmlApiResponse<User> fetchAccessToken(int userId) {
        String json = http.get("/v1/user/access-token/" + userId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<User>>(){}.getType());
    }

    /**
     * Verify that a JWT token is valid and not expired.
     *
     * @param token  The JWT string to verify.
     * @return response indicating whether the token is valid.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse verifyJwt(String token) {
        String json = http.post("/v1/user/verify-jwt", Map.of("token", token));
        return http.fromJson(json, XmlApiResponse.class);
    }
}
