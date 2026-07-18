require "openssl"

module Cleanster
  # Utility methods for verifying Cleanster webhook delivery signatures.
  #
  # When Cleanster sends a webhook it computes an HMAC-SHA256 of the raw
  # request body using the +secret+ returned when the webhook was created,
  # and includes the hex-encoded digest in the +X-Cleanster-Signature+ header.
  #
  # @example Rack / Sinatra handler
  #   post "/webhook" do
  #     secret    = ENV.fetch("CLEANSTER_WEBHOOK_SECRET")
  #     payload   = request.body.read
  #     signature = request.env["HTTP_X_CLEANSTER_SIGNATURE"].to_s
  #
  #     halt 401 unless Cleanster::WebhookUtils.verify_signature(secret, payload, signature)
  #     # process event…
  #     status 200
  #   end
  module WebhookUtils
    # Compute the HMAC-SHA256 of +payload+ keyed with +secret+.
    #
    # @param secret  [String] Webhook secret returned by the Cleanster API.
    # @param payload [String] Raw request body as a UTF-8 string.
    # @return [String] Lowercase hex-encoded HMAC-SHA256 digest.
    def self.compute_signature(secret, payload)
      OpenSSL::HMAC.hexdigest("SHA256", secret, payload)
    end

    # Verify that +signature+ matches the expected HMAC-SHA256 of +payload+.
    # Uses +OpenSSL.fixed_length_secure_compare+ (or +Rack::Utils.secure_compare+
    # fallback) for constant-time comparison to prevent timing attacks.
    #
    # @param secret    [String] Webhook secret returned by the Cleanster API.
    # @param payload   [String] Raw request body as a UTF-8 string.
    # @param signature [String] Value from the +X-Cleanster-Signature+ header.
    # @return [Boolean] +true+ if the signature is valid.
    def self.verify_signature(secret, payload, signature)
      return false if secret.nil? || payload.nil? || signature.nil?
      return false if secret.empty? || signature.empty?

      expected = compute_signature(secret, payload)
      return false unless expected.bytesize == signature.bytesize
      OpenSSL.fixed_length_secure_compare(expected, signature)
    rescue OpenSSL::OpenSSLError, ArgumentError
      false
    end
  end
end
