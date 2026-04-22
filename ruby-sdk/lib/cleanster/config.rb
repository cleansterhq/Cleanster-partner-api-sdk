module Cleanster
  SANDBOX_BASE_URL    = "https://partner-sandbox-dot-official-tidyio-project.ue.r.appspot.com/public".freeze
  PRODUCTION_BASE_URL = "https://partner-dot-official-tidyio-project.ue.r.appspot.com/public".freeze

  # Holds all configuration for a CleansterClient instance.
  #
  # @example Sandbox (for development)
  #   config = Cleanster::Config.sandbox("your-key")
  #
  # @example Production (for live use)
  #   config = Cleanster::Config.production("your-key")
  #
  # @example Builder pattern
  #   config = Cleanster::Config.builder("your-key")
  #     .production
  #     .open_timeout(10)
  #     .read_timeout(60)
  #     .build
  class Config
    attr_reader :access_key, :base_url, :open_timeout, :read_timeout

    def initialize(access_key:, base_url: SANDBOX_BASE_URL, open_timeout: 10, read_timeout: 30)
      raise ArgumentError, "access_key must not be nil or blank." if access_key.nil? || access_key.strip.empty?

      @access_key   = access_key
      @base_url     = base_url.chomp("/")
      @open_timeout = open_timeout
      @read_timeout = read_timeout
    end

    # Create a config pointing to the sandbox environment.
    def self.sandbox(access_key)
      new(access_key: access_key, base_url: SANDBOX_BASE_URL)
    end

    # Create a config pointing to the production environment.
    def self.production(access_key)
      new(access_key: access_key, base_url: PRODUCTION_BASE_URL)
    end

    # Return a Builder for custom configuration.
    def self.builder(access_key)
      Builder.new(access_key)
    end

    # Fluent builder for Config.
    class Builder
      def initialize(access_key)
        @access_key   = access_key
        @base_url     = SANDBOX_BASE_URL
        @open_timeout = 10
        @read_timeout = 30
      end

      def sandbox
        @base_url = SANDBOX_BASE_URL
        self
      end

      def production
        @base_url = PRODUCTION_BASE_URL
        self
      end

      def base_url(url)
        @base_url = url
        self
      end

      def open_timeout(seconds)
        @open_timeout = seconds
        self
      end

      def read_timeout(seconds)
        @read_timeout = seconds
        self
      end

      def build
        Config.new(
          access_key:   @access_key,
          base_url:     @base_url,
          open_timeout: @open_timeout,
          read_timeout: @read_timeout
        )
      end
    end
  end
end
