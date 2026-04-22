module Cleanster
  # Base error class for all Cleanster SDK errors.
  # Raised for network failures, timeouts, and unexpected SDK-level issues.
  class CleansterError < StandardError; end

  # Raised when the API returns HTTP 401 Unauthorized.
  # Indicates an invalid or missing access key or user token.
  class AuthError < CleansterError
    attr_reader :status_code, :response_body

    def initialize(message = "Unauthorized — invalid or missing access key or user token.", response_body: "")
      super(message)
      @status_code   = 401
      @response_body = response_body
    end
  end

  # Raised when the API returns a non-2xx HTTP response (other than 401).
  # Contains the HTTP status code and raw response body for debugging.
  class ApiError < CleansterError
    attr_reader :status_code, :response_body

    def initialize(status_code, message, response_body: "")
      super(message)
      @status_code   = status_code
      @response_body = response_body
    end
  end
end
