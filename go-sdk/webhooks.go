package cleanster

import (
	"context"
	"fmt"
)

// WebhooksService manages real-time event notification endpoints.
type WebhooksService struct {
	http *httpClient
}

// ListWebhooks returns all configured webhook endpoints.
func (s *WebhooksService) ListWebhooks(ctx context.Context) (APIResponse[[]map[string]interface{}], error) {
	raw, err := s.http.get(ctx, "/v1/webhooks", nil)
	if err != nil {
		return APIResponse[[]map[string]interface{}]{}, err
	}
	return decode[[]map[string]interface{}](raw)
}

// CreateWebhook registers a new webhook endpoint to receive booking event notifications.
func (s *WebhooksService) CreateWebhook(ctx context.Context, req WebhookRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.post(ctx, "/v1/webhooks", req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// UpdateWebhook updates the URL or event type of an existing webhook.
func (s *WebhooksService) UpdateWebhook(ctx context.Context, webhookID int, req WebhookRequest) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.put(ctx, fmt.Sprintf("/v1/webhooks/%d", webhookID), req)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}

// DeleteWebhook removes a webhook endpoint.
func (s *WebhooksService) DeleteWebhook(ctx context.Context, webhookID int) (APIResponse[map[string]interface{}], error) {
	raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/webhooks/%d", webhookID), nil)
	if err != nil {
		return APIResponse[map[string]interface{}]{}, err
	}
	return decode[map[string]interface{}](raw)
}
