package com.cleanster.sdk.client;

import com.cleanster.sdk.exception.CleansterApiException;
import com.cleanster.sdk.exception.CleansterAuthException;
import com.cleanster.sdk.exception.CleansterException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Internal HTTP client that wraps OkHttp and handles authentication, serialization, and error handling.
 */
public class HttpClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final CleansterConfig config;
    private volatile String bearerToken;

    public HttpClient(CleansterConfig config) {
        this.config = config;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(config.getConnectTimeoutSeconds(), TimeUnit.SECONDS)
                .readTimeout(config.getReadTimeoutSeconds(), TimeUnit.SECONDS)
                .writeTimeout(config.getWriteTimeoutSeconds(), TimeUnit.SECONDS)
                .build();
    }

    /**
     * Set the bearer token for user-authenticated requests.
     * This token is used in the Authorization header for user-specific endpoints.
     */
    public void setBearerToken(String token) {
        this.bearerToken = token;
    }

    /**
     * Returns the currently configured bearer token (may be null).
     */
    public String getBearerToken() {
        return bearerToken;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Build the full URL for an API path.
     */
    public String url(String path) {
        return config.getBaseUrl() + path;
    }

    /**
     * Execute a GET request with access-key and optional bearer token.
     */
    public <T> T get(String path, TypeReference<T> type) {
        Request request = new Request.Builder()
                .url(url(path))
                .addHeader("Content-Type", "application/json")
                .addHeader("access-key", config.getAccessKey())
                .addHeader("token", bearerToken != null ? bearerToken : "")
                .get()
                .build();
        return execute(request, type);
    }

    /**
     * Execute a POST request with a JSON body.
     */
    public <T> T post(String path, Object body, TypeReference<T> type) {
        String json = serialize(body);
        Request request = new Request.Builder()
                .url(url(path))
                .addHeader("Content-Type", "application/json")
                .addHeader("access-key", config.getAccessKey())
                .addHeader("token", bearerToken != null ? bearerToken : "")
                .post(RequestBody.create(json, JSON))
                .build();
        return execute(request, type);
    }

    /**
     * Execute a PUT request with a JSON body.
     */
    public <T> T put(String path, Object body, TypeReference<T> type) {
        String json = serialize(body);
        Request request = new Request.Builder()
                .url(url(path))
                .addHeader("Content-Type", "application/json")
                .addHeader("access-key", config.getAccessKey())
                .addHeader("token", bearerToken != null ? bearerToken : "")
                .put(RequestBody.create(json, JSON))
                .build();
        return execute(request, type);
    }

    /**
     * Execute a DELETE request (no body).
     */
    public <T> T delete(String path, TypeReference<T> type) {
        Request request = new Request.Builder()
                .url(url(path))
                .addHeader("Content-Type", "application/json")
                .addHeader("access-key", config.getAccessKey())
                .addHeader("token", bearerToken != null ? bearerToken : "")
                .delete()
                .build();
        return execute(request, type);
    }

    /**
     * Execute a multipart POST request for file uploads.
     *
     * @param path       API path (e.g., "/v1/checklist/5/upload")
     * @param imageBytes Raw bytes of the image to upload
     * @param fileName   File name sent in the form-data part (e.g., "photo.jpg")
     * @param type       TypeReference for response deserialization
     */
    public <T> T postMultipart(String path, byte[] imageBytes, String fileName, TypeReference<T> type) {
        RequestBody fileBody = RequestBody.create(imageBytes, MediaType.parse("image/*"));
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", fileName, fileBody)
                .build();
        Request request = new Request.Builder()
                .url(url(path))
                .addHeader("access-key", config.getAccessKey())
                .addHeader("token", bearerToken != null ? bearerToken : "")
                .post(requestBody)
                .build();
        return execute(request, type);
    }

    /**
     * Execute a DELETE request with a JSON body.
     */
    public <T> T delete(String path, Object body, TypeReference<T> type) {
        String json = serialize(body);
        Request request = new Request.Builder()
                .url(url(path))
                .addHeader("Content-Type", "application/json")
                .addHeader("access-key", config.getAccessKey())
                .addHeader("token", bearerToken != null ? bearerToken : "")
                .delete(RequestBody.create(json, JSON))
                .build();
        return execute(request, type);
    }

    private <T> T execute(Request request, TypeReference<T> type) {
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";

            if (response.code() == 401) {
                throw new CleansterAuthException(
                        "Unauthorized - invalid or missing access key/token", responseBody);
            }
            if (!response.isSuccessful()) {
                throw new CleansterApiException(response.code(),
                        "API request failed with status " + response.code(), responseBody);
            }
            return objectMapper.readValue(responseBody, type);
        } catch (CleansterException e) {
            throw e;
        } catch (IOException e) {
            throw new CleansterException("Network error: " + e.getMessage(), e);
        }
    }

    private String serialize(Object body) {
        if (body == null) return "{}";
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new CleansterException("Failed to serialize request body: " + e.getMessage(), e);
        }
    }
}
