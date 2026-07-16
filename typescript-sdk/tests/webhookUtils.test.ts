import { computeWebhookSignature, verifyWebhookSignature } from "../src/webhook-utils";

const SECRET   = "test-secret-key";
const PAYLOAD  = '{"event":"BOOKING_CREATED","bookingId":42}';
const EXPECTED = "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364";

describe("WebhookUtils", () => {
  test("computeWebhookSignature returns correct hex", () => {
    expect(computeWebhookSignature(SECRET, PAYLOAD)).toBe(EXPECTED);
  });

  test("verifyWebhookSignature returns true for valid signature", () => {
    expect(verifyWebhookSignature(SECRET, PAYLOAD, EXPECTED)).toBe(true);
  });

  test("verifyWebhookSignature returns false for wrong signature", () => {
    expect(verifyWebhookSignature(SECRET, PAYLOAD, "badsignature")).toBe(false);
  });

  test("verifyWebhookSignature returns false for wrong secret", () => {
    const wrongSig = computeWebhookSignature("wrong-secret", PAYLOAD);
    expect(verifyWebhookSignature(SECRET, PAYLOAD, wrongSig)).toBe(false);
  });
});
