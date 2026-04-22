module Cleanster
  module Api
    # BlacklistApi — prevent specific cleaners from being assigned to your bookings.
    class BlacklistApi
      def initialize(http)
        @http = http
      end

      # Return all cleaners currently on the blacklist.
      # @return [Models::ApiResponse]
      def list_blacklisted_cleaners
        raw = @http.get("/v1/blacklist/cleaner")
        Models::ApiResponse.from_hash(raw)
      end

      # Add a cleaner to the blacklist.
      #
      # @param cleaner_id [Integer]
      # @param reason     [String, nil] Optional reason.
      # @return [Models::ApiResponse]
      def add_to_blacklist(cleaner_id:, reason: nil)
        body = { cleanerId: cleaner_id }
        body[:reason] = reason if reason
        raw = @http.post("/v1/blacklist/cleaner", body: body)
        Models::ApiResponse.from_hash(raw)
      end

      # Remove a cleaner from the blacklist.
      #
      # @param cleaner_id [Integer]
      # @return [Models::ApiResponse]
      def remove_from_blacklist(cleaner_id:)
        raw = @http.delete("/v1/blacklist/cleaner", body: { cleanerId: cleaner_id })
        Models::ApiResponse.from_hash(raw)
      end
    end
  end
end
