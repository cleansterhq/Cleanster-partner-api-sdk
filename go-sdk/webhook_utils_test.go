package cleanster_test

import (
	"testing"

	cleanster "github.com/cleanster/cleanster-go-sdk"
)

const (
	whSecret   = "test-secret-key"
	whPayload  = `{"event":"BOOKING_CREATED","bookingId":42}`
	whExpected = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364"
)

func TestComputeWebhookSignature_ReturnsCorrectHex(t *testing.T) {
	got := cleanster.ComputeWebhookSignature(whSecret, whPayload)
	if got != whExpected {
		t.Errorf("expected %q, got %q", whExpected, got)
	}
}

func TestVerifyWebhookSignature_ValidSignatureReturnsTrue(t *testing.T) {
	if !cleanster.VerifyWebhookSignature(whSecret, whPayload, whExpected) {
		t.Error("expected valid signature to verify as true")
	}
}

func TestVerifyWebhookSignature_WrongSignatureReturnsFalse(t *testing.T) {
	if cleanster.VerifyWebhookSignature(whSecret, whPayload, "badsignature") {
		t.Error("expected wrong signature to verify as false")
	}
}

func TestVerifyWebhookSignature_WrongSecretReturnsFalse(t *testing.T) {
	wrongSig := cleanster.ComputeWebhookSignature("wrong-secret", whPayload)
	if cleanster.VerifyWebhookSignature(whSecret, whPayload, wrongSig) {
		t.Error("expected signature from wrong secret to verify as false")
	}
}
