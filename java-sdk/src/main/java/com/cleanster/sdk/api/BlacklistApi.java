package com.cleanster.sdk.api;

import com.cleanster.sdk.client.HttpClient;
import com.cleanster.sdk.model.*;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * API operations for the cleaner blacklist: list, add, and remove blacklisted cleaners.
 */
public class BlacklistApi {

    private final HttpClient httpClient;

    public BlacklistApi(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * List all blacklisted cleaners.
     *
     * @return API response with list of blacklisted cleaners
     */
    public ApiResponse<Object> listBlacklistedCleaners() {
        return httpClient.get("/v1/blacklist/cleaner",
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Add a cleaner to the blacklist.
     *
     * @param request Cleaner ID and reason
     * @return API response
     */
    public ApiResponse<Object> addToBlacklist(BlacklistRequest request) {
        return httpClient.post("/v1/blacklist/cleaner", request,
                new TypeReference<ApiResponse<Object>>() {});
    }

    /**
     * Remove a cleaner from the blacklist.
     *
     * @param request Cleaner ID to remove
     * @return API response
     */
    public ApiResponse<Object> removeFromBlacklist(BlacklistRequest request) {
        return httpClient.delete("/v1/blacklist/cleaner", request,
                new TypeReference<ApiResponse<Object>>() {});
    }
}
