package com.cleanster.xml.api;

import com.cleanster.xml.client.XmlHttpClient;
import com.cleanster.xml.model.BlacklistEntry;
import com.cleanster.xml.model.XmlApiResponse;
import com.google.gson.reflect.TypeToken;

import java.util.*;

/**
 * Blacklist API - prevent specific cleaners from being assigned to your properties.
 *
 * <h3>Endpoints (3)</h3>
 * <ol>
 *   <li>GET    /v1/blacklist/cleaner - list blacklisted cleaners</li>
 *   <li>POST   /v1/blacklist/cleaner - add cleaner to blacklist</li>
 *   <li>DELETE /v1/blacklist/cleaner - remove cleaner from blacklist</li>
 * </ol>
 */
public class BlacklistXmlApi {

    private final XmlHttpClient http;

    public BlacklistXmlApi(XmlHttpClient http) { this.http = http; }

    /** Return all blacklisted cleaners for the partner account. */
    public XmlApiResponse<List<BlacklistEntry>> listBlacklist() {
        String json = http.get("/v1/blacklist/cleaner");
        return http.fromJson(json, new TypeToken<XmlApiResponse<List<BlacklistEntry>>>(){}.getType());
    }

    /**
     * Add a cleaner to the blacklist.
     *
     * @param cleanerId  The cleaner's user ID.
     * @param reason     Optional reason for blacklisting.
     */
    public XmlApiResponse<BlacklistEntry> addToBlacklist(int cleanerId, String reason) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("cleanerId", cleanerId);
        if (reason != null) body.put("reason", reason);
        String json = http.post("/v1/blacklist/cleaner", body);
        return http.fromJson(json, new TypeToken<XmlApiResponse<BlacklistEntry>>(){}.getType());
    }

    /**
     * Remove a cleaner from the blacklist.
     *
     * @param cleanerId  The cleaner's user ID.
     */
    @SuppressWarnings("rawtypes")
    public XmlApiResponse removeFromBlacklist(int cleanerId) {
        String json = http.delete("/v1/blacklist/cleaner");
        return http.fromJson(json, XmlApiResponse.class);
    }
}
