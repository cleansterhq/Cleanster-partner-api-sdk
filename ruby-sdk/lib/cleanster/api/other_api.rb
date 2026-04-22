module Cleanster
  module Api
    # OtherApi — utility endpoints for services, plans, cost estimates, extras, and coupons.
    class OtherApi
      def initialize(http)
        @http = http
      end

      # Return all available cleaning service types.
      def get_services
        raw = @http.get("/v1/services")
        Models::ApiResponse.from_hash(raw)
      end

      # Return available booking plans for a given property.
      #
      # @param property_id [Integer]
      # @return [Models::ApiResponse]
      def get_plans(property_id)
        raw = @http.get("/v1/plans", params: { propertyId: property_id })
        Models::ApiResponse.from_hash(raw)
      end

      # Get the system-recommended cleaning hours based on property size.
      #
      # @param property_id    [Integer]
      # @param bathroom_count [Integer]
      # @param room_count     [Integer]
      # @return [Models::ApiResponse]
      def get_recommended_hours(property_id, bathroom_count:, room_count:)
        raw = @http.get("/v1/recommended-hours",
                        params: { propertyId: property_id, bathroomCount: bathroom_count, roomCount: room_count })
        Models::ApiResponse.from_hash(raw)
      end

      # Calculate the estimated cost for a potential booking.
      #
      # @param request [Hash] property_id, plan_id, hours, and optionally coupon_code.
      # @return [Models::ApiResponse]
      def calculate_cost(request)
        raw = @http.post("/v1/cost-estimate", body: camel_keys(request))
        Models::ApiResponse.from_hash(raw)
      end

      # Get available add-on services for a given service type.
      #
      # @param service_id [Integer]
      # @return [Models::ApiResponse]
      def get_cleaning_extras(service_id)
        raw = @http.get("/v1/cleaning-extras/#{service_id}")
        Models::ApiResponse.from_hash(raw)
      end

      # Find cleaners available for a specific property, date, and time.
      #
      # @param request [Hash] property_id, date, time.
      # @return [Models::ApiResponse]
      def get_available_cleaners(request)
        raw = @http.post("/v1/available-cleaners", body: camel_keys(request))
        Models::ApiResponse.from_hash(raw)
      end

      # Return all valid coupon codes.
      def get_coupons
        raw = @http.get("/v1/coupons")
        Models::ApiResponse.from_hash(raw)
      end

      private

      def camel_keys(hash)
        map = {
          property_id:  "propertyId",
          plan_id:      "planId",
          coupon_code:  "couponCode"
        }
        hash.transform_keys { |k| map[k] || k }
      end
    end
  end
end
