module Cleanster
  module Models
    # Represents a Cleanster end-user account (e.g. from GET /v1/user/access-token/:id).
    class User
      attr_reader :id, :email, :first_name, :last_name, :phone, :token, :raw

      def initialize(data)
        @raw        = data
        @id         = data["id"]
        @email      = data["email"]
        @first_name = data["firstName"]
        @last_name  = data["lastName"]
        @phone      = data["phone"]
        @token      = data["token"]
      end

      def to_s
        "User(id=#{id}, email=#{email.inspect})"
      end
    end

    # Response from POST /v1/user/account.
    #
    # Confirmed against the live sandbox API: creating a user does NOT return a full
    # user profile - only the new Cleanster user ID and a per-user JWT. +access_token+
    # is already prefixed with "Bearer "; use +access_token_without_prefix+ if your
    # HTTP layer adds that prefix itself.
    class CreateUserResponse
      attr_reader :user_id, :access_token, :raw

      def initialize(data)
        @raw          = data
        @user_id      = data["userId"]
        @access_token = data["accessToken"]
      end

      def access_token_without_prefix
        return nil unless access_token
        prefix = "Bearer "
        access_token.start_with?(prefix) ? access_token[prefix.length..] : access_token
      end

      def to_s
        "CreateUserResponse(user_id=#{user_id})"
      end
    end
  end
end
