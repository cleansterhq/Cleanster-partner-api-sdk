module Cleanster
  module Api
    # PaymentMethodsApi — manage Stripe and PayPal payment methods.
    class PaymentMethodsApi
      def initialize(http)
        @http = http
      end

      # Get Stripe SetupIntent details for collecting card information client-side.
      # @return [Models::ApiResponse]
      def get_setup_intent_details
        raw = @http.get("/v1/payment-methods/setup-intent")
        Models::ApiResponse.from_hash(raw)
      end

      # Get a PayPal client token for rendering the PayPal button client-side.
      # @return [Models::ApiResponse]
      def get_paypal_client_token
        raw = @http.get("/v1/payment-methods/paypal-client-token")
        Models::ApiResponse.from_hash(raw)
      end

      # Save a new payment method after client-side tokenization.
      #
      # @param request [Hash] paymentMethodId (Stripe token) or PayPal nonce.
      # @return [Models::ApiResponse]
      def add_payment_method(request)
        raw = @http.post("/v1/payment-methods", body: request)
        Models::ApiResponse.from_hash(raw)
      end

      # Return all saved payment methods for the current user.
      # @return [Models::ApiResponse]
      def get_payment_methods
        raw = @http.get("/v1/payment-methods")
        Models::ApiResponse.from_hash(raw)
      end

      # Delete a saved payment method.
      #
      # @param payment_method_id [Integer]
      # @return [Models::ApiResponse]
      def delete_payment_method(payment_method_id)
        raw = @http.delete("/v1/payment-methods/#{payment_method_id}")
        Models::ApiResponse.from_hash(raw)
      end

      # Set a payment method as the default for future bookings.
      #
      # @param payment_method_id [Integer]
      # @return [Models::ApiResponse]
      def set_default_payment_method(payment_method_id)
        raw = @http.put("/v1/payment-methods/#{payment_method_id}/default")
        Models::ApiResponse.from_hash(raw)
      end
    end
  end
end
