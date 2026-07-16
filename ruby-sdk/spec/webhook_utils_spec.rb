require "spec_helper"

RSpec.describe Cleanster::WebhookUtils do
  let(:secret)   { "test-secret-key" }
  let(:payload)  { '{"event":"BOOKING_CREATED","bookingId":42}' }
  let(:expected) { "50be2cc8acebdc50a0016760550b78f09b281d455d8673f05b154a63ef060364" }

  describe ".compute_signature" do
    it "returns the correct HMAC-SHA256 hex" do
      expect(described_class.compute_signature(secret, payload)).to eq(expected)
    end
  end

  describe ".verify_signature" do
    it "returns true for a valid signature" do
      expect(described_class.verify_signature(secret, payload, expected)).to be true
    end

    it "returns false for a wrong signature" do
      expect(described_class.verify_signature(secret, payload, "badsignature")).to be false
    end

    it "returns false for a wrong secret" do
      wrong_sig = described_class.compute_signature("wrong-secret", payload)
      expect(described_class.verify_signature(secret, payload, wrong_sig)).to be false
    end
  end
end
