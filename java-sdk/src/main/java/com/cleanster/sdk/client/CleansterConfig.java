package com.cleanster.sdk.client;

/**
 * Configuration for the Cleanster SDK client.
 * Use the builder to construct instances.
 */
public class CleansterConfig {

    public static final String SANDBOX_BASE_URL =
            "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public";
    public static final String PRODUCTION_BASE_URL =
            "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public";

    private final String baseUrl;
    private final String accessKey;
    private final int connectTimeoutSeconds;
    private final int readTimeoutSeconds;
    private final int writeTimeoutSeconds;

    private CleansterConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.accessKey = builder.accessKey;
        this.connectTimeoutSeconds = builder.connectTimeoutSeconds;
        this.readTimeoutSeconds = builder.readTimeoutSeconds;
        this.writeTimeoutSeconds = builder.writeTimeoutSeconds;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public int getConnectTimeoutSeconds() {
        return connectTimeoutSeconds;
    }

    public int getReadTimeoutSeconds() {
        return readTimeoutSeconds;
    }

    public int getWriteTimeoutSeconds() {
        return writeTimeoutSeconds;
    }

    /**
     * Create a new builder for sandbox environment.
     *
     * @param accessKey Your Cleanster partner API access key
     */
    public static Builder sandboxBuilder(String accessKey) {
        return new Builder(accessKey).baseUrl(SANDBOX_BASE_URL);
    }

    /**
     * Create a new builder for production environment.
     *
     * @param accessKey Your Cleanster partner API access key
     */
    public static Builder productionBuilder(String accessKey) {
        return new Builder(accessKey).baseUrl(PRODUCTION_BASE_URL);
    }

    public static class Builder {
        private String baseUrl = SANDBOX_BASE_URL;
        private final String accessKey;
        private int connectTimeoutSeconds = 30;
        private int readTimeoutSeconds = 60;
        private int writeTimeoutSeconds = 60;

        public Builder(String accessKey) {
            if (accessKey == null || accessKey.isBlank()) {
                throw new IllegalArgumentException("accessKey must not be null or blank");
            }
            this.accessKey = accessKey;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder connectTimeoutSeconds(int seconds) {
            this.connectTimeoutSeconds = seconds;
            return this;
        }

        public Builder readTimeoutSeconds(int seconds) {
            this.readTimeoutSeconds = seconds;
            return this;
        }

        public Builder writeTimeoutSeconds(int seconds) {
            this.writeTimeoutSeconds = seconds;
            return this;
        }

        public CleansterConfig build() {
            return new CleansterConfig(this);
        }
    }
}
