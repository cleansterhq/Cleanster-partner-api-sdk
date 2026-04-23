package cleanster

import (
        "bytes"
        "context"
        "encoding/json"
        "fmt"
        "io"
        "mime/multipart"
        "net/http"
        "net/url"
        "sync"
)

// httpClient is the internal HTTP transport layer.
// It attaches authentication headers, serializes request bodies,
// deserializes responses, and maps HTTP errors to SDK error types.
type httpClient struct {
        baseURL    string
        accessKey  string
        client     *http.Client
        mu         sync.RWMutex
        bearerToken string
}

func newHTTPClient(cfg Config) *httpClient {
        return &httpClient{
                baseURL:   cfg.BaseURL,
                accessKey: cfg.AccessKey,
                client: &http.Client{
                        Timeout: cfg.Timeout,
                },
        }
}

func (h *httpClient) setToken(token string) {
        h.mu.Lock()
        defer h.mu.Unlock()
        h.bearerToken = token
}

func (h *httpClient) getToken() string {
        h.mu.RLock()
        defer h.mu.RUnlock()
        return h.bearerToken
}

func (h *httpClient) get(ctx context.Context, path string, query url.Values) (rawAPIResponse, error) {
        u := h.baseURL + path
        if len(query) > 0 {
                u += "?" + query.Encode()
        }
        req, err := http.NewRequestWithContext(ctx, http.MethodGet, u, nil)
        if err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "failed to build request: " + err.Error()}
        }
        return h.do(req)
}

func (h *httpClient) post(ctx context.Context, path string, body interface{}) (rawAPIResponse, error) {
        return h.withBody(ctx, http.MethodPost, path, body)
}

func (h *httpClient) put(ctx context.Context, path string, body interface{}) (rawAPIResponse, error) {
        return h.withBody(ctx, http.MethodPut, path, body)
}

func (h *httpClient) delete(ctx context.Context, path string, body interface{}) (rawAPIResponse, error) {
        return h.withBody(ctx, http.MethodDelete, path, body)
}

func (h *httpClient) postMultipart(ctx context.Context, path string, imageData []byte, fileName string) (rawAPIResponse, error) {
        var buf bytes.Buffer
        w := multipart.NewWriter(&buf)
        part, err := w.CreateFormFile("image", fileName)
        if err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "failed to create multipart writer: " + err.Error()}
        }
        if _, err = part.Write(imageData); err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "failed to write image data: " + err.Error()}
        }
        w.Close()

        req, err := http.NewRequestWithContext(ctx, http.MethodPost, h.baseURL+path, &buf)
        if err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "failed to build request: " + err.Error()}
        }
        req.Header.Set("Content-Type", w.FormDataContentType())
        return h.do(req)
}

func (h *httpClient) withBody(ctx context.Context, method, path string, body interface{}) (rawAPIResponse, error) {
        var r io.Reader
        if body != nil {
                data, err := json.Marshal(body)
                if err != nil {
                        return rawAPIResponse{}, &CleansterError{Message: "failed to encode request body: " + err.Error()}
                }
                r = bytes.NewReader(data)
        }
        req, err := http.NewRequestWithContext(ctx, method, h.baseURL+path, r)
        if err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "failed to build request: " + err.Error()}
        }
        if r != nil {
                req.Header.Set("Content-Type", "application/json")
        }
        return h.do(req)
}

func (h *httpClient) do(req *http.Request) (rawAPIResponse, error) {
        req.Header.Set("access-key", h.accessKey)
        req.Header.Set("token", h.getToken())

        resp, err := h.client.Do(req)
        if err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "request failed: " + err.Error()}
        }
        defer resp.Body.Close()

        bodyBytes, err := io.ReadAll(resp.Body)
        if err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "failed to read response body: " + err.Error()}
        }

        switch {
        case resp.StatusCode == 401:
                return rawAPIResponse{}, &AuthError{StatusCode: 401, ResponseBody: string(bodyBytes)}
        case resp.StatusCode < 200 || resp.StatusCode >= 300:
                return rawAPIResponse{}, &APIError{
                        StatusCode:   resp.StatusCode,
                        Message:      fmt.Sprintf("API request failed with status %d", resp.StatusCode),
                        ResponseBody: string(bodyBytes),
                }
        }

        var raw rawAPIResponse
        if err := json.Unmarshal(bodyBytes, &raw); err != nil {
                return rawAPIResponse{}, &CleansterError{Message: "failed to parse response JSON: " + err.Error()}
        }
        return raw, nil
}
