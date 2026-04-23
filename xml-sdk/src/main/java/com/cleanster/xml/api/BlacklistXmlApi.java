package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.BlacklistEntry;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Blacklist API — manage the cleaner blacklist.
 *
 * <h3>Endpoints (3)</h3>
 * <ol>
 *   <li>GET    /blacklist           — list blacklisted users</li>
 *   <li>POST   /blacklist           — add user to blacklist</li>
 *   <li>DELETE /blacklist/{userId}  — remove user from blacklist</li>
 * </ol>
 */
public class BlacklistXmlApi {

    private final XmlHttpClient http;

    public BlacklistXmlApi(XmlHttpClient http) { this.http = http; }

    public XmlApiResponse<List<BlacklistEntry>> listBlacklist() {
        String json = http.get("/blacklist");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<BlacklistEntry>>>(){}.getType());
    }

    public XmlApiResponse<BlacklistEntry> addToBlacklist(int userId, String reason) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("userId", userId);
        if (reason != null) body.put("reason", reason);
        String json = http.post("/blacklist", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<BlacklistEntry>>(){}.getType());
    }

    public XmlApiResponse<BlacklistEntry> removeFromBlacklist(int userId) {
        String json = http.delete("/blacklist/" + userId);
        return http.fromJson(json, new TypeToken<XmlApiResponse<BlacklistEntry>>(){}.getType());
    }
}
