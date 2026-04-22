module Cleanster
  module Models
    # Wraps every Cleanster API response.
    #
    # @attr_reader status  [Integer] HTTP-style status code (e.g., 200).
    # @attr_reader message [String]  Human-readable status message (e.g., "OK").
    # @attr_reader data    [Object]  The parsed response payload.
    class ApiResponse
      attr_reader :status, :message, :data

      def initialize(status:, message:, data:)
        @status  = status
        @message = message
        @data    = data
      end

      def self.from_hash(hash, model_class: nil)
        raw_data = hash["data"]
        data = if model_class && raw_data.is_a?(Hash)
          model_class.new(raw_data)
        elsif model_class && raw_data.is_a?(Array)
          raw_data.map { |item| item.is_a?(Hash) ? model_class.new(item) : item }
        else
          raw_data
        end

        new(
          status:  hash["status"],
          message: hash["message"],
          data:    data
        )
      end

      def to_s
        "ApiResponse(status=#{status}, message=#{message.inspect})"
      end
    end
  end
end
