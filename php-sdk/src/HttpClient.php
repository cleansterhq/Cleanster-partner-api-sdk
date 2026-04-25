<?php

declare(strict_types=1);

namespace Cleanster;

use Cleanster\Exceptions\ApiException;
use Cleanster\Exceptions\AuthException;
use Cleanster\Exceptions\CleansterException;

/**
 * Low-level HTTP transport layer.
 *
 * Uses PHP's built-in cURL extension — no external HTTP dependencies.
 * Attaches "access-key" and "token" auth headers on every request.
 * Maps HTTP error responses to typed SDK exceptions.
 */
class HttpClient
{
    private string $bearerToken = '';

    public function __construct(private readonly Config $config) {}

    /** Set the user bearer token sent as the "token" header. Thread-safe when PHP-FPM pools one request per process. */
    public function setToken(string $token): void
    {
        $this->bearerToken = $token;
    }

    /** Return the currently active bearer token. */
    public function getToken(): string
    {
        return $this->bearerToken;
    }

    /**
     * Perform a GET request.
     *
     * @param  string  $path   API path (e.g., "/v1/bookings").
     * @param  array   $query  Optional query parameters.
     * @return array           Decoded JSON response.
     */
    public function get(string $path, array $query = []): array
    {
        $url = $this->buildUrl($path, $query);
        return $this->request('GET', $url, null);
    }

    /**
     * Perform a POST request.
     *
     * @param  string      $path  API path.
     * @param  array|null  $body  Optional request body (serialised to JSON).
     * @return array              Decoded JSON response.
     */
    public function post(string $path, ?array $body = null): array
    {
        return $this->request('POST', $this->buildUrl($path), $body);
    }

    /**
     * Perform a PUT request.
     *
     * @param  string      $path  API path.
     * @param  array|null  $body  Optional request body (serialised to JSON).
     * @return array              Decoded JSON response.
     */
    public function put(string $path, ?array $body = null): array
    {
        return $this->request('PUT', $this->buildUrl($path), $body);
    }

    /**
     * Perform a DELETE request.
     *
     * @param  string      $path  API path.
     * @param  array|null  $body  Optional request body (serialised to JSON).
     * @return array              Decoded JSON response.
     */
    public function delete(string $path, ?array $body = null): array
    {
        return $this->request('DELETE', $this->buildUrl($path), $body);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private function buildUrl(string $path, array $query = []): string
    {
        $url = rtrim($this->config->baseUrl, '/') . $path;
        if (!empty($query)) {
            $url .= '?' . http_build_query($query);
        }
        return $url;
    }

    /**
     * Upload an image as multipart/form-data.
     *
     * @param  string  $path        API path (e.g. "/v1/checklist/5/upload").
     * @param  string  $imageData   Raw binary image content.
     * @param  string  $fileName    File name for the form-data part.
     * @return array                Decoded JSON response.
     */
    public function postMultipart(string $path, string $imageData, string $fileName): array
    {
        $url      = $this->buildUrl($path);
        $boundary = '----CleansterBoundary' . bin2hex(random_bytes(8));
        $body     = "--{$boundary}\r\n"
            . "Content-Disposition: form-data; name=\"file\"; filename=\"{$fileName}\"\r\n"
            . "Content-Type: image/*\r\n\r\n"
            . $imageData
            . "\r\n--{$boundary}--\r\n";

        $ch = curl_init();
        curl_setopt_array($ch, [
            CURLOPT_URL            => $url,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_TIMEOUT        => $this->config->timeout,
            CURLOPT_POST           => true,
            CURLOPT_POSTFIELDS     => $body,
            CURLOPT_HTTPHEADER     => [
                'Content-Type: multipart/form-data; boundary=' . $boundary,
                'access-key: ' . $this->config->accessKey,
                'token: ' . $this->bearerToken,
                'Accept: application/json',
            ],
        ]);

        $responseBody = curl_exec($ch);
        $httpCode     = (int) curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError    = curl_error($ch);
        curl_close($ch);

        if ($responseBody === false || $curlError !== '') {
            throw new CleansterException("cURL request failed: {$curlError}");
        }

        $responseBody = (string) $responseBody;

        if ($httpCode === 401) {
            throw new AuthException(401, $responseBody);
        }
        if ($httpCode < 200 || $httpCode >= 300) {
            throw new ApiException($httpCode, $responseBody, "API request failed with status {$httpCode}");
        }

        try {
            return json_decode($responseBody, true, 512, JSON_THROW_ON_ERROR);
        } catch (\JsonException $e) {
            throw new CleansterException('Failed to parse response JSON: ' . $e->getMessage());
        }
    }

    /**
     * Execute an HTTP request via cURL.
     *
     * @throws CleansterException on network failure or JSON parse error.
     * @throws AuthException      on HTTP 401.
     * @throws ApiException       on HTTP 4xx/5xx (other than 401).
     */
    public function request(string $method, string $url, ?array $body): array
    {
        $ch = curl_init();

        $headers = [
            'access-key: ' . $this->config->accessKey,
            'token: ' . $this->bearerToken,
            'Accept: application/json',
        ];

        if ($body !== null) {
            $headers[] = 'Content-Type: application/json';
        }

        $options = [
            CURLOPT_URL            => $url,
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_TIMEOUT        => $this->config->timeout,
            CURLOPT_HTTPHEADER     => $headers,
            CURLOPT_CUSTOMREQUEST  => $method,
        ];

        if ($body !== null) {
            $options[CURLOPT_POSTFIELDS] = json_encode($body, JSON_THROW_ON_ERROR);
        }

        curl_setopt_array($ch, $options);

        $responseBody = curl_exec($ch);
        $httpCode     = (int) curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError    = curl_error($ch);
        curl_close($ch);

        if ($responseBody === false || $curlError !== '') {
            throw new CleansterException("cURL request failed: {$curlError}");
        }

        $responseBody = (string) $responseBody;

        if ($httpCode === 401) {
            throw new AuthException(401, $responseBody);
        }

        if ($httpCode < 200 || $httpCode >= 300) {
            throw new ApiException(
                $httpCode,
                $responseBody,
                "API request failed with status {$httpCode}"
            );
        }

        try {
            $decoded = json_decode($responseBody, true, 512, JSON_THROW_ON_ERROR);
        } catch (\JsonException $e) {
            throw new CleansterException('Failed to parse response JSON: ' . $e->getMessage());
        }

        return $decoded;
    }
}
