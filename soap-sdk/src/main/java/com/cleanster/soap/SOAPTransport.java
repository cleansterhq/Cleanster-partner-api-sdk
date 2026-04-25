package com.cleanster.soap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Low-level HTTP transport for the Cleanster SOAP SDK.
 *
 * <p>Translates SOAP operation calls into Cleanster REST API HTTP requests.
 * Handles authentication, connection management, and error handling.
 */
public class SOAPTransport {

    public static final String BASE_URL = "https://api.cleanster.com";
    private static final int TIMEOUT_MS = 30_000;

    private final String apiKey;
    protected final ObjectMapper objectMapper;

    public SOAPTransport(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key must not be null or blank");
        }
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Execute a GET request and return the parsed JSON response node.
     */
    public JsonNode get(String path) {
        return execute("GET", path, null, "application/json");
    }

    /**
     * Execute a POST request with a JSON body.
     */
    public JsonNode post(String path, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return execute("POST", path, json.getBytes(StandardCharsets.UTF_8), "application/json");
        } catch (IOException e) {
            throw new SOAPClientException("Failed to serialize request body", e);
        }
    }

    /**
     * Execute a PUT request with a JSON body.
     */
    public JsonNode put(String path, Object body) {
        try {
            String json = objectMapper.writeValueAsString(body);
            return execute("PUT", path, json.getBytes(StandardCharsets.UTF_8), "application/json");
        } catch (IOException e) {
            throw new SOAPClientException("Failed to serialize request body", e);
        }
    }

    /**
     * Execute a DELETE request.
     */
    public JsonNode delete(String path) {
        return execute("DELETE", path, null, "application/json");
    }

    /**
     * Execute a multipart/form-data POST (for file uploads).
     */
    public JsonNode postMultipart(String path, byte[] fileData, String fileName) {
        String boundary = "CleansterSOAP_" + System.currentTimeMillis();
        byte[] body = buildMultipartBody(boundary, fileData, fileName);
        return execute("POST", path, body, "multipart/form-data; boundary=" + boundary);
    }

    private JsonNode execute(String method, String path, byte[] body, String contentType) {
        try {
            URL url = new URL(BASE_URL + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            conn.setRequestProperty("access-key", apiKey);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", contentType);

            if (body != null) {
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(body);
                }
            }

            int statusCode = conn.getResponseCode();
            InputStream stream = statusCode >= 400
                    ? conn.getErrorStream()
                    : conn.getInputStream();

            if (stream == null) {
                return objectMapper.createObjectNode().put("status", statusCode);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                String responseBody = sb.toString();

                if (statusCode >= 400) {
                    throw new SOAPClientException(
                            "HTTP " + statusCode + ": " + responseBody);
                }

                return responseBody.isEmpty()
                        ? objectMapper.createObjectNode().put("status", statusCode)
                        : objectMapper.readTree(responseBody);
            }
        } catch (SOAPClientException e) {
            throw e;
        } catch (IOException e) {
            throw new SOAPClientException("Network error calling " + path, e);
        }
    }

    private byte[] buildMultipartBody(String boundary, byte[] fileData, String fileName) {
        String ext = fileName.contains(".")
                ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase()
                : "jpg";
        String mime = ext.equals("png") ? "image/png"
                : ext.equals("gif") ? "image/gif"
                : "image/jpeg";

        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("\r\n");
        sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"")
          .append(fileName).append("\"\r\n");
        sb.append("Content-Type: ").append(mime).append("\r\n\r\n");

        byte[] header = sb.toString().getBytes(StandardCharsets.UTF_8);
        byte[] footer = ("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8);

        byte[] result = new byte[header.length + fileData.length + footer.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(fileData, 0, result, header.length, fileData.length);
        System.arraycopy(footer, 0, result, header.length + fileData.length, footer.length);
        return result;
    }

    /** Extract the data payload from an API response node. */
    public JsonNode extractData(JsonNode root) {
        if (root == null) return objectMapper.createObjectNode();
        return root.has("data") ? root.get("data") : root;
    }
}
