module Cleanster
  module Api
    # WebhooksApi — manage real-time event notification endpoints.
    class WebhooksApi
      def initialize(http)
        @http = http
      end

      # Return all configured webhook endpoints.
      # @return [Models::ApiResponse]
      def list_webhooks
        raw = @http.get("/v1/webhooks")
        Models::ApiResponse.from_hash(raw)
      end

      # Register a new webhook endpoint.
      #
      # @param request [Hash] url (HTTPS endpoint), event, and optional extra fields.
      # @return [Models::ApiResponse]
      def create_webhook(request)
        raw = @http.post("/v1/webhooks", body: request)
        Models::ApiResponse.from_hash(raw)
      end

      # Update an existing webhook endpoint configuration.
      #
      # @param webhook_id [Integer]
      # @param request    [Hash] Updated fields.
      # @return [Models::ApiResponse]
      def update_webhook(webhook_id, request)
        raw = @http.put("/v1/webhooks/#{webhook_id}", body: request)
        Models::ApiResponse.from_hash(raw)
      end

      # Delete a webhook endpoint.
      #
      # @param webhook_id [Integer]
      # @return [Models::ApiResponse]
      def delete_webhook(webhook_id)
        raw = @http.delete("/v1/webhooks/#{webhook_id}")
        Models::ApiResponse.from_hash(raw)
      end
    end
  end
end
