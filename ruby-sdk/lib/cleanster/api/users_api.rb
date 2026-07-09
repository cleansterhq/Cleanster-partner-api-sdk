module Cleanster
  module Api
    # UsersApi - manage end-user accounts and authentication tokens.
    class UsersApi
      def initialize(http)
        @http = http
      end

      # Create a new user account under your partner.
      #
      # Confirmed against the live sandbox API: this requires an undocumented
      # +customer_id+ field, and the response is +{userId, accessToken}+, not a
      # full user profile.
      #
      # @param email       [String] Required.
      # @param first_name  [String] Required.
      # @param last_name   [String] Required.
      # @param customer_id [String] Required. Your own internal customer/user ID.
      # @param phone       [String, nil] Optional.
      # @return [Models::ApiResponse<Models::CreateUserResponse>]
      def create_user(email:, first_name:, last_name:, customer_id:, phone: nil)
        body = { email: email, firstName: first_name, lastName: last_name, customerId: customer_id }
        body[:phone] = phone if phone
        raw = @http.post("/v1/user/account", body: body)
        Models::ApiResponse.from_hash(raw, model_class: Models::CreateUserResponse)
      end

      # Fetch the long-lived bearer token for a user.
      # Pass the returned token to client.access_token=.
      #
      # @param user_id [Integer]
      # @return [Models::ApiResponse<Models::User>]  User with token field populated.
      def fetch_access_token(user_id)
        raw = @http.get("/v1/user/access-token/#{user_id}")
        Models::ApiResponse.from_hash(raw, model_class: Models::User)
      end

      # Verify that a JWT token is valid and has not expired.
      #
      # @param token [String]
      # @return [Models::ApiResponse]
      def verify_jwt(token:)
        raw = @http.post("/v1/user/verify-jwt", body: { token: token })
        Models::ApiResponse.from_hash(raw)
      end
    end
  end
end
