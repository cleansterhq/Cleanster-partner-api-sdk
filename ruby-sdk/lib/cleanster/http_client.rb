require "net/http"
require "uri"
require "json"

module Cleanster
  # Internal HTTP transport layer.
  # Wraps Net::HTTP, attaches auth headers, and maps HTTP errors to SDK exceptions.
  class HttpClient
    attr_accessor :bearer_token

    def initialize(config)
      @config       = config
      @bearer_token = nil
    end

    def get(path, params: nil)
      uri = build_uri(path, params)
      request = Net::HTTP::Get.new(uri)
      attach_headers(request)
      perform(uri, request)
    end

    def post(path, body: nil)
      uri = build_uri(path)
      request = Net::HTTP::Post.new(uri)
      attach_headers(request)
      request.body = body.nil? ? "" : body.to_json
      perform(uri, request)
    end

    def put(path, body: nil)
      uri = build_uri(path)
      request = Net::HTTP::Put.new(uri)
      attach_headers(request)
      request.body = body.nil? ? "" : body.to_json
      perform(uri, request)
    end

    def delete(path, body: nil)
      uri = build_uri(path)
      request = Net::HTTP::Delete.new(uri)
      attach_headers(request)
      request.body = body.nil? ? "" : body.to_json
      perform(uri, request)
    end

    private

    def build_uri(path, params = nil)
      uri = URI.parse(@config.base_url + path)
      if params
        filtered = params.compact
        uri.query = URI.encode_www_form(filtered) unless filtered.empty?
      end
      uri
    end

    def attach_headers(request)
      request["Content-Type"] = "application/json"
      request["access-key"]   = @config.access_key
      request["token"]        = @bearer_token.to_s
    end

    def perform(uri, request)
      Net::HTTP.start(uri.host, uri.port,
                      use_ssl:      uri.scheme == "https",
                      open_timeout: @config.open_timeout,
                      read_timeout: @config.read_timeout) do |http|
        response = http.request(request)
        handle_response(response)
      end
    rescue Cleanster::CleansterError
      raise
    rescue => e
      raise Cleanster::CleansterError, "Network error: #{e.message}"
    end

    def handle_response(response)
      code = response.code.to_i
      body = response.body.to_s

      case code
      when 401
        raise Cleanster::AuthError.new(response_body: body)
      when 200..299
        begin
          JSON.parse(body)
        rescue JSON::ParserError => e
          raise Cleanster::CleansterError, "Failed to parse JSON response: #{e.message}"
        end
      else
        raise Cleanster::ApiError.new(code, "API request failed with status #{code}", response_body: body)
      end
    end
  end
end
