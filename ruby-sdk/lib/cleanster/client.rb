require_relative "http_client"
require_relative "api/bookings_api"
require_relative "api/users_api"
require_relative "api/properties_api"
require_relative "api/checklists_api"
require_relative "api/other_api"
require_relative "api/blacklist_api"
require_relative "api/payment_methods_api"
require_relative "api/webhooks_api"

module Cleanster
  # CleansterClient is the main entry point for the Cleanster Partner API SDK.
  #
  # @example Sandbox (for development and testing)
  #   client = Cleanster::Client.sandbox("your-access-key")
  #
  # @example Production (for live use)
  #   client = Cleanster::Client.production("your-access-key")
  #
  # @example With builder for custom configuration
  #   config = Cleanster::Config.builder("your-access-key")
  #     .production
  #     .read_timeout(60)
  #     .build
  #   client = Cleanster::Client.new(config)
  #
  # @example Authenticate a user and make API calls
  #   response = client.users.fetch_access_token(user_id)
  #   client.access_token = response.data.token
  #   booking = client.bookings.get_booking_details(16926).data
  class Client
    attr_reader :bookings, :users, :properties, :checklists,
                :other, :blacklist, :payment_methods, :webhooks

    def initialize(config)
      @http             = HttpClient.new(config)
      @bookings         = Api::BookingsApi.new(@http)
      @users            = Api::UsersApi.new(@http)
      @properties       = Api::PropertiesApi.new(@http)
      @checklists       = Api::ChecklistsApi.new(@http)
      @other            = Api::OtherApi.new(@http)
      @blacklist        = Api::BlacklistApi.new(@http)
      @payment_methods  = Api::PaymentMethodsApi.new(@http)
      @webhooks         = Api::WebhooksApi.new(@http)
    end

    # Create a client configured for the sandbox environment.
    # @param access_key [String] Your partner access key.
    # @return [Client]
    def self.sandbox(access_key)
      new(Config.sandbox(access_key))
    end

    # Create a client configured for the production environment.
    # @param access_key [String] Your partner access key.
    # @return [Client]
    def self.production(access_key)
      new(Config.production(access_key))
    end

    # Set the user bearer token sent as the `token` header on every request.
    #
    # @param token [String, nil] The bearer token from users.fetch_access_token.
    def access_token=(token)
      @http.bearer_token = token
    end

    # Return the currently active user bearer token, or nil if not set.
    # @return [String, nil]
    def access_token
      @http.bearer_token
    end
  end
end
