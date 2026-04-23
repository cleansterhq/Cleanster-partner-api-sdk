package com.cleanster.xml.client;

import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Low-level HTTP client that speaks JSON to the Cleanster REST API and returns
 * raw JSON strings.  The API classes convert those JSON strings to JAXB-annotated
 * model objects via Gson.
 */
public class XmlHttpClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient http;
    private final String       baseUrl;
    private final String       accessKey;
    private       String       token;
    private final Gson         gson;

    XmlHttpClient(String baseUrl, String accessKey, OkHttpClient http) {
        this.baseUrl   = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.accessKey = accessKey;
        this.http      = http;
        this.gson      = new GsonBuilder().serializeNulls().create();
    }

    void setToken(String token) { this.token = token; }

    String getToken() { return token; }

    /* ────────────────────────────── public HTTP verbs ──────────────────────── */

    public String get(String path) {
        Request req = baseRequest(path).get().build();
        return execute(req);
    }

    public String post(String path, Object body) {
        String json    = gson.toJson(body);
        Request req    = baseRequest(path).post(RequestBody.create(json, JSON)).build();
        return execute(req);
    }

    public String put(String path, Object body) {
        String json    = gson.toJson(body);
        Request req    = baseRequest(path).put(RequestBody.create(json, JSON)).build();
        return execute(req);
    }

    public String patch(String path, Object body) {
        String json    = gson.toJson(body);
        Request req    = baseRequest(path).patch(RequestBody.create(json, JSON)).build();
        return execute(req);
    }

    public String delete(String path) {
        Request req = baseRequest(path).delete().build();
        return execute(req);
    }

    /* ──────────────────── JSON → typed object helpers ───────────────────────── */

    public <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    /* ─────────────────────────── private helpers ──────────────────────────── */

    private Request.Builder baseRequest(String path) {
        Request.Builder b = new Request.Builder()
                .url(baseUrl + path)
                .header("access-key", accessKey);
        if (token != null && !token.isEmpty()) {
            b.header("token", token);
        }
        return b;
    }

    private String execute(Request req) {
        try (Response resp = http.newCall(req).execute()) {
            ResponseBody body = resp.body();
            String       raw  = body != null ? body.string() : "";
            if (!resp.isSuccessful()) {
                throw new CleansterXmlException(resp.code(),
                        "HTTP " + resp.code() + " " + resp.message(), raw);
            }
            return raw;
        } catch (IOException e) {
            throw new CleansterXmlException("Network error: " + e.getMessage(), e);
        }
    }

    /* ─────────────────────── factory builder ──────────────────────────────── */

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String       baseUrl   = "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public";
        private String       accessKey = "";
        private long         timeoutMs = 30_000;
        private OkHttpClient custom;

        public Builder baseUrl(String url)         { this.baseUrl = url;    return this; }
        public Builder accessKey(String key)       { this.accessKey = key;  return this; }
        public Builder timeoutMs(long ms)          { this.timeoutMs = ms;   return this; }
        public Builder httpClient(OkHttpClient c)  { this.custom = c;       return this; }

        public XmlHttpClient build() {
            OkHttpClient client = custom != null ? custom :
                    new OkHttpClient.Builder()
                            .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                            .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                            .build();
            return new XmlHttpClient(baseUrl, accessKey, client);
        }
    }
}
