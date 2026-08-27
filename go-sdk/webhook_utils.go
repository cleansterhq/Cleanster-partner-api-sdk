package cleanster

import (
	"crypto/hmac"
	"crypto/sha256"
	"encoding/hex"
)

// WebhookUtils provides helpers for verifying Cleanster webhook delivery signatures.
//
// When Cleanster sends a webhook, it computes an HMAC-SHA256 of the raw request
// body using the secret returned when the webhook was created, and includes the
// hex-encoded digest in the X-Webhook-Signature header.
//
// Example:
//
//	http.HandleFunc("/webhook", func(w http.ResponseWriter, r *http.Request) {
//	    body, _ := io.ReadAll(r.Body)
//	    secret  := os.Getenv("CLEANSTER_WEBHOOK_SECRET")
//	    sig     := r.Header.Get("X-Webhook-Signature")
//
//	    if !cleanster.VerifyWebhookSignature(secret, string(body), sig) {
//	        http.Error(w, "invalid signature", http.StatusUnauthorized)
//	        return
//	    }
//	    // process event…
//	})
var WebhookUtils = webhookUtils{}

type webhookUtils struct{}

// ComputeSignature computes the HMAC-SHA256 of payload keyed with secret and
// returns the lowercase hex-encoded digest.
func (webhookUtils) ComputeSignature(secret, payload string) string {
	mac := hmac.New(sha256.New, []byte(secret))
	mac.Write([]byte(payload))
	return hex.EncodeToString(mac.Sum(nil))
}

// VerifySignature returns true if signature is the valid HMAC-SHA256 of payload
// keyed with secret. Uses hmac.Equal for constant-time comparison.
func (webhookUtils) VerifySignature(secret, payload, signature string) bool {
	if secret == "" || payload == "" || signature == "" {
		return false
	}
	expected, err := hex.DecodeString(WebhookUtils.ComputeSignature(secret, payload))
	if err != nil {
		return false
	}
	actual, err := hex.DecodeString(signature)
	if err != nil {
		return false
	}
	return hmac.Equal(expected, actual)
}

// Package-level convenience functions.

// ComputeWebhookSignature computes the HMAC-SHA256 of payload keyed with secret.
func ComputeWebhookSignature(secret, payload string) string {
	return WebhookUtils.ComputeSignature(secret, payload)
}

// VerifyWebhookSignature returns true if signature is the valid HMAC-SHA256 of
// payload keyed with secret.
func VerifyWebhookSignature(secret, payload, signature string) bool {
	return WebhookUtils.VerifySignature(secret, payload, signature)
}
