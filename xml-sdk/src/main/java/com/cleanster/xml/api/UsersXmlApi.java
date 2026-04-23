package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.User;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Users API — authenticate and manage user profiles.
 *
 * <h3>Endpoints (3)</h3>
 * <ul>
 *   <li>POST /users/{userId}/token — fetch access token</li>
 *   <li>GET  /users/{userId}       — get user profile</li>
 *   <li>PUT  /users/{userId}       — update user profile</li>
 * </ul>
 */
public class UsersXmlApi {

    private final XmlHttpClient http;

    public UsersXmlApi(XmlHttpClient http) { this.http = http; }

    /**
     * Fetch an access token for the given user.  The returned token should be
     * stored via {@code CleansterXmlClient.setToken()} before calling any other endpoint.
     *
     * @param userId numeric user ID
     * @return response containing a {@link User} with the token field populated
     */
    public XmlApiResponse<User> fetchAccessToken(int userId) {
        String json = http.post("/users/" + userId + "/token", Map.of());
        return http.fromJson(json, new TypeToken<XmlApiResponse<User>>(){}.getType());
    }

    /**
     * Retrieve a user's full profile.
     *
     * @param userId numeric user ID
     * @return response containing the {@link User}
     */
    public XmlApiResponse<User> getUserProfile(int userId) {
        String json = http.get("/users/" + userId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<User>>(){}.getType());
    }

    /**
     * Update a user's profile fields.
     *
     * @param userId    numeric user ID
     * @param firstName updated first name (nullable — omit by passing null)
     * @param lastName  updated last name (nullable)
     * @param phone     updated phone (nullable)
     * @return response containing the updated {@link User}
     */
    public XmlApiResponse<User> updateUserProfile(int userId,
                                                   String firstName,
                                                   String lastName,
                                                   String phone) {
        Map<String, Object> body = new HashMap<>();
        if (firstName != null) body.put("firstName", firstName);
        if (lastName  != null) body.put("lastName",  lastName);
        if (phone     != null) body.put("phone",      phone);
        String json = http.put("/users/" + userId, body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<User>>(){}.getType());
    }
}
